function J = Jaccard(A)
% Compute Jaccard coefficients in upper triangle (no diagonal) 
% of unweighted, undirected adjacency matrix with no self-edges. 
% Ex: Jaccard(Mat2Assoc([0,1,1;1,0,1;1,1,0])
%          == Mat2Assoc([0,1/3,1/3;0,0,1/3;0,0,0])

A = Abs0(A+A.');                    % Enforce unweighted, undirected.
d = sum(A,1);                       % Degree row vector.

strictTriu = @(a) reAssoc(putAdj(a, triu(Adj(a),1))); 
U = strictTriu(A);                  % Take strict upper triangle.
% Initialize Jaccard to upper triangle of A^2.
% Math: triu(A^2) = U^2 + U.'*U + U*U.'
% Note: sqOut(U)=U*U.';  sqIn(U)=U.'*U;  efficiently
J = U*U + strictTriu(sqOut(U)) + strictTriu(sqIn(U));
%J = NoDiag(J); % By above line, J has no diagonal.
if isempty(J)  % short circuit all-zero Jaccard coefficients
    return
end

% for each nonzero Jij do Jij := Jij ./ (di + dj - Jij)
[r,c,v] = find(J);
[~,dc,dv] = find(d);
x1 = StrSearch(dc,c);
x2 = StrSearch(dc,r);
vnew = v ./ (dv(x1) + dv(x2) - v);
J = Assoc(r,c,vnew); % might do faster by putAdj impl.
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
