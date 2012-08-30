function varargout = subsref(T, s)
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

  nl = char(10);
  DB = struct(T.DB);

  retRows = '';  retCols = '';  retVals = '';

  if strcmp(DB.type,'BigTableLike') || strcmp(DB.type,'Accumulo')

    % If there are arguments, then build a new query.
    if (numel(s.subs) > 0)
      row = s.subs{1};
      col = s.subs{2};
      T.d4mQuery.setCloudType(DB.type);
      T.d4mQuery.setLimit(T.numLimit);
      % Need a new function:
      % T.d4mQuery.setRow(T.numRow);

      T.d4mQuery.reset();

      % Need modify doMatlabQuery to check numLimit and numRow.
      % If numLimit = 0 and numRow > 0, then switch to row query.
      T.d4mQuery.doMatlabQuery(row, col, T.columnfamily, T.security);
    end

    % If there are no arguments, then run the cached query.
    if (numel(s.subs) == 0)
      T.d4mQuery.next();
    end

    retRows = char(T.d4mQuery.getRowReturnString);
    retCols = char(T.d4mQuery.getColumnReturnString);
    retVals = char(T.d4mQuery.getValueReturnString);

  end

  if strcmp(DB.type,'sqlserver')


    if (numel(s.subs) == 2)

      Qsize = TsqlSize(T);    % Get query size.
      rowIndex = 1:Qsize(1);   % Select all rows.
      colIndex = 1:Qsize(2);   % Select all columns.
      retCols = TsqlCol(T);    % Get column names.
      retColsMat = Str2mat(retCols);
      AcolName = Assoc(1,retCols,1:Qsize(2));

      row = s.subs{1};
      if isnumeric(row)        % Pick a subset of rows.
        rowIndex = sort(row);
        rowIndex = rowIndex(rowIndex <= Qsize(1));
      end

      col = s.subs{2};
      if isnumeric(col)     % Pick a subset of column numbers.   
        colIndex = sort(col);
        colIndex = colIndex(colIndex <= Qsize(2));
        retCols = Mat2str(retColsMat(colIndex,:));
      else
        if (strcmp(col,':'))
          % Select all columns.
        else   % Pick a subset of column names.
          AcolName = AcolName(1,col);
          colIndex = sort(Val(AcolName));
          retCols = Mat2str(retColsMat(colIndex,:));
        end
      end

      numCols = numel(colIndex);
      numRows = numel(rowIndex);

      for i=rowIndex    % Loop through each row in results.
        T.d4mQuery.absolute(i);    % Move to row.
        jjval = '';
        for j=colIndex
          jval = char(T.d4mQuery.getString(j));    % Get value.
          if isempty(jval)
            jval = 'NULL';
          end
          jjval = [jjval jval nl];
        end
        retVals = [retVals jjval];
      end

      retRows = reshape(repmat(rowIndex.',[1 numCols]).',[numRows.*numCols 1]);
      retRows = sprintf(['%d' nl],retRows);

      retCols = repmat(retCols,[1 numRows]);
    
%      conn.close();
    end
  end

  % Return associative array.
  if (nargout <= 1)
    varargout{1} = Assoc(retRows,retCols,retVals);
  end

  % Return triple.
  if (nargout == 3)
    varargout{1} = retRows;
    varargout{2} = retCols;
    varargout{3} = retVals;
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

