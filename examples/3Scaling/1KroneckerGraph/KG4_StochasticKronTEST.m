%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Degree distributions of connected and unconnected bipartite graphs.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')              % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
n = 1; m = 3; k = 3;  EtoV = 8;      % Size, exponent, and edge/vertex ratio.

B = bipartite(n,m);                  % Create bipartite graph.
G = B + eye(n+m) + 0.1;              % Connect sub-graphs and add background.

G3 = kron(kron(G,G),G);              % Exponentiate.

[i j] = StochasticKronGraph(G,k,EtoV);   % Create instance.
A = sparse(i,j,1);                   % Create adjacency matrix.

figure;                              % Display probabilities.
subplot(1,2,1); imagesc(G3); axis('square');
subplot(1,2,2); spy(A);              % Display edges.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Graph Analysis Graph.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
k = 14;  EtoV = 8;                   % Exponent and edge/vertex ratio.
G = [0.55 0.1; 0.1 0.25];            % Generator. 

[i j] = StochasticKronGraph(G,k,EtoV);   % Create instance.
A = sparse(i,j,1);                   % Create adjacency matrix.

% Measure vertex degree distributions.
figure; loglog( full(OutDegree(A)) ,'o');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Graph 500 Graph.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
k = 14;  EtoV = 16;                   % Exponent and edge/vertex ratio.
G = [0.57 0.19; 0.19 0.05];           % Generator. 

[i j] = StochasticKronGraph(G,k,EtoV);   % Create instance.
A = sparse(i,j,1);                   % Create adjacency matrix.

% Measure vertex degree distributions.
figure; loglog( full(OutDegree(A)) ,'o');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
