function B = Equallike(arg1,arg2,relop)
%EQUALLIKE runs ==, >, <, ... operations on an associative array and a scalar
  % Does equal like elementwise operations
  % on an associative array and a scalal.


  % Set argument order.
  if IsClass(arg1,'Assoc')
    A = arg1;  v = arg2;
  else
    A = arg2;  v = arg1;
  end

  % Set default values.
  B = A;

  if isempty(A)  % Short circuit if nothing in A.
    return;
  end

  B.A(:) = 0;
  vnum = v;

  % If v is a string, find it in A.val.
  if ischar(v)
    vnum = StrSearch(A.val,v);
    if (vnum < 1)
      vnum = abs(vnum) + 0.5;
    end
  end

  % Find locations in A.A that satisfy relation.
  if IsClass(arg1,'Assoc')
    vind = find(relop(A.A,vnum));
  else
    vind = find(relop(vnum,A.A));
  end

  if isempty(vind)  % Short circuit if nothing will be in B.
    B.row = ''; B.col = ''; B.val = ''; B.A = [];
    return;
  end
  
  % Copy locations that satisfy relation.
  B.A(vind) = A.A(vind);

  % Get non-zero entries.
  [Brow Bcol Bval] = find(B);

  % Reconstruct answer.
  B = Assoc(Brow,Bcol,Bval);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

