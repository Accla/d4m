function AB = Pluslike(A,B,func)
%Pluslike: Performs element wise binary functions on two associative arrays.
%Associative array internal function used by + and -.
%  Usage:
%    B = Pluslike(arg1,arg2,relop)
%  Inputs:
%    arg1 = associative array or scalar numeric or string value
%    arg2 = associative array or scalar numeric or string value
%    relop = @plut, @minus
%  Outputs:
%    B = associative array of that satisfy the operation

  % Does plus like elementwise operations
  % on two associative arrays.
  % Collision function func is optional.

  if ( isempty(A) && isempty(B) )  % Short circuit if nothing in A && B.
    AB = A;  return;
  elseif ( not(isempty(A)) && isempty(B) )
    AB = A; return;
  elseif ( isempty(A) && not(isempty(B)) )
    AB = B; return;
  end

  if ( not(nnz(A)) && not(nnz(B)) )  % Short circuit if nothing in A && B.
    AB = A;  return;
  elseif ( nnz(A) && not(nnz(B)) )
    AB = A; return;
  elseif ( not(nnz(A)) && nnz(B) )
    AB = B; return;
  end

  % Get sizes.
  [NA MA] = size(A.A);
  [NB MB] = size(B.A);
  
  % Deal with mismatched case.
  % Truncate and match rows.
  if (not(isempty(A.row)) && isempty(B.row))
    A.A = A.A(1:min(NA,NB),:);
    B.A = B.A(1:min(NA,NB),:);
    B.row = A.row;
  elseif (isempty(A.row) && not(isempty(B.row)))
    A.A = A.A(1:min(NA,NB),:);
    B.A = B.A(1:min(NA,NB),:);
    A.row = B.row;
  end

  % Deal with mismatched case.
  % Truncate and match cols.
  if (not(isempty(A.col)) && isempty(B.col))
    A.A = A.A(:,1:min(MA,MB));
    B.A = B.A(:,1:min(MA,MB));
    B.col = A.col;
  elseif (isempty(A.col) && not(isempty(B.col)))
    A.A = A.A(:,1:min(MA,MB));
    B.A = B.A(:,1:min(MA,MB));
    A.col = B.col;
  end


  [Arow Acol Aval] = find(A);
  [Brow Bcol Bval] = find(B);

  % Convert separators.
  if (ischar(Arow) && ischar(Brow))
    [Arow Brow] = StrSepsame(Arow,Brow);
    ABrow = [Arow Brow];
  else
    ABrow = [Arow; Brow];
  end

  % Convert separators.
  if (ischar(Acol) && ischar(Bcol))
    [Acol Bcol] = StrSepsame(Acol,Bcol);
    ABcol = [Acol Bcol];
  else
    ABcol = [Acol; Bcol];
  end

  % Convert separators.
  if (ischar(Aval) && ischar(Bval))
    [Aval Bval] = StrSepsame(Aval,Bval);
    ABval = [Aval Bval];
  else
    ABval = [Aval; Bval];
  end

%whos

%  AB = Assoc([Arow Brow],[Acol Bcol],[Aval Bval],func);
  AB = Assoc(ABrow,ABcol,ABval,func);

  % Or: takes care of itself.
  % Xor: takes care of itself.
  % Nand: don't worry about it.
  % And: need to deal with [0 1] case.
  isAnd = not(func([0 1]));
  if isAnd
%    ABtest = Assoc([Arow Brow],[Acol Bcol],ones(NumStr([Arow Brow]),1),@sum)
%    ABtest = Assoc([Arow Brow],[Acol Bcol],1,@sum);
    ABtest = Assoc(ABrow,ABcol,1,@sum);
    AB.A(ABtest.A < 2) = 0;
  end

  % Repeat to clean up eliminated entries.
  [ABrow ABcol ABval] = find(AB);
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

