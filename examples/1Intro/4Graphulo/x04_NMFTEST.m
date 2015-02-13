% Basic NMF Demo
Emat = [1 1 0 0 0;
     0 1 1 0 0;
     1 0 0 1 0;
     0 0 1 1 0;
     1 0 1 0 0;
     0 1 0 0 1];
E = Mat2Assoc(Emat,'e','v'); % Convert to Assoc, use node labels 'v1,v2,...,'
displayFull(E)               %    Use edge labels 'e1,e2,...,'
k=3;
[W,H] = NMF(E,k);
displayFull(W)
displayFull(H)

Wmax = TopColPerRow(W,1);      % Display the best topic for each edge.
displayFull(Wmax)

Hmax = TopRowPerCol(H,1);      % Display the best topic for each node.
displayFull(Hmax.')




