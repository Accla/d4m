function Js = Jaccard(As)
% Compute Jaccard coefficients in upper triangle (no diagonal) 
% of unweighted, undirected adjacency matrix with no self-edges. 
% Ex: Jaccard(Mat2Assoc([0,1,1;1,0,1;1,1,0])
%          == Mat2Assoc([0,1/3,1/3;0,0,1/3;0,0,0])

As = Abs0(As+As.');                    % Enforce unweighted, undirected.
A = Adj(As);
d = sum(A,2);                       % Degree column vector.

%%strictTriu = @(a) reAssoc(putAdj(a, triu(Adj(a),1))); 
%%U = strictTriu(A);                  % Take strict upper triangle.
A = triu(A,1);
% Initialize Jaccard to upper triangle of A^2.
% Math: triu(A^2) = U^2 + U.'*U + U*U.'
% Note: sqOut(U)=U*U.';  sqIn(U)=U.'*U;  efficiently
%%J = U*U + strictTriu(sqOut(U)) + strictTriu(sqIn(U));
J = A*A + triu(A*A.',1) + triu(A.'*A,1);
%J = NoDiag(J); % By above line, J has no diagonal.
if isempty(J)  % short circuit all-zero Jaccard coefficients
    return
end

% for each nonzero Jij do Jij := Jij ./ (di + dj - Jij)
[r,c,v] = find(J);
v = v ./ (d(r)+d(c)-v);
%%[dr,~,dv] = find(d);
%%x1 = StrSearch(dr,c);
%%x2 = StrSearch(dr,r);
%%vnew = v ./ (dv(x1) + dv(x2) - v);
%%J = Assoc(r,c,vnew); % might do faster by putAdj impl.

% Put back to Assoc, using labels from As
rowMat = Str2mat(Row(As));
r = Mat2str(rowMat(r,:));
colMat = Str2mat(Col(As));
c = Mat2str(colMat(c,:));
Js = Assoc(r,c,v);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
