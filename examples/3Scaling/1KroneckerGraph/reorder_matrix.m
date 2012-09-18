function[result] = reorder_matrix(A,iv)
% Reorder matrix.

[i j v] = find(A);

result = sparse(iv(i),iv(j),v);
