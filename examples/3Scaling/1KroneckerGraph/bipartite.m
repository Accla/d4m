function[result] = bipartite(n,m)
% Create an adjacency matrix for a  bipartite graph.

% Compute total vertices.
Nv = n + m;

result = spalloc(Nv,Nv,2*n*m);

result((n+1):Nv,1:n) = 1;
result(1:n,(n+1):Nv) = 1;
