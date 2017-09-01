%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Adjacency Assoc Breadth First Search
% Load A from .mat files and do BFS from a subset of starting nodes.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite if DoDB=false: pDB03_AssocTEST
% Prerequisite if DoDB=true : pDB06_AdjInsertTEST
DoDB = true;
echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nv0 = 10000;
v0 = ceil(500000.*rand(Nv0,1));              % Create a starting set of vertices.
%v0=(1:Nv0).';

myV = 1:numel(v0);
%%myV = global_ind(zeros(numel(v0),1,map([Np 1],{},0:Np-1)));    % PARALLEL.

v0str = StrUnique(sprintf('%d,',v0(myV)));             % Convert to string list.

kmax = 3;             % BFS k steps away;
dmin = 4; dmax = 20;  % Degree filter.
Ak = cell(kmax,1);    % Cell array to hold the subgraph at each step

if DoDB
    DBsetup;                          % Create binding to database.
    %G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
    
    rnameMat = [getName(Tadj) '_Mat_k' num2str(kmax)];
    TresMat = DB(rnameMat);
    deleteForce(TresMat);
    TresMat = DB(rnameMat);
    TresMat=addColCombiner(TresMat,':,','sum');
    
    rnameGra = [getName(Tadj) '_Gra_k' num2str(kmax)];
    TresGra = DB(rnameGra);
    deleteForce(TresGra);
    TresGra = DB(rnameGra);
    
    vk = v0str;
    getTimeTOT = 0;
    writeDBTimeTOT = 0;
    for k = 1:kmax
    
        tic;
        Ak = AdjBFS(Tadj,TadjDeg,'OutDeg,',vk,1,dmin,dmax,false); % no union
        getTime = toc; fprintf('BFS Time %f. Reached %d nodes in %d steps from %d starting nodes.\n', ...
                               getTime, NumStr(Col(Ak)), k, NumStr(vk));
        getTimeTOT = getTimeTOT + getTime;
        %figure; spy(Ak); xlabel('end vertex'); ylabel('start vertex'); title(['Adjacency BFS Step ' num2str(kmax)]);
        
        tic;
        put(TresMat,Ak);
        writeDBTime = toc; fprintf('Write subgraph to DB: %f\n',writeDBTime);
        writeDBTimeTOT = writeDBTimeTOT + writeDBTime;
        
        vk = Col(Ak);
    end
    fprintf('TOTAL getTime = %f\n',getTimeTOT);
    fprintf('TOTAL writeDBTime = %f\n',writeDBTimeTOT);
    fprintf('TOTAL D4M Time = %f\n',getTimeTOT+writeDBTimeTOT);
    
    tic;
    res = G.AdjBFS(getName(Tadj),v0str,kmax,rnameGra,'',getName(TadjDeg),'OutDeg',false,dmin,dmax);
    graphuloBFSTime = toc; fprintf('Graphulo BFS time: %f\n',graphuloBFSTime);
    
    res = char(res); % convert java.lang.String to MATLAB char
    Akcol = StrUnique(Col(Ak));
    resUni = StrUnique(res);
    [Akcol, resUni] = StrSepsame(Akcol, resUni);
    if ~isequal(Akcol,resUni)
        fprintf('NOT EQUAL D4M %d and GRAPHULO %d VERSIONS\n',NumStr(Akcol),NumStr(resUni));
    end
    
    % Verify full tables
    Rmat = TresMat(:,:);
    Rgra = TresGra(:,:);
    if ~isequal(Rmat,Rgra)
        fprintf('NOT EQUAL SUBGRAPHS D4M %d and GRAPHULO %d\n',nnz(Rmat),nnz(Rgra));
    end
    
else
    % Adj matrix is stored in pieces, one per file.
    % Go 1 step in BFS in each piece, then aggregate nodes reached for next step.
    Nfile = 8;                   % Set the number of files to save to.
    myFiles = 1:Nfile;
    % myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));    % PARALLEL.
    vstart = v0str;              % Nodes to search from.
    for k = 1:kmax
        Ak{k} = Assoc('','',''); % Initalize submatrix for k steps away.
        for fi = myFiles         % Run BFS on each part of the adjacency matrix.
            tic;
                fname = ['data/' num2str(fi)]; disp(fname);  % Filename.
                load([fname '.A.mat']);                      % Load associative array.
                Adeg = sum(A,2);                             % Compute out-degrees.
                Ak{k} = Ak{k} + AdjBFS(A,Adeg,'',vstart,1,dmin,dmax,false);  % Combine results of BFS.
            sumTime = toc; disp(['Sum Time: ' num2str(sumTime) ])%', Edges/sec: ' num2str(0.5.*(nnz(Adj(Aout))+nnz(Adj(Ain)))./sumTime)]);
        end
        %Ak{k} = gagg(Ak{k});          % PARALLEL.
        fprintf('Step %d: Starting from %d nodes (ignoring %d outside range %d <= out-degree <= %d),\n  reached %d nodes in exactly 1 step.\n', ...
                k, NumStr(vstart), NumStr(vstart)-NumStr(Row(Ak{k})), dmin,dmax, NumStr(Col(Ak{k})) );
        vstart = Col(Ak{k});     % Search next from nodes we reached in 1 step.
    end
    for k = 1:kmax
        figure; spy(Ak{k}); xlabel('end vertex'); ylabel('start vertex'); title(['Adjacency BFS Step ' num2str(k)]);
    end
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

