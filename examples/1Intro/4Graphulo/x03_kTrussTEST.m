% Basic k-Truss Demo
k = 3;
Emat = [1 1 0 0 0;
     0 1 1 0 0;
     1 0 0 1 0;
     0 0 1 1 0;
     1 0 1 0 0;
     0 1 0 0 1];
E = Mat2Assoc(Emat,'e','v'); % Convert to Assoc, use node labels 'v1,v2,...,'
displayFull(E)               %    Use edge labels 'e1,e2,...,'
Etruss = kTrussEdge(E,3);             % Compute 3-Truss subgraph.
displayFull(Etruss)
disp('')

Amat = [0 1 1 1 0; % Input adjacency matrix
     1 0 1 0 1;
     1 1 0 1 0; 
     1 0 1 0 0;
     0 1 0 0 0];
A = Mat2Assoc(Amat,'v'); % Convert to Assoc, use 'v1,v2,...,' as labels.
displayFull(A)
Atruss = kTrussAdj(A,k);
if isempty(Atruss)
    fprintf('%d-Truss is empty\n',k);
else
    displayFull(Atruss)
end

% Visualize difference between A and Atruss
% Color Atruss part blue (the edges in the k-truss)
% Color A-Atruss part red (for deleted edges; edges in A but not in Atruss)
markAtrussonly = '''.'''; markAonly = '''.r'''; 
Atmp = putVal(num2str(A+A - Atruss),[markAtrussonly ',' markAonly ',']);
spy(Atmp,Atmp);                             % use values as plot linespec
xlabel('edge vertex'); ylabel('edge vertex');
title(sprintf('Blue are edges in %d-Truss; Red are deleted edges from A',k));
