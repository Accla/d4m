util_Require('DB, G, TNadjUU, triangles');

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUU ' ']) < 1
    error(['Lost the original table: ' TadjUU]);
end
TadjUU = DB(TNadjUU);

%%%%% Verification
% The following script can verify the output of Graphulo.
% It must be run *before* Graphulo runs, because Graphulo filters entries to the upper triangle.

A = str2num(TadjUU(:,:));
As = Adj(Abs0(A+A.'));
As(logical(speye(size(A)))) = 0; % NoDiag.
L = tril(As,-1);
U = triu(As,1);
B = tril(L * U);
C = B .* As;
trianglesD4M = full(sum(sum(C,1),2));
fprintf('Triangles    : %d\n', triangles)
fprintf('Triangles D4M: %d\n', trianglesD4M)


if trianglesD4M ~= triangles
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end
clear A As L U B C
