function retCols = Col(T)
% Col : Returns column names from a table
%Database utility function.
%  Usage:
%    retCols = Col(T)
%  Inputs:
%    T = binding to an SQL table
%  Outputs:
%    retCols = string list of table column names

retCols = '';  % return empty for non-sql databases
nl = char(10);
Tstruct = struct(T);
DBobj = Tstruct.DB;
DB = struct(Tstruct.DB);

if strcmp(DB.type,'BigTableLike') || strcmp(DB.type,'Accumulo') ||  strcmp(DB.type,'scidb')
    return;
end

%Check if SQL or MYSQL database
if strcmp(DB.type,'sqlserver')||strcmp(DB.type,'mysql')||strcmp(DB.type,'pgres')
    
    if(~isempty(T.d4mQuery)) %case where data has been queried
        %Get metadata from query
        md = Tstruct.d4mQuery.getMetaData();
        numCols = md.getColumnCount();
        for j=1:numCols
            jcol = char(md.getColumnName(j));
            if isempty(jcol)
                jcol = sprintf('%d',j);
            end
            retCols = [retCols jcol nl];
        end
    else %case of only table binding
        
        %check if schema/tablename
        tablename = Tstruct.name;
        schemaname='';
        ind = strfind(tablename, '.');
        if(numel(ind>0))
            schemaname = tablename(1:ind-1);
            tablename = tablename(ind+1:end);
        end
        
        %           queryStr = ['select column_name from information_schema.columns ' ...
        %                         'where table_name=''' strrep(tablename,'"','') ...
        %                         ''';'];
        
        if(~isempty(schemaname))
            queryStr = ['select column_name from information_schema.columns where ' ...
                'table_name=''' strrep(tablename, '"','') ...
                ''' and table_schema=''' strrep(schemaname, '"','') ''';'];
        else
            
            queryStr = ['select column_name from information_schema.columns where ' ...
                'table_name=''' strrep(tablename, '"','') ''';'];
        end

        % Establish connection to sql server
	conn = DBsqlConnect(T.DB);
        %Create the Statement object to execute SQL query
        query = ...
              sqlCreateStatement(T,conn);
       
        T.d4mQuery = query.executeQuery(queryStr);
        Qsize = TsqlSize(T);
        rowIndex = 1:Qsize(1);   % Select all rows.
        colIndex = 1:Qsize(2);
        retCols = TsqlCol(T);    % Get column names.
        retColsMat = Str2mat(retCols);
        AcolName = Assoc(1,retCols,1:Qsize(2));
        
        numCols = numel(colIndex);
        numRows = numel(rowIndex);
        retVals = '';
        
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
        
        retCols=retVals;
        conn.close();
        
        
        
    end
    
    
end

end % closes function definition

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
% Dr. Vijay Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

