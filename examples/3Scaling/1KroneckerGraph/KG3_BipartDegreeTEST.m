%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Degree distributions of connected and unconnected bipartite graphs.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')             % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
n = 1; m = 5; k = 4;                % Set size and exponent.

B = bipartite(n,m);                 % Create bipartite graph.

B4 = kron(kron(kron(B,B),B),B);     % Exponentiate.

% Measure vertex degree distributions.
[i j v] = find(sum(B4,2));
B4deg = sum(sparse(i,v,1),1);

% Compute theoretical degree distribution (Kepner & Gilbert 2011, Chapter 10).
for r=0:k; Cnk(r+1) = nchoosek(k,r); end     % Compute binomial coefficent.
r = 0:k;
B4degX = (n.^r).*(m.^(k-r));  B4degY = Cnk.*(n.^(k-r)).* (m.^r);


% Plot measured and theoretical.
figure;
loglog(full(B4deg),'o'); hold('on'); loglog(B4degX,B4degY,'-'); hold('off');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
