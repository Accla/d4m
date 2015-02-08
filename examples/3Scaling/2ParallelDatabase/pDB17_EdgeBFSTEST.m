%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Incidence Assoc Breadth First Search
% Load E from .mat files and do BFS from a subset of starting nodes.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite if DoDB=false: pDB03_AssocTEST
% Prerequisite if DoDB=true : pDB10_EdgeInsertTEST
DoDB = true;
echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nv0 = 101;
v0 = ceil(10000.*rand(Nv0,1));              % Create a starting set of vertices.
%v0=(1:Nv0).';

myV = 1:numel(v0);
%%myV = global_ind(zeros(numel(v0),1,map([Np 1],{},0:Np-1)));    % PARALLEL.

v0str = StrUnique(sprintf('%d,',v0(myV)));  % Convert to string list; ensure unique nodes.

kmax = 3;             % BFS k steps away;
dmin = 5; dmax = 15;  % Degree filter.
Ek = cell(kmax,1);    % Cell array to hold the subgraph at each step

if DoDB
    DBsetup;                          % Create binding to database.
    tic;
        [vk,uk,Ek] = EdgeBFS(Tedge,'Out,','In,','|',TedgeDeg,v0str,kmax,dmin,dmax,true); % Take union of all nodes reached in k steps.
    getTime = toc; fprintf('BFS Time %f. Reached %d nodes through %d edges in up to %d steps from %d starting nodes.\n', ...
        getTime, NumStr(vk), NumStr(Row(Ek)), kmax, NumStr(v0str))
    figure; spy(Ek); xlabel('begin and end vertex'); ylabel('edge'); title(['Incidence BFS Step ' num2str(kmax)]);
else
    % Incidence Assoc is stored in pieces, one per file.
    % Go 1 step in BFS in each piece, then aggregate nodes reached for next step.
    Nfile = 8;                   % Set the number of files to save to.
    myFiles = 1:Nfile;
    % myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));    % PARALLEL.
    vstart = v0str;              % Nodes to search from.
    for k = 1:kmax
        vkall='';ukall='';Ekall=Assoc('','',''); % Initalize variables to hold BFS results from all files.
        for fi = myFiles            % Run BFS on each part of the adjacency matrix.
            tic;
                fname = ['data/' num2str(fi)]; disp(fname);  % Filename.
                load([fname '.E.mat']);                      % Load Incidence Assoc.
                Edeg = sum(E,1).';                           % Compute degrees.
                [vk,uk,Ek{k}] = EdgeBFS(E,'Out,','In,','|',Edeg,vstart,1,dmin,dmax,false);  % Combine results of BFS.
                vkall=StrUnique([vkall vk]); ukall=StrUnique([ukall uk]); % Accumulate results.
            sumTime = toc; disp(['BFS Time: ' num2str(sumTime) ])%', Edges/sec: ' num2str(0.5.*(nnz(Adj(Aout))+nnz(Adj(Ain)))./sumTime)]);
        end
        vk=vkall; uk=ukall;
        %vk = gagg(vkall);          % PARALLEL.
        fprintf(['Step %d: Starting from %d nodes (excluding %d nodes outside range %d <= out-degree <= %d),\n' ...
                '\ttraversed %d edges to reach %d nodes in exactly 1 step.\n'], ...
                k, NumStr(vstart), NumStr(vstart)-NumStr(uk), dmin,dmax, NumStr(Row(Ek{k})), NumStr(vk) );
        %Ek{k} is the subgraph containing all edges traversed in this BFS step.
        vstart = vk;     % Search next from nodes we reached in 1 step.
    end
    for k = 1:kmax
        figure; spy(Ek{k}); xlabel('begin and end vertex'); ylabel('edge'); title(['Incidence BFS Step ' num2str(k)]);
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