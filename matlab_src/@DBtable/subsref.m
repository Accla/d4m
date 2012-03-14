function varargout = subsref(T, s)
%SUBSREF Get entries from DB table.

  nl = char(10);
  DB = struct(T.DB);

  retRows = '';  retCols = '';  retVals = '';

  if strcmp(DB.type,'BigTableLike')

    % If there are arguments, then build a new query.
    if (numel(s.subs) > 0)
      row = s.subs{1};
      col = s.subs{2};

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

