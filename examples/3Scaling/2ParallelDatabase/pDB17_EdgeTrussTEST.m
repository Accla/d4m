%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute k-Truss subgraph of incidence Assoc.
% 1) Select edges of interest.
% 2) Construct incidence Assoc from edges of interest in each file from pDB03_AssocTEST.
% 3) Compute k-Truss subgraph of incidence Assoc.
% 4) Visualize edges in k-Truss and edges deleted to form it.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite: pDB03_AssocTEST
echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nfile = 8;                                       % Set the number of files to load from.

myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));   % PARALLEL.

% Select Nv0 nodes of interest (ignoring duplicates).
Nv0 = 5000;
v0 = ceil(10000.*rand(1,Nv0));      % Nodes of interest.
v0str = num2str(v0,'%d,');          % Form column subset string.
v0subset = [CatStr('In,','/',v0str) CatStr('Out,','/',v0str)];

Eall = Assoc('','','');  % Gather edges in our target subset into Eall.
for i = myFiles
  tic;
    fname = ['data/' num2str(i)];  disp(fname); % Create filename.
    load([fname '.E.mat']);                     % Read saved Edge Assoc.
    
    Esubset = E(:,v0subset);
    % Remove edges that do not have both ends in our subset
    Esubset = Esubset(Row(sum(dblLogi(Esubset),2) == 2) ,:);
    
    Eall = Eall + Esubset;
  readTime = toc;  disp(['Read&Subset Time: ' num2str(readTime)])
end
E = Eall;

% Convert to undirected incidence matrix.
[~,labeledCol] = SplitStr(Col(E),'/');
E = reAssoc(putCol(E,labeledCol));

% Remove self-edges.
E = E(Row(sum(dblLogi(E),2) == 2) ,:);

% Make unweighted.
E = dblLogi(E);

% Note: by construction of pDB03_AssocTEST, there are no duplicate rows. Every edge row is unique.
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

% Visualize difference between E and Etruss
% Color Etruss part blue (the edges in the k-truss)
% Color E-Etruss part red (for deleted edges; edges in E but not in Etruss)
markEtrussonly = '''.'''; markEonly = '''.r'''; 
Etmp = putVal(num2str(E+E - Etruss),[markEtrussonly ',' markEonly ',']);
spy(Etmp,Etmp);                             % use values as plot linespec
xlabel('edge'); ylabel('vertex');
title(sprintf('Blue are edges in %d-Truss; Red are deleted edges from E',ktruss));

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%