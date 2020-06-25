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
    Tstruct = struct(T);

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
        
    elseif strcmp(DB.type,'scidb')
        
        % If there are arguments, then build a new query.
        if (numel(s.subs) > 0)
            row = s.subs{1};
            col = s.subs{2};
            
            if length(s.subs)>=2%isa(row, 'double') && isa(col, 'double')
                if length(s.subs) == 2                
                    [retRows, retCols, retVals] = querySciDB(T, DB, s.subs{:});
                    nr = range(retRows)+1;
                    nc = range(retCols)+1;
                    idx = sub2ind([nr nc], retRows-min(retRows)+1, retCols-min(retCols)+1);
                    y = zeros(nr, nc);
                    y(idx) = retVals;
                else
                    [retRows, retCols, retDepth, retVals] = querySciDB(T, DB, s.subs{:});                
                    nr = range(retRows)+1;
                    nc = range(retCols)+1;
                    ns = range(retDepth)+1;                
                    idx = sub2ind([nr nc ns], retRows-min(retRows)+1, retCols-min(retCols)+1, retDepth-min(retDepth)+1);
                    y = zeros(nr, nc, ns);
                    y(idx) = retVals;
                end
                varargout{1} = y;
                return
            else
                [tableName, tableSchema] = SplitSciDBstr(T.name);
                urlport = DB.host;
                %[sessionID,success]=urlread([urlport 'new_session']);
                [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' ...
                                    DB.user ' --http-password=' DB.pass]);
                sessionID = deblank(sessionID);
                
                
                ieq = find(tableSchema == '=');
                icom = find(tableSchema == ',');
                rowStr = tableSchema(find(tableSchema == '[')+1:(ieq(1)-1));
                colStr = tableSchema((icom(3)+1):(ieq(2)-1));
                
                rowStart = '';  rowEnd = '';
                
                colStart = '';  colEnd = '';
                
                if ( (numel(row) == 1) && (row == ':') )
                    % Grab all rows.
                    rowStart = 'NULL';  rowEnd = 'NULL';
                    rowQuery = 'true';
                else
                    NrowStr = NumStr(row);
                    rowMat = Str2mat(row);
                    if ( (NrowStr >= 3) && (mod(NrowStr,3) == 0) && (sum(rowMat(2:3:end,1) == ':')==(NrowStr/3)) )
                        % Grab range of rows.
                        rowStart = StrSubind(row,1);
                        rowStart = rowStart(1:end-1);
                        rowEnd = StrSubind(row,3);
                        rowEnd = rowEnd(1:end-1);
                        rowQuery = ['(' rowStr '>=' rowStart ' and ' rowStr '<=' rowEnd ')'];
                    else  % row is a string of keys.
                        sep = row(end);
                        rowQuery = CatStr([rowStr sep],'=',row);
                        rowQuery = ['(' strrep(rowQuery(1:end-1),sep,' or ') ')'];
                    end
                end
                
                if ( (numel(col) == 1) && (col == ':') )
                    % Grab all cols.
                    colStart = 'NULL';  colEnd = 'NULL';
                    colQuery = 'true';
                else
                    NcolStr = NumStr(col);
                    colMat = Str2mat(col);
                    if ( (NcolStr >= 3) && (mod(NcolStr,3) == 0) && (sum(colMat(2:3:end,1) == ':')==(NcolStr/3)) )
                        % Grab range of cols.
                        colStart = StrSubind(col,1);
                        colStart = colStart(1:end-1);
                        colEnd = StrSubind(col,3);
                        colEnd = colEnd(1:end-1);
                        colQuery = ['(' colStr '>=' colStart ' and ' colStr '<=' colEnd ')'];
                    else  % col is a string of keys.
                        sep = col(end);
                        colQuery = CatStr([colStr sep],'=',col);
                        colQuery = ['(' strrep(colQuery(1:end-1),sep,' or ') ')'];
                    end
                end
                
                if (isempty(rowQuery) || isempty(colQuery))
                    disp('Ill-formed query');
                else
                    %[queryID,success]=urlread([urlport 'execute_query?id=' sessionID ...
                    %'&query=between(' tableName ',' rowStart ',' colStart ',' rowEnd ',' colEnd ')&save=dcsv']);
                    % queryStr = ['filter(' tableName ',(row>=' rowStart ' and row<=' rowEnd ') and (col>=' colStart ' and col<=' colEnd '))']
                    queryStr = ['filter(' tableName ',(' rowQuery ') and (' colQuery '))'];
                    queryStr = strrep(queryStr,' ','%20');
                    
                    syscommand = ['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
                                  '&query=' queryStr '&save=dcsv" --http-user=' DB.user ' --http-password=' ...
                                  DB.pass];
                    %urlreadStr = [urlport 'execute_query?id=' sessionID '&query=' queryStr '&save=dcsv'];
                    %[queryID,success]=urlread(urlreadStr);
                    [stat, queryID] = system(syscommand);
                    if stat>0
                        error('Unable to query database');
                    end
                    
                    %[tableData,success]=urlread([urlport 'read_lines?id=' sessionID '&release=1']);
                    [stat, tableData] = system(['wget -q -O - "' urlport 'read_lines?id=' ...
                                        sessionID '&release=1"  --http-user=' DB.user ' --http-password=' ...
                                        DB.pass]);
                    
                    if stat>0
                        error('Unable to read query results');
                    end
                    
                    [rc, v] = SplitStr(tableData,'}');
                    vMat = Str2mat(v);
                    retVals = Mat2str(vMat(2:end,2:end));
                    [r, c] = SplitStr(rc,',');
                    cMat = Str2mat(c);
                    retCols = Mat2str(cMat(2:end,:));
                    rMat = Str2mat(r);
                    retRows = Mat2str(rMat(2:end,2:end));
                end
            end
        end
        
    elseif (strcmp(DB.type,'sqlserver') || strcmp(DB.type, 'mysql') || strcmp(DB.type, 'pgres') )
        
        %Moving query execution from DBtable.m to subsref.m
        
        %if(exist('OCTAVE_VERSION')) %not working yet (VG)
            %javaMethod('ResultSet', 'java.sql.ResultSet');   
            %javaMethod('Statement', 'java.sql.Statement');
            %ResultSet = javaObject('java.sql.ResultSet');   
            %Statement = javaObject('java.sql.Statement');
        %else
        %    import java.sql.ResultSet;
        %    import java.sql.Statement;
        %end
        
       
        if (numel(s.subs) == 2)
            
            %Qsize = TsqlSize(T);    % Get query size.
            Qsize = size(T);
            rowIndex = 1:Qsize(1);   % Select all rows.
            colIndex = 1:Qsize(2);   % Select all columns.
                                     %retCols = TsqlCol(T);    % Get column names.
            retCols = Col(T);
            retColsMat = Str2mat(retCols);
            AcolName = Assoc(1,retCols,1:Qsize(2));
            row = s.subs{1};
            if isnumeric(row)        % Pick a subset of rows.
                rowIndex = sort(row);
                rowIndex = rowIndex(rowIndex <= Qsize(1));
                offset = min(rowIndex)-1; %matlab counting from 1
                limit = numel(rowIndex);
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
            
            if (strcmp(lower(T.name(1:7)),'select '))
                queryStr = T.name;
            else
                queryStr = ['select * from ' T.name ' '];
            end

            if( exist('limit') && exist('offset') )
                queryStr = [queryStr ' limit ' num2str(limit)...
                            ' offset ' num2str(offset) ';'];
            else
                queryStr = [queryStr ';'];
            end
            %disp(['  ****  RUNNING QUERY --  ' queryStr])
            %  Establish connection with SQL server 
            conn = DBsqlConnect(T.DB);
            % Create Statement object
            %query = sqlCreateStatement(T,conn);
            % Create a D4mDbQuerySql java object to do queries
            query = createD4mDbQuerySql(T,conn);
            
	        %Execute query
            query.executeQuery(queryStr);
            retVals = [char(query.getVals())];
            numRecords = query.getRecordCount();
            if ~(numRecords == numRows)
                numRows = numRecords;
                rowIndex = rowIndex(1:numRows)
            end

            retRows = reshape(repmat(rowIndex.',[1 numCols]).',[numRows.*numCols 1]);          
            retRows = sprintf(['%d' nl],retRows);
            retCols = repmat(retCols,[1 numRows]);

            conn.close();

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

    if nargout == 4
        % case where we call : [ir, ic, iz, vals] = T(:,:,N);
        varargout{1} = retRows;
        varargout{2} = retCols;
        varargout{3} = retDepth;
        varargout{4} = retVals;
    end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), Dr. Vijay
%    Gadepally (vijayg@ll.mit.edu), Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
