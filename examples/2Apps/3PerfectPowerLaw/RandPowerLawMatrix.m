function A = RandPowerLawMatrix(alpha,dmax,Nd);
% RandPowerLawMatrix: Compute a randomized perfect power law matrix.
%  Usage:
%     A = PowerLawDist(alpha,dmax,Nd)
%  Input:
%     alpha = (negative) power law exponent
%     dmax = maximum vertex degree
%     Nd = approximate number of degree bins
%  Output:
%     A = adjacency matrix of graph

  [di ni] = PowerLawDist(alpha,dmax,Nd);    % Perfect power law degree distribution.

  N = sum(ni);                              % Number of vertices.
  M = sum(ni.*di);                          % Number of edges.
  disp(['Vertices: ' num2str(N) ', Edges: ' num2str(M) ', Ratio: ' num2str(M/N)]);

  v = EdgesFromDist(di,ni);                 % Create vertices from degree distribution.

  % Create edge and vertext permutations.
  eoutperm = randperm(numel(v));     einperm = randperm(numel(v));   % Randomize edge order.
  voutperm = randperm(max(v));       vinperm = randperm(max(v));     % Randome vertex label

  A = sparse(voutperm(v(eoutperm)), vinperm(v(einperm)),1);          % Randomize both.

return
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
