function B = sum(A,dim)
%SUM associative array along a dimension.
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
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
