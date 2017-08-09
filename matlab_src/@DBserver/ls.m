function varargout = ls(DB)
%ls: List tables in a DBserver object
%Database user function.
%  Usage:
%    ls(DB)
%    [tableValueStr, tableList] = ls(DB)
%  Inputs:
%    DB = database object with a binding to a specific database
% Outputs:
%    tableValueStr = Matrix with header and list of tables
%    tableList     = Cell array of string containing list of tables

nl = char(10); %tab = char(9); q = char(39);
actualTables = {};

if strcmp(DB.type,'BigTableLike') || strcmp(DB.type, 'Accumulo')
    ops = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbInfo',DB.instanceName,DB.host,DB.user,DB.pass);
    tableValueStr = char(ops.getTableList());
end

if strcmp(DB.type,'sqlserver')
    conn = DBsqlConnect(DB);
    % Send SQL command:  SHOW FULL TABLES FROM db_name.
    q = conn.prepareStatement('select * from sys.Tables');
    results = q.executeQuery();
    md = results.getMetaData();
    numCols = md.getColumnCount(); %#ok<NASGU>
    tableValueStr = '';
    for j=[1 7 8 9]
        tableValueStr = [tableValueStr char(md.getColumnName(j)) ','];
    end
    tableValueStr = [tableValueStr char(10)];
    while results.next()
        for j=[1 7 8 9]
            tableValueStr = [ tableValueStr char(results.getString(j)) ','];
        end
        tableValueStr = [tableValueStr char(10)];
    end
    conn.close();
end

if strcmp(DB.type,'mysql')
    conn = DBsqlConnect(DB);
    % Send SQL command:  SHOW FULL TABLES FROM db_name.
    q=conn.prepareStatement('show tables;');
    results = q.executeQuery();
    md = results.getMetaData();
    numCols = md.getColumnCount();
    tableValueStr = '';
    for j=1:numCols
        tableValueStr = [tableValueStr char(md.getColumnName(j)) ','];
    end
    tableValueStr = [tableValueStr char(10)];
    while results.next()
        for j=1:numCols
            tableValueStr = [ tableValueStr char(results.getString(j)) ','];
        end
        tableValueStr = [tableValueStr char(10)];
    end
    conn.close();
end

if strcmp(DB.type,'scidb')
    
    urlport = DB.host;
    
    cmd = ['wget -q -O - "' urlport 'new_session" --http-user=' ...
           DB.user ' --http-password=' DB.pass];

    [stat, sessionID] = system(cmd);
    if stat>0
        error('Unable to create a new SciDB session');
    end
    sessionID = deblank(sessionID);
    
    cmd = ['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
           '&query=list()&save=dcsv" --http-user=' DB.user ' --http-password=' DB.pass];

    [stat, queryID] = system(cmd);
    if stat>0
        error('Unable to execute query\nServer response : %s', queryID);
    end
    
    cmd = ['wget -q -O - "' urlport 'read_lines?id=' sessionID ...
           '&n=100000" --http-user=' DB.user ' --http-password=' DB.pass];

    % HACK : system() is not returning entire stdout when running on DB with tons of tables (>1000)
    % unix() with -echo works
    %if isunix
    %    [stat, tableValueStr] = unix(cmd, '-echo');
    %else
    [stat, tableValueStr] = system(cmd);
    %end
    
    if stat>0
        error('Unable to retrieve query resutls');
    end
    
    cmd = ['wget -q -O - "' urlport 'release_session?id=' sessionID ...
           '" --http-user=' DB.user ' --http-password=' DB.pass];

    [stat, sessionID] = system(cmd);
    if stat>0
        error('Unable to release SciDB session\nServer response : %s', sessionID); % do we have to error out here ? May not be necessary
    end
    
    % Convert to TSV format : code removed
    C = strsplit(tableValueStr, nl);
    id = cellfun(@(IN) ~isempty(IN), C);
    C = C(id); % remove empty cells
    C = cellfun(@(IN) sprintf('%s\n', IN), C, 'UniformOutput', false); % add the newline character at the end for clarity
    id = cellfun(@(IN) strncmpi(IN, '{No}', 4)==0, C); % this will help remove the header
    actualTables = C(id);
    tableValueStr = cell2mat(C);    
end

switch nargout
    case 1
        varargout{1} = tableValueStr;
    case 2
        varargout{1} = tableValueStr;
        varargout{2} = actualTables;
    otherwise
        disp(tableValueStr);
end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
% Dr. Vijay Gadepally (vijayg@ll.mit.edu),
% Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
