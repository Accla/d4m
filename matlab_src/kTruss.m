function E = kTruss(E,k)
% Compute k-Truss subgraph of Undirected Incidence Assoc E.
if k < 3 || isempty(E)% short-circuit trivial cases; every graph is a 2-truss.
    return
end
A = NoDiag(sqIn(E));  % Compute Adjacency Assoc (sqIn(E) = E.'*E efficiently).
% IMPL^ Can subtract out diagonal. Conjecture: Will not delete entries.
%       so no reAssoc necessary.
%   Also, no need to store A.
R = E*A;              % Compute neighbors of each edge's start and end node.
                      % Value '2' means that an edge's start and end node 
                      % share a common neightbor, forming a triangle.
s = sum(dblLogi(R==2),2);% Compute # of triangles each edge is part of.
% IMPL^ Can divide up above calculation if it takes long.

% Compute edges that violate k-Truss: are part of < k-2 triangles
%   Case 1: Edge is part of 0 triangles.
%   Case 2: Edge is part of 0 < numTriangles < k-2.
x = [Row(logical(sum(E,2))-logical(s)), Row(s < k-2)];
% While edges exist violating k-Truss, delete those edges and take a subgraph.
while ~isempty(x)
    Ex = E(x,:);    % Bad edges in incidence Assoc.
    E = E - Ex;     % The rest of the incidence Assoc.
    % IMPL^ E = E(Row(s >= k-2));
    R = R - R(x,:) - E * NoDiag(sqIn(Ex)); % Update edge node neighbors.
    s = sum(dblLogi(R==2),2); % Update edge # of triangles.
    x = [Row(logical(sum(E,2))-logical(s)), Row(s < k-2)];
end

end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%