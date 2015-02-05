%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute k-Truss subgraph of incidence Assoc.
% 1) Select nodes of interest.
% 2) Construct incidence Assoc from all edges between nodes of interest.
% 3) Compute k-Truss subgraph of incidence Assoc.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite: pDB02_FileTEST
echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nfile = 8;                                       % Set the number of files to load from.

myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));   % PARALLEL.

% Select Nv0 nodes of interest (ignoring duplicates).
Nv0 = 3000;
v0 = ceil(10000.*rand(1,Nv0));              % Nodes of interest.

% Read in start and end nodes of edges of interest, from each file.
startMat = []; endMat = [];
for i = myFiles
  tic;
    fname = ['data/' num2str(i)];  disp(fname); % Create filename.
    fileRowMat = csvread([fname 'r.txt']);      % Read file data as integers.
    fileColMat = csvread([fname 'c.txt']);
    % (val file not used; we assume an unweighted, undirected graph)
    
    % Select only edges that are both to and from a node of interest.
    subgraphIdx = ismember(fileRowMat,v0) & ismember(fileColMat,v0);
    % Append edges of interest: start node and end node.
    startMat = [startMat fileRowMat(subgraphIdx)];
    endMat = [endMat fileColMat(subgraphIdx)];
  readTime = toc;  disp(['Read Time: ' num2str(readTime)])
end

tic;
Emat = EdgeListToMat(startMat,endMat); % Construct sparse incidence matrix.
E = Mat2Assoc(Emat,'','',true,false);  % Convert to Assoc, zero-pad edge labels.
makeTime = toc; fprintf('Incidence Assoc construct time: %f\n',makeTime)
fprintf('The original incidence-Assoc has %d edges and %d nodes.\n', ...
    NumStr(Row(E)),NumStr(Col(E)))

ktruss = 3;                              % Find 3-truss
tic;
Etruss = kTrussEdge(E,ktruss);
trussTime = toc; fprintf('%d-Truss calculation time: %f\n',ktruss,trussTime)

if isempty(Etruss)
    fprintf('The %d-Truss sub-incidence-Assoc is empty.\n',ktruss)
else
    fprintf('The %d-Truss sub-incidence-Assoc has %d edges and %d nodes.\n', ...
        ktruss,NumStr(Row(Etruss)),NumStr(Col(Etruss)))
    if NumStr(Row(Etruss)) < 15     % Don't display Etruss if it is too big.
        displayFull(Etruss)
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

