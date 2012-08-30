function B = min(A,C,dim)
%min: DEPRECATED. Compares an associative array with a scalar along a specified dimension.
%Associative array user function.
%  Usage:
%    B = min(A,C,dim)
%  Inputs:
%    A = associative array with numeric values
%    C = scalar numeric value
%    dim = dimension to perform max along; either 1 or 2
%  Outputs:
%    B = associative array with values that are the min of the inputs

  B = A;
%  if not(isempty(B.val))
%    B.val = '';
%    B.A = logical(B.A);
%  end

%  B.A = min(B.A,C,dim);
  X = full(B.A);    %!!! Could be costly.
  X(X == 0) = +Inf;
  B.A = sparse(min(X,C,dim));

  if (dim == 1);
    B.row = '';
  end  
  if (dim == 2);
    B.col = '';
  end

  [r c v] = find(B);
  B = Assoc(r,c,v);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

