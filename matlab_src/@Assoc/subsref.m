function varargout = subsref(A, s)
%(),subsref: Selects rows and columns from an associative array or database table.
%Associative array and database user function.
%  Usage:
%    Asub = A(row,col)
%    [r c v] = A(row,col)
%  Inputs:
%    A = associative array or database table
%    col = row keys to select; can be also be numeric if A is an associative array
%    col = column keys to select; can be also be numeric if A is an associative array
%  Outputs:
%    Asub = sub associative array of all the non-empty rows and columns
%  Examples:
%    row and col can take one of the following formats:
%       :                 - Returns every value
%       value1,value2,... - Returns every row or column called value1, value2, ...
%       start,:,end,      - Returns every row or column between start and
%                           end inclusive lexographically (! comes before
%                           all alpha numberic values, and ~ comes after
%                           all alpha numeric values).
%       prefix.*,         - Returns ever row or column that starts with
%                           prefix. This is can ve slow. It is generally
%                           recommended that instead of 'prefix.*,' you use
%                           something to the effect of 'prefix!,:,prefix~,'
%                           as the latter will be much faster. The full
%                           operation of the * operatior is not entirely
%                           known.
%   NOTE: Each key is a string, and the last character represents the
%   delimiter to separate arguments. For these layouts we will use a
%   comma, but in practice you could use any single character.


subs = s(1).subs;

% If A is empty , return immediately.
% If subs is empty, return empty immediately
% to mimic table with NumLimit = 0;
if ( (nnz(A) < 1) || (numel(s.subs) == 0))
  Asub = Assoc('','','');  r = '';  c = '';  v = '';

  if (nargout <= 1)
    varargout{1} = Asub;
  end

  % Return triple.
  if (nargout == 3)
    varargout{1} = r;
    varargout{2} = c;
    varargout{3} = v;
  end

  return;
end

%
% Array access.
%
if strcmp(s(1).type, '()') %subscripting type

  if (numel(subs) == 1)
    % Using a logical Assoc for sub-assign.
    subA = subs{1};
    if IsClass(subA,'Assoc')
      if (islogical(Adj(subA)))
        row = Row(subA);
        col = Col(subA);
      end
    end
  end

  if (numel(subs) == 2)
    % Using (row,col) for sub-assign.
    row = subs{1};
    col = subs{2};
  end


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
      NrowStr = NumStr(row);
      rowMat = Str2mat(row);
      if ( (NrowStr >= 3) && (mod(NrowStr,3) == 0) && (sum(rowMat(2:3:end,1) == ':')==(NrowStr/3)) )
        % Grab range of rows.

        istart = StrSearch(A.row,Mat2str(rowMat(1:3:end,:)));
        istartOK = (istart > -N);
        istart(istart < 1) = min(abs(istart(istart < 1))+1,N);
%        Look like 'end' was a reserved word.  Should it be kept it?
%        if (strcmp(rowMat(3,1:end-1),'end'))
%          iend = N;
%        else
          iend = abs(StrSearch(A.row,Mat2str(rowMat(3:3:end,:))));
%          iend = StrSubsref(A.row,Mat2str(rowMat(3,:)));
%        end
        istart = istart(istartOK);
        iend = iend(istartOK);
        i = [];  % Initialize i.
        for iloop = 1:numel(istart)
          i = [i istart(iloop):iend(iloop)];
        end
        i = unique(i);  % Eliminate duplicates.
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
      NcolStr = NumStr(col);
      colMat = Str2mat(col);
      if ( (NcolStr >= 3) && (mod(NcolStr,3) == 0) && (sum(colMat(2:3:end,1) == ':')==(NcolStr/3)) )
        % Grab range of cols.
        jstart = StrSearch(A.col,Mat2str(colMat(1:3:end,:)));
        jstartOK = (jstart > -M);
        jstart(jstart < 1) = min(abs(jstart(jstart < 1))+1,M);

%        if (strcmp(colMat(3,1:end-1),'end'))
%          jend = N;
%        else
          jend = abs(StrSearch(A.col,Mat2str(colMat(3:3:end,:))));
%        end
        jstart = jstart(jstartOK);
        jend = jend(jstartOK);
        j = [];  % Initialize n.
        for jloop = 1:numel(jstart)
          j = [j jstart(jloop):jend(jloop)];
        end
        j = unique(j);  % Eliminate duplicates.

      else  % col is a string of keys.
        j = StrSubsref(A.col,col);
      end
    end
  end

  % Get the submatrix.
%  AA = A.A;
%  AA(:) = 0;
%  AA(i,j) = A.A(i,j);
  %vecN = sparse(zeros(N,1));
  %vecN(i) = 1;
  %diagN = diag(vecN);
  diagN = sparse(i,i,1,N,N);
  %vecM = sparse(zeros(M,1));
  %vecM(j) = 1;
  %diagM = diag(vecM);
  diagM = sparse(j,j,1,M,M);

  AA = diagN * A.A * diagM;
  % Get the indices and values.
%  [rowSub colSub valSub] = find(AA);

%  iSub = find(sum(AA,2));
%  jSub = find(sum(AA,1));
 iSub = find(sum(AA ~= 0,2));
 jSub = find(sum(AA ~= 0,1));
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
%    [vSub  v_out2in v_in2out] = unique(full(Asub.A(Asub.A > 0)));
    [r c v] = find(Asub.A);
    [vSub  v_out2in v_in2out] = unique(v);
    valMat = Str2mat(A.val);
    Asub.val = Mat2str(valMat(vSub,:));

%    tic; Asub.A(Asub.A > 0) = v_in2out; toc
    [N M] = size(Asub.A);
    Asub.A = sparse(r,c,v_in2out,N,M);
  end


  if (numel(subs) == 1)
    % Using a logical Assoc for sub-assign.
    if IsClass(subA,'Assoc')
      if (islogical(Adj(subA)))
        % Get indices from Asub.
        row = Row(Asub);
        col = Col(Asub);
        % Fine corresponding indices in subA.
        i = StrSubsref(subA.row,row);
        j = StrSubsref(subA.col,col);

        % And the adjacency matrices.
        Asub.A = (Asub.A .* subA.A(i,j));

        % Reconstruct.
        [r c v] = find(Asub);
        Asub = Assoc(r,c,v);     
      end
    end
  end
  
  if (nargout <= 1)
    varargout{1} = Asub;
  end
  % Return triple.
  if (nargout == 3)
    [r c v] = find(Asub);
    varargout{1} = r;
    varargout{2} = c;
    varargout{3} = v;
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

