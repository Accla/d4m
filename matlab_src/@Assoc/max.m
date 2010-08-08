function B = max(A,C,dim)
%MAX associative array along a dimension.
  B = A;
%  if not(isempty(B.val))
%    B.val = '';
%    B.A = logical(B.A);
%  end

  B.A = max(B.A,C,dim);

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

