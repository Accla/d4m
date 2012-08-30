function AB = mtimes(A,B)
%*,mtimes: Performs matrix multiply of two associative arrays.
%Associative array user function.
%  Usage:
%    AB = A * B
%    AB = mtimes(A,B)
%  Inputs:
%    A = associative array; string values are converted to 1
%    B = associative array; string values are converted to 1
%  Outputs:
%    AB = associative array that is the matrix multiply of A and B limited to the intersection of Col(A) and Row(B)

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

  if (isempty(A.col) || isempty(B.row))
    jA = (1:min(MA,NB)).';
    iB = (1:min(MA,NB)).';
  else
    jA = StrSubsref(A.col,B.row);
    iB = StrSubsref(B.row,A.col);
  end

  AB.A = double(A.A(:,jA)) * double(B.A(iB,:));

  iAB = find(sum(AB.A,2));
  jAB = find(sum(AB.A,1));

  if (nnz(iAB) & nnz(jAB))
    AB.A = AB.A(iAB,jAB);
    if (not(isempty(AB.row)))
      ABrowMat = Str2mat(AB.row);
      AB.row = Mat2str(ABrowMat(iAB,:));
    end
    if (not(isempty(AB.col)))
      ABcolMat = Str2mat(AB.col);
      AB.col = Mat2str(ABcolMat(jAB,:));
    end
  else
    AB = Assoc('','','');
  end

  % Repeat to clean up eliminated entries.
%  [ABrow ABcol ABval] = find(AB);
%  AB = Assoc(ABrow,ABcol,ABval);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

