%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Adjacency Assoc Breadth First Search
% Load A from .mat files and do BFS from a subset of starting nodes.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite if DoDB=false: pDB03_AssocTEST
% Prerequisite if DoDB=true : pDB06_AdjInsertTEST
DoDB = false;
echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nv0 = 101;
v0 = ceil(10000.*rand(Nv0,1));              % Create a starting set of vertices.
%v0=(1:Nv0).';

myV = 1:numel(v0);
%%myV = global_ind(zeros(numel(v0),1,map([Np 1],{},0:Np-1)));    % PARALLEL.

v0str = StrUnique(sprintf('%d,',v0(myV)));             % Convert to string list.

kmax = 3;             % BFS k steps away;
dmin = 5; dmax = 15;  % Degree filter.
Ak = cell(kmax,1);    % Cell array to hold the subgraph at each step

if DoDB
    DBsetup;                          % Create binding to database.
    tic;
        Ak = AdjBFS(Tadj,TadjDeg,'OutDeg,',v0str,kmax,dmin,dmax,true); % Take union of all nodes reached in k steps.
    getTime = toc; disp(['BFS Time: ' num2str(getTime) ])
    figure; spy(Ak); xlabel('end vertex'); ylabel('start vertex'); title(['Adjacency BFS Step ' num2str(k)]);
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

