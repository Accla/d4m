%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Insert graph data into an adjacency matrix.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                         % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

DB = DBsetupD4Muser;                             % Create binding to database.

Tadj = DB('TgraphAdj','TgraphAdjT');             % Bind to adjacency matrix table.
TadjDeg = DB('TgraphAdjDeg');                    % Bind to degree table.

Nfile = 8;                                       % Set the number of files to save to.

myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));   % Assign files to each processor.

rout = '';  cin = '';  dout = zeros(0);  din = zeros(0);     % Initialize arrays for tallying.
for i = myFiles
  tic;
    fname = ['data/' num2str(i)];  disp(fname);  % Create filename.

    load([fname '.mat']);                        % Load associative array.

    put(Tadj,num2str(A));                        % Insert associative array.

    Aout_i = putCol(num2str(sum(A,2)),'OutDeg,');   % Compute out degree.
    Ain_i = putCol(num2str(sum(A,2)),'InDeg,');     % Compute in degree.

    put(TadjDeg,Aout_i);                         % Accumulate out degrees.
    put(TadjDeg,Ain_i);                          % Accumulate ing degrees.
  insertTime = toc;  disp(['Time: ' num2str(insertTime) ', Edges/sec: ' num2str((nnz(A)+nnz(Aout_i)+nnz(Ain_i))./degreeTime)]); end

disp(['Table entries: ' num2str(nnz(Tadj))]);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

