function [row col val] = find(A)
%find: Converts an associative array in to triples.
%Associative array user function.
%  Usage:
%    [row col val] = find(A)
%  Inputs:
%    A = associative array
%  Outputs:
%    row = list of N row strings or Nx1 numeric index vector
%    col = list of N column strings or Nx1 numeric index vector
%    col = list of N value strings or Nx1 numeric vector

  % Get indices.
  [row, col, val] = find(A.A);

  % Fix context bug in find.
  n = size(row,1);
  if n == 1
    row = row.';
  end

  n = size(col,1);
  if n == 1
    col = col.';
  end

  n = size(val,1);
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
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

