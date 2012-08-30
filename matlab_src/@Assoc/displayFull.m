function displayFull(A)
%displayFull: Display an associative array as formatted table.
%Associative array user function.
%  Usage:
%    displayFull(A)
%  Inputs:
%    A = associative array
%  Outputs:
%

%DISPLAY prints out non-zero triples as a table.

  row = Row(A); col = Col(A);  val = Val(A);
  AA = Adj(A);


  if ischar(row)
    row =  Str2mat(row);
  else
    row = num2str(row);
  end

  if ischar(col)
    col =  Str2mat(col);
  else
    col = num2str(col);
  end

  if ischar(val)
    val =  Str2mat(val);
  else
    [r c v] = find(A);
    v = num2str(v);    
    v(:,end+1) = ',';
    A = Assoc(r,c,Mat2str(v));
    val = Str2mat(Val(A));
    AA = Adj(A);
  end


  [Nrow Mrow] = size(row);
  [Ncol Mcol] = size(col);
  [Nval Mval] = size(val);

  ColSpace = max(Mcol,Mval);

  Amat = repmat(' ',Nrow+1,Mrow + Ncol*ColSpace);


  [NAmat MAmat] = size(Amat);

  % Insert row labels.
  Amat(2:end,1:Mrow) = row;

  % Insert column labels.
  jj = 1;
  for j=(Mrow+1):ColSpace:MAmat
    Amat(1,j:(j+Mcol-1)) = col(jj,:);
    jj = jj + 1;
  end

  [i j v] = find(AA);

  % Insert values.
  for ii=1:numel(i)

    j1 = (Mrow+1) + ColSpace*(j(ii)-1);
    j2 = j1 + Mval - 1;
    Amat(i(ii)+1,j1:j2) = val(v(ii),:);

  end

  Amat(Amat == ' ') = 0;
  Amat = Amat(:,sum(double(Amat),1) > 0);
  Amat(Amat == 0) = ' ';
  disp(Amat);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

