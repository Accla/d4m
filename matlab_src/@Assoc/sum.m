function B = sum(A,dim)
%sum: Performs sum along the dimension of an associative array with numeric values.
%Associative array user function.
%  Usage:
%    B = sum(A,dim)
%  Inputs:
%    A = associative array
%    dim = dimension to sum along; 1 = rows, 2 = columns
%  Outputs:
%    B =  Vector associative array holding the sum along the specified dimension.

  B = A;
  if not(isempty(B.val))
    B.val = '';
    B.A = logical(B.A);
  end

  B.A = sum(B.A,dim);

  if (dim == 1);
    B.row = '';
  end  
  if (dim == 2);
    B.col = '';
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

