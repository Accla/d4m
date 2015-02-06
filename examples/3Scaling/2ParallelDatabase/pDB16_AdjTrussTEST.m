%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute k-Truss subgraph of adjacency Assoc.
% Visualize k-Truss and edges deleted to form it.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite: pDB15_AdjJaccardTEST
DoDB = false;
echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Use A from previous script. 
fprintf('The original Adjacency-Assoc has %d edges and %d nodes.\n', ...
    nnz(A)/2,NumStr(Row(A)))

ktruss = 3;                              % Find 3-truss
tic;
Atruss = kTrussAdj(A,ktruss);
trussTime = toc; fprintf('%d-Truss calculation time: %f\n',ktruss,trussTime)

if isempty(Atruss)
    fprintf('The %d-Truss sub-Adjacency-Assoc is empty.\n',ktruss)
else
    fprintf('The %d-Truss sub-Adjacency-Assoc has %d edges and %d nodes.\n', ...
        ktruss,nnz(Atruss)/2,NumStr(Row(Atruss)))
    if NumStr(Row(Atruss)) < 15     % Don't display Atruss if it is too big.
        displayFull(Atruss)
    end
end

% Visualize difference between A and Atruss
% Color Atruss part blue (the edges in the k-truss)
% Color A-Atruss part red (for deleted edges; edges in A but not in Atruss)
markAtrussonly = '''.'''; markAonly = '''.r'''; 
Atmp = putVal(num2str(A+A - Atruss),[markAtrussonly ',' markAonly ',']);
spy(Atmp,Atmp);                             % use values as plot linespec
xlabel('edge vertex'); ylabel('edge vertex');
title(sprintf('Blue are edges in %d-Truss; Red are deleted edges from A',ktruss));

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%