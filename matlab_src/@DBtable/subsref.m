function varargout = subsref(T, s)
%SUBSREF Get entries from DB table.

  nl = char(10);
  DB = struct(T.DB);

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
    if (numel(s.subs) == 1)
      queryStr = s.subs{1};
      row = ':';
      col = ':';
    end
    if (numel(s.subs) == 2)
      row = s.subs{1};
      if isnumeric(row)
        row = sort(row);
        rowIndex = row;
      end
      col = s.subs{2};
      if (strcmp(col,':') || isnumeric(col))
        colQ = '*';
      else
        colQ = strrep(col,col(end),',');
        colQ = col(1:end-1);
      end
      queryStr = ['select ' colQ ' from ' T.name];
      if (strcmp(T.name(1:7),'SELECT ') || strcmp(T.name(1:7),'select '))
        queryStr = T.name;
      end
    end
    conn = DBsqlConnect(T.DB);
    query = conn.prepareStatement(queryStr);
    results = query.executeQuery();
    md = results.getMetaData();
    numCols = md.getColumnCount();
    retCols = '';
    colIndex = 1:numCols;
    if isnumeric(col)
      colIndex = reshape(col,[1 numel(col)]);
      numCols = numel(colIndex);
    end
    for j=colIndex
      jcol = char(md.getColumnName(j));
      if isempty(jcol)
        jcol = sprintf('%d',j);
      end
      retCols = [retCols jcol nl];
    end
    retVals = '';
    numRows = 0;
    KeepGoing = 1;
    AppendRow = 1;
    while (results.next() & KeepGoing)
      numRows = numRows + 1;
      if isnumeric(row)
        if (numRows == rowIndex(1))
          AppendRow = 1;
          if (numel(rowIndex) > 1)
            rowIndex = rowIndex(2:end);
          end
        elseif (numRows > rowIndex(end))
          KeepGoing = 0; % Break while loop.
          AppendRow = 0;
          numRows = numRows - 1;
        else
          AppendRow = 0;
        end
      end
      if (AppendRow)
        jjval = '';
        for j=colIndex
          jval = char(results.getString(j));
          if isempty(jval)
            jval = 'NULL';
          end
          jjval = [jjval jval nl];
        end
        retVals = [retVals jjval];
      end
    end
    rowIndex = 1:numRows;
    if isnumeric(row)
      rowIndex = row(row <= numRows);
      numRows = numel(rowIndex);
    end
    retRows = reshape(repmat(rowIndex.',[1 numCols]).',[numRows.*numCols 1]);
    if (nargout <= 1)
      retRows = sprintf(['%d' nl],retRows);
    end

    if (nnz(retCols == nl) == numel(retCols))
      retCols = repmat(colIndex,[1 numRows]).';
    else
      retCols = repmat(retCols,[1 numRows]);
    end
    
    conn.close();
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

