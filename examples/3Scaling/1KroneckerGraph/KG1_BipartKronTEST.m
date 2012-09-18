%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Kronecker products of unconnected bipartite graphs.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')             % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
n = 2; m =4; k = 3;                 % Size and exponent.

% Compute permutations necessary for reconnecting bipartite sub-graphs.
[nu mu] = BipartiteIndexTree(k);
nki = n.^real(nu) .* m.^imag(nu);
mki = n.^real(mu) .* m.^imag(mu);
P_B2 = bipartite_kron_split(nki(1,1),mki(1,1),n,m);
P_B31 = bipartite_kron_split(nki(2,1),mki(2,1),n,m);
P_B32 = bipartite_kron_split(nki(2,2),mki(2,2),n,m);
P_B3 = [P_B31 (length(P_B31) + P_B32)];

B = bipartite(n,m);                 % Bipartite graph.

BB = kron(B,B);                     % Exponentiate.
BBr = reorder_matrix(BB,P_B2);      % Regroup sub-graphs.

BBB = kron(BB,B);                   % Exponentiate.
BBrB = kron(BBr,B);                 % Exponentiate.
BBBr = reorder_matrix(BBrB,P_B3);   % Regroup sub-graphs.

figure; spy(B);                     % Display
figure; subplot(1,2,1); spy(BB);  subplot(1,2,2); spy(BBr);
figure; subplot(1,2,1); spy(BBB); subplot(1,2,2); spy(BBBr);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
