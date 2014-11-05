function [tableValueStr] = ls(DB)
%ls: List tables in a DBserver object
%Database user function.
%  Usage:
%    ls(DB)
%  Inputs:
%    DB = database object with a binding to a specific database
% Outputs:
%

nl = char(10); tab = char(9);

if strcmp(DB.type,'BigTableLike') || strcmp(DB.type, 'Accumulo')
    ops = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbInfo',DB.instanceName,DB.host,DB.user,DB.pass);
    ops.setCloudType(DB.type);
    tableValueStr = char(ops.getTableList());
end

if strcmp(DB.type,'sqlserver')
    conn = DBsqlConnect(DB);
    % Send SQL command:  SHOW FULL TABLES FROM db_name.
    q = conn.prepareStatement(['select * from sys.Tables']);
    results = q.executeQuery();
    md = results.getMetaData();
    numCols = md.getColumnCount();
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
    q=conn.prepareStatement(['show tables;']);
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
    %wget -q -O - http://scidb02.cloud.llgrid.txe1.mit.edu:8080/new_session --http-user=SciDBUser --http-password=KXp1lq54@KGmH4qNd-IBCjGoI    %curl --anyauth -u SciDBUser:KXp1lq54@KGmH4qNd-IBCjGoI http://scidb02.cloud.llgrid.txe1.mit.edu:8080/new_session
    
    
    
    %[sessionID,success]=urlread([urlport 'new_session']);
    %[queryID,success]=urlread([urlport 'execute_query?id=' sessionID '&query=list()&save=dcsv']);
    %[tableValueStr,success]=urlread([urlport 'read_lines?id=' sessionID '&n=500']);
    %[sessionID,success]=urlread([urlport 'release_session?id=' sessionID]);
    
    [stat, sessionID] = system(['wget -q -O - "' urlport ...
        'new_session" --http-user=' DB.user ' --http-password=' ...
        DB.pass]);
    sessionID = deblank(sessionID);
    [stat, queryID] = system(['wget -q -O - "' urlport ...
        'execute_query?id=' sessionID '&query=list()&save=dcsv" --http-user=' ...
        DB.user ' --http-password=' DB.pass]);
    [stat, tableValueStr] = system(['wget -q -O - "' urlport 'read_lines?id=' ...
        sessionID '&n=500" --http-user=' DB.user ' --http-password=' DB.pass]);
    [stat, sessionID] = system(['wget -q -O - "' urlport 'release_session?id=' ...
        
    % Convert to TSV format.
    hdr = ['{No}' tab 'name' tab 'id' tab 'schema' tab 'availability' nl];
    tableValueStrMat = Str2mat(strrep(strrep(strrep(tableValueStr,'",',tab),',"',tab),'} "',['}' tab]));
    tableValueStrMat(1,1:size(hdr,2)) = hdr;
    tableValueStr = Mat2str(tableValueStrMat);
    
    end
    
    end
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % D4M: Dynamic Distributed Dimensional Data Model
    % Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
    % Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
    % Dr. Vijay Gadepally (vijayg@ll.mit.edu)
    % MIT Lincoln Laboratory
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % (c) <2010> Massachusetts Institute of Technology
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
