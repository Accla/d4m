function AB = mtimes(A,B)
%MTIMES performance matrix multiply on two associative arrays.
  % Matrix multiply.
  % Eliminates val on output.

  [NA MA] = size(A.A);
  [NB MB] = size(B.A);

  AB = A;

  AB.col = B.col;

  AB.val = '';

  if not(isempty(A.val))
    A = logical(A);
  end

  if not(isempty(B.val))
    B = logical(B);
  end

  if (isempty(A.col) | isempty(B.row))
    jA = (1:min(MA,NB)).';
    iB = (1:min(MA,NB)).';
  else
    jA = StrSubsref(A.col,B.row);
    iB = StrSubsref(B.row,A.col);
  end

  AB.A = double(A.A(:,jA)) * double(B.A(iB,:));

  % Repeat to clean up eliminated entries.
  [ABrow ABcol ABval] = find(AB);
%disp('Here 1');
%whos
  AB = Assoc(ABrow,ABcol,ABval);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

