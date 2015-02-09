function Es = kTrussEdge(Es,k)
% Compute k-Truss subgraph of Undirected, Unweighted Incidence Assoc E.
if k < 3 || isempty(Es)  % short-circuit trivial cases; every graph is a 2-truss.
    return
end
Es = Abs0(Es);  % Make Unweighted, Undirected.
E = Adj(Es);    % Operate on sparse matrix, then reAssoc at the end.

% Note: sqIn(E) = E.'*E efficiently = the Adjacency Assoc.
% Theorem: We may safely use NoDiagNoAssoc on sqIn(E). Proof on paper.
%%R = E*NoDiagNoAssoc(sqIn(E)); % R = neighbors of each edge's start and end node.
                            % Value '2' means that an edge's start and end node 
                            % share a common neightbor, forming a triangle.
tmp = E.'*E;
R = E* (tmp-diag(diag(tmp)));
%%s = sum(Abs0(R==2),2);      % Compute # of triangles each edge is part of.
s = sum(double(R==2),2);
xc = s >= k-2;
%%%x = spfun(@(x) x < k-2,s)

% Compute edges that violate k-Truss: are part of < k-2 triangles
%   Case 1: Edge is part of 0 triangles.
%   Case 2: Edge is part of 0 < numTriangles < k-2.
%%x = [Row(logical(sum(E,2))-logical(s)), Row(s < k-2)];
% While edges exist violating k-Truss, delete those edges and take a subgraph.
while nnz(xc) ~= nnz(any(E,2)) %%~isempty(x)
    %Ex = E(x,:);              % Bad edges in incidence Assoc.
    %%xc = Row(s >= k-2);       % Complement of x. R(xc,:) faster than R - R(x,:).
    %%%E = E(xc,:);      %<-- This changes the dimension of the sparse matrix           % The rest of the incidence Assoc.
    E(not(xc),:) = 0;    %<-- Dimension does not change.
    %R = R(xc,:) - E * NoDiagNoAssoc(sqIn(Ex));  % Update edge node neighbors.
    %PROFILING: Recomputing "R = E*NoDiagNoAssoc(sqIn(E))" is faster than the above: removing the part of R that is taken away. 
    
    %%R = E*NoDiagNoAssoc(sqIn(E));               % Recompute R.
    tmp = E.'*E;
    R = E* (tmp-diag(diag(tmp)));
    %%s = sum(Abs0(R==2),2);                      % Update edge # of triangles.
    s = sum(double(R==2),2);
    xc = s >= k-2;
    %%x = [Row(logical(sum(E,2))-logical(s)), Row(s < k-2)];
end
Es = reAssoc(putAdj(Es,E));
end

function A = NoDiagNoAssoc(A)
% Same as NoDiag when ischar(Row(A)) && ischar(Col(A)),
% but does not call reAssoc. Use when no Row or Col entries are deleted as a
% result of removing the diagonal, e.g., when 
% ?r1?Row(A)?Col(A) ?r2?Row(A): (r1,r2,_)?A v (r2,r1,_)?A
AA = Adj(A);
A = putAdj(A,AA-diag(diag(AA)));
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%