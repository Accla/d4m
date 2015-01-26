%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Adjacency Assoc Breadth First Search
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite if DoDB=false: pDB03_AssocTEST
DoDB = false;

echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nv0 = 100;
v0 = ceil(10000.*rand(Nv0,1));              % Create a starting set of vertices.

myV = 1:numel(v0);
%myV = global_ind(zeros(numel(v0),1,map([Np 1],{},0:Np-1)));    % PARALLEL.

v0str = sprintf('%d,',v0(myV));             % Convert to string list.

k = 1;                % BFS 2 steps away;
dmin = 2; dmax = 15;  % Degree filter.
Ak = Assoc('','',''); % Initialize.

if DoDB
    %DBsetup;                          % Create binding to database.
    % Use Tadj DB table
else
    % Load Adj matrix from Assoc
    Nfile = 8;                        % Set the number of files to save to.
    myFiles = 1:Nfile;
    
    for i = myFiles   % Run BFS on each part of the adjacency matrix.
        tic;
            fname = ['data/' num2str(i)];  disp(fname);  % Create filename.
            load([fname '.mat']);                        % Load associative array.
            Ak = Ak + AdjBFS(A,v0str,k,dmin,dmax);       % Combine results of BFS.
            % Note: results may have
        sumTime = toc;  disp(['Sum Time: ' num2str(sumTime) ])%', Edges/sec: ' num2str(0.5.*(nnz(Adj(Aout))+nnz(Adj(Ain)))./sumTime)]);
    end
end

fprintf('Starting from %d nodes, # of nodes in %d steps with %d <= degree <= %d: %d\n', ...
    nnz(myV),k,dmin,dmax,NumStr(Col(Ak)));

%A = gagg(A);                               % PARALLEL.

echo('off'); figure; spy(Ak); xlabel('end vertex'); ylabel('start vertex');  % Show.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

