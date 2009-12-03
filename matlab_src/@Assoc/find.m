function [row col val] = find(A);
%FIND  Returns triples of non-zero entries of an associative array.
  % Get indices.
  [row col val] = find(A.A);

  % Fix context bug in find.
  [n m] = size(row);
  if n == 1
    row = row.';
  end

  [n m] = size(col);
  if n == 1
    col = col.';
  end

  [n m] = size(val);
  if n == 1
    val = val.';
  end

  if not(isempty(A.row))
    rowMat = Str2mat(A.row);
    row = Mat2str(rowMat(row,:));
  end  

  if not(isempty(A.col))
    colMat = Str2mat(A.col);
    col = Mat2str(colMat(col,:));
  end  

  if not(isempty(A.val))
    valMat = Str2mat(A.val);
    val = Mat2str(valMat(val,:));
  end  

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%