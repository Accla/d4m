%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Adjacency Assoc Breadth First Search
% Load A from .mat files and do BFS from a subset of starting nodes.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite if DoDB=false: pDB03_AssocTEST
% Prerequisite if DoDB=true : pDB06_AdjInsertTEST
DoDB = false;
echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nv0 = 100;
v0 = ceil(10000.*rand(Nv0,1));              % Create a starting set of vertices.

myV = 1:numel(v0);
%myV = global_ind(zeros(numel(v0),1,map([Np 1],{},0:Np-1)));    % PARALLEL.

v0str = sprintf('%d,',v0(myV));             % Convert to string list.

k = 3;                % BFS k steps away;
dmin = 5; dmax = 15;  % Degree filter.
Ak = Assoc('','',''); % Initialize.

if DoDB
    DBsetup;                          % Create binding to database.
    tic;
        Ak = AdjBFS(Tadj,TadjDeg,'OutDeg,',v0str,k,dmin,dmax);
    getTime = toc; disp(['BFS Time: ' num2str(getTime) ])
else
    % Load Adj matrix from Assoc
    Nfile = 8;                        % Set the number of files to save to.
    myFiles = 1:Nfile;
    
    for i = myFiles         % Run BFS on each part of the adjacency matrix.
        tic;
            fname = ['data/' num2str(i)];  disp(fname);  % Create filename.
            load([fname '.mat']);                        % Load associative array.
            Adeg = sum(A,2);                             % Compute out-degrees.
            Ak = Ak + AdjBFS(A,Adeg,'',v0str,k,dmin,dmax);  % Combine results of BFS.
        sumTime = toc; disp(['Sum Time: ' num2str(sumTime) ])%', Edges/sec: ' num2str(0.5.*(nnz(Adj(Aout))+nnz(Adj(Ain)))./sumTime)]);
    end
end

%Ak = gagg(Ak);                               % PARALLEL.

fprintf('Starting from %d nodes, # of nodes in exactly %d steps\n  (only traversing nodes with %d <= degree <= %d): %d\n', ...
    nnz(myV),k,dmin,dmax,NumStr(Col(Ak)));

echo('off'); figure; spy(Ak); xlabel('end vertex'); ylabel('start vertex');  % Show.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

