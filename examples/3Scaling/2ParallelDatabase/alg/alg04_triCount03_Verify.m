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
As = Adj(A);
L = tril(As,-1);
U = triu(As,1);
B = L*U;
C = B(As & B);
trianglesD4M = sum(C)/2;
fprintf('Triangles D4M: %d%n', trianglesD4M)


if trianglesD4M ~= triangles
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end
clear A As L U B C
