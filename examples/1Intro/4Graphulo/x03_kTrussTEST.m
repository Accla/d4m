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
Etruss = kTruss(E,3);             % Compute 3-Truss subgraph.
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
displayFull(Atruss)

