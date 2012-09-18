%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Kronecker products of unconnected bipartite graphs.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')              % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
n = 2; m = 4; k = 2;                 % Size and exponent.

% Compute permutations necessary for reconnecting bipartite sub-graphs.
[nu mu] = BipartiteIndexTree(k);
nki = n.^real(nu) .* m.^imag(nu);
mki = n.^real(mu) .* m.^imag(mu);
P_B2 = bipartite_kron_split(nki(1,1),mki(1,1),n,m);

B = bipartite(n,m);                  % Bipartite graph.
G = B + eye(n+m);                    % Connect sub-graphs.

BB = kron(B,B);                      % Exponentiate.
GG = kron(G,G);                      % Exponentiate.
BBr = reorder_matrix(BB,P_B2);       % Regroup sub-graphs.
GGr = reorder_matrix(GG,P_B2);       % Regroup sub-graphs.

figure;                              % Display B and G.
spy(B); hold('on'); spy(G - B,'o'); hold('off');

figure;                              % Display BB and GG.
subplot(1,2,1); spy(BB); hold('on'); spy(GG - BB,'o'); hold('off');
subplot(1,2,2); spy(BBr); hold('on'); spy(GGr - BBr,'o'); hold('off');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%