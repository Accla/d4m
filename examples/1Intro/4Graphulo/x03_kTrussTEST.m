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
E = kTruss(E,3);             % Compute 3-Truss subgraph.
displayFull(E)
