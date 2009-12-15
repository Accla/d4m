function Asub = subsref(A, s)
%SUBSREF Subscripted reference. Called for syntax A(S).
%   Should not be called directly.
%   SUBSREF(A, S) Subscripted reference on a distributed array A. 
%       S is a structure array with the fields:
%        type -- string containing '()', '{}', or '.' specifying the
%                subscript type.
%        subs -- Cell array or string containing the actual subscripts. 


subs = s(1).subs;
%sizeA = size(A);

row = subs{1};
col = subs{2};

%
% Array access.
%
if s(1).type == '()' %subscripting type
  % TODO eventually support < cases

  Asub = A;
  A = struct(A);

  if isnumeric(row)
    i = sort(row);
  end
  if isnumeric(col)
    j = sort(col);
  end

  [N M] = size(A.A);

  if ischar(row)
    if ( (numel(row) == 1) && (row == ':') )
      % Grab all rows.
      i = 1:N;
    else
      rowMat = Str2mat(row);
      if ( (NumStr(row) == 3) && (rowMat(2,1) == ':') )
        % Grab range of rows.

        istart = StrSearch(A.row,Mat2str(rowMat(1,:)));
        if (istart < 1)
          istart = min(abs(istart)+1,N);
        end
        if (strcmp(rowMat(3,1:end-1),'end'))
          iend = N;
        else
          iend = abs(StrSearch(A.row,Mat2str(rowMat(3,:))));
%          iend = StrSubsref(A.row,Mat2str(rowMat(3,:)));
        end
        i = istart:iend;
      else  % row is a string of keys.
        i = StrSubsref(A.row,row);
      end
    end
  end

  if ischar(col)
    if ((numel(col) == 1) && (col == ':'))
      % Grab all cols.
      j = 1:M;
    else
      colMat = Str2mat(col);
      if ( (NumStr(col) == 3) && (colMat(2,1) == ':') )
        % Grab range of cols.
        jstart = StrSearch(A.col,Mat2str(colMat(1,:)));
        if (jstart < 1)
          jstart = min(abs(jstart)+1,M);
        end
        if (strcmp(colMat(3,1:end-1),'end'))
          jend = N;
        else
          jend = abs(StrSearch(A.col,Mat2str(colMat(3,:))));
        end
        j = jstart:jend;
      else  % col is a string of keys.
        j = StrSubsref(A.col,col);
      end
    end
  end

  % Get the submatrix.
%  AA = A.A;
%  AA(:) = 0;
%  AA(i,j) = A.A(i,j);
  vecN = sparse(zeros(N,1));
  vecN(i) = 1;
  diagN = diag(vecN);
  vecM = sparse(zeros(M,1));
  vecM(j) = 1;
  diagM = diag(vecM);

  AA = diagN * A.A * diagM;

  % Get the indices and values.
%  [rowSub colSub valSub] = find(AA);

  iSub = find(sum(AA,2));
  jSub = find(sum(AA,1));
  Asub.A = A.A(iSub,jSub);

%  if isfield(A,'row')
  if not(isempty(A.row))
    rowMat = Str2mat(A.row);
    Asub.row = Mat2str(rowMat(iSub,:));
  end

%  if isfield(A,'col')
  if not(isempty(A.col))
    colMat = Str2mat(A.col);
    Asub.col = Mat2str(colMat(jSub,:));
  end

%  if isfield(A,'val')
  if not(isempty(A.val))
    [vSub  v_out2in v_in2out] = unique(Asub.A(Asub.A > 0));
    valMat = Str2mat(A.val);
    Asub.val = Mat2str(valMat(vSub,:));
    Asub.A(Asub.A > 0) = v_in2out;
  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
