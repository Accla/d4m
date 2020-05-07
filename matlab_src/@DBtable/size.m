function s = size(T,varargin)
%SIZE returns size of table.
%  Usage:
%     s = size(T,varargin)
%  INPUT:
%     T = Table object
%     varargin = index - 1 or 2
%  OUTPUT:
%     s = size of the table as vector [nrow ncol ] or scalar (either nrow or ncol).
%
%  The size that is returned can be a vector [ nrow ncol] or scalar
%    varargin : indicate the index 
%  Example:
%     sz = size(T) , where sz is vector of number of rows and number of columns.
%     sz = size(T,1)  where sz is a scalar representing the number of rows.
%     sz = size(T,2) will return the number of columns.

if(numel(varargin)>0)
    indexNum=varargin{1};
end

%Flags for executing row count or column count or both
doRowCount=0;
doColCount=0;

if(exist('indexNum'))
    s=[1];
    if(indexNum == 1)
        doRowCount=1;
    else
        doColCount=1;
    end    
      
else
    s = [1 1];
    doRowCount=1;
    doColCount=1;
end


Tstruct = struct(T);
DB = struct(Tstruct.DB);

if strcmp(DB.type,'BigTableLike') || strcmp(DB.type,'Accumulo')
    
end

if strcmp(DB.type,'sqlserver') || strcmp(DB.type,'pgres') || strcmp(DB.type,'mysql') %added pgres --sid
    
    if(~isempty(T.d4mQuery))
        Tstruct.d4mQuery.last();
        s(1) = Tstruct.d4mQuery.getRow();
        md = Tstruct.d4mQuery.getMetaData();
        s(2) = md.getColumnCount();
    else %case where only a binding
        
        %check if schema/tablename
        tablename = Tstruct.name;
        schemaname='';
        ind = strfind(tablename, '.');
        if(numel(ind>0))
            schemaname = tablename(1:ind-1);
            tablename = tablename(ind+1:end);
        end
        
        rowQuery = ['select count(*) from ' [schemaname '.' ...
            tablename] ';'];
        if(~isempty(schemaname))
            colQuery = ['select count(*) from information_schema.columns where ' ...
                'table_name=''' strrep(tablename, '"','') ...
                ''' and table_schema=''' strrep(schemaname, '"','') ''';'];
        else
            
            colQuery = ['select count(*) from information_schema.columns where ' ...
                'table_name=''' strrep(tablename, '"','') ''';'];
        end
        % Establish connection to sql server
        conn = DBsqlConnect(T.DB);
        query = ...
                sqlCreateStatement(T,conn);
        % Do row count
        if(doRowCount == 1)
            if strcmp(DB.type,'pgres') 
               % Go do the estimated count for Postgresql
               nrows = do_postgres_count(query,schemaname,tablename);
            else 
               T.d4mQuery = query.executeQuery(rowQuery);
               T.d4mQuery.absolute(1);
               nrows = (T.d4mQuery.getInt(1));
            end
        end
        % Do column count
        if(doColCount == 1)
            T.d4mQuery = query.executeQuery(colQuery);
            T.d4mQuery.absolute(1);
            ncols = (T.d4mQuery.getInt(1));
        end
        if(exist('indexNum'))
                if (indexNum==1) %return rows
                    s = nrows;
                else
                    s = ncols;
                end
        else
                s(1) =nrows;  % Get value.
                s(2) =ncols;
        end
        
        
        conn.close();
        
    end
    
end

if strcmp(DB.type,'scidb')
    urlport = struct(DB).host;
    
    %Create Session
    [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' DB.user ...
        ' --http-password=' DB.pass]);
    
    sessionID = deblank(sessionID);
    [a,b] = SplitSciDBstr(struct(T).name);
    queryStr=['dimensions(' a ')'];
    
    %Get schema of output file
    syscommand = ['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
        '&query=' queryStr '&save=dcsv" --http-user=' DB.user ...
        ' --http-password=' DB.pass];
    
    [stat, tmp]=system(syscommand);
    [stat, tableData] = system(['wget -q -O - "' urlport 'read_lines?id=' ...
        sessionID '&release=1"  --http-user=' DB.user ' --http-password=' ...
        DB.pass]);
    
    [rc v] = SplitStr(tableData,'}');
    vMat = Str2mat(v);
    retVals = Mat2str(vMat(2:end,2:end));
    [r c] = SplitStr(rc,',');
    cMat = Str2mat(c);
    retCols = Mat2str(cMat(2:end,:));
    rMat = Str2mat(r);
    retRows = Mat2str(rMat(2:end,2:end));
    s=v;
end



end
function nrow = do_postgres_count(queryObj, schemaname, tablename)
%  PostgreSQL only
%  Sum of the estimated count 'reltuples' from pg_class for the children tables or  from SELECT COUNT(*) from tablename
%
%        rowQuery = ['select count(*) from ' [schemaname '.' ...
%           tablename] ';'];
%
%
%    SELECT relname,relhassubclass,relpages,reltuples FROM pg_class WHERE oid='${TABLENAME}'::regclass;
%
% Summation of the reltuples from the children tables
%      SELECT  SUM(child.reltuples)    AS number_of_records_all_partitions \
%            FROM pg_inherits \
%                JOIN pg_class parent  ON pg_inherits.inhparent = parent.oid \
%                JOIN pg_class child  ON pg_inherits.inhrelid   = child.oid \
%                JOIN pg_namespace nmsp_parent ON nmsp_parent.oid  = parent.relnamespace \
%                JOIN pg_namespace nmsp_child  ON nmsp_child.oid   = child.relnamespace \
%            WHERE parent.relname = 'trackdata_1' AND nmsp_parent.nspname ='dis';
%
% Do the estimated count if there are children table, excluding system catalog tables.
%     queryObj 'PreparedStatement' object
%     schemaname  Schema name of the parent table
%     tablename  name of the main table 
   nrow=0;

   % Determine if the table has children
   sql1=['select relhassubclass from pg_class WHERE oid=''' [schemaname '.' tablename] '''::regclass;'];
   rs= queryObj.executeQuery(sql1); 
   rs.absolute(1);
   hasChild = (rs.getString(1));

   if strcmp(hasChild, 't')

        % Children tables exist, get the estimated count
        sql2 = ['SELECT  COALESCE(SUM(pgcls_child.reltuples),0)  ' ...
            'FROM pg_inherits ' ...
            'JOIN pg_class pgcls_parent  ON pg_inherits.inhparent = pgcls_parent.oid ' ...
            'JOIN pg_class pgcls_child  ON pg_inherits.inhrelid   = pgcls_child.oid ' ...
            'JOIN pg_namespace nmsp_parent ON nmsp_parent.oid     = pgcls_parent.relnamespace '  ...
            'JOIN pg_namespace nmsp_child  ON nmsp_child.oid      = pgcls_child.relnamespace ' ...
            'WHERE pgcls_parent.relname ='''  strrep(tablename, '"','')  ''' AND nmsp_parent.nspname=''' schemaname ''';'];
        rs= queryObj.executeQuery(sql2); 
        rs.next();
        nrow = str2double(rs.getString(1));

   else
       if ~strcmp(schemaname,'pg_catalog') || ~strcmp(schemaname,'information_schema')
           % Use this query for schema's that are not pg_catalog and information_schema
           rowQuery = ['select reltuples from pg_class WHERE oid=''' [schemaname '.' tablename] '''::regclass;'];
           rs= queryObj.executeQuery(rowQuery); 
           rs.absolute(1);
           nrow = (rs.getInt(1));
        else
           rowQuery = ['select count(*) from ' [schemaname '.' ...
                        tablename] ';'];
           rs= queryObj.executeQuery(rowQuery); 
           rs.absolute(1);
           nrow = (rs.getInt(1));
        end

   end
   
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), Dr. Vijay
% Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

