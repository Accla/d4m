function T = DBtable(DB,tablename)
%DBtable: Create DBtable object binding to a specific database table.
%Database table internal function.
%  Usage:
%    T = DBtable(DB,tablename)
%  Inputs:
%    DB = database object with a binding to a specific database
%    table = name of table in database
% Outputs:
%    T = database table object

T.DB = DB;   % Copy table.
T.name = tablename;  % Set tablename.
T.security = '';    % Set default security authorizations.
T.numLimit = 0;    % Set default results limit - infinite.
T.numRow = 0;    % Set default results limit - infinite.
T.columnfamily = '';
T.putBytes = 5e5;  % Set default put chunk size.

DBstruct=struct(DB);

T.d4mQuery = '';

if strcmp(DBstruct.type,'BigTableLike') || strcmp(DBstruct.type,'Accumulo')
    
    T.d4mQuery = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDataSearch',DBstruct.instanceName, ...
        DBstruct.host, T.name, DBstruct.user,DBstruct.pass);
    T.d4mQuery.setCloudType(DBstruct.type);
    
end

if strcmp(DBstruct.type,'sqlserver') || strcmp(DBstruct.type,'mysql')
    
    import java.sql.ResultSet;
    import java.sql.Statement;
    
    queryStr = ['select * from ' T.name];
    if (strcmp(lower(T.name(1:7)),'select '))
        queryStr = T.name;
    end
    conn = DBsqlConnect(T.DB);
    query = conn.createStatement(java.sql.ResultSet.TYPE_SCROLL_SENSITIVE,java.sql.ResultSet.CONCUR_READ_ONLY);
    T.d4mQuery = query.executeQuery(queryStr);
    %T.d4mQuery
    
end

if strcmp(DBstruct.type,'scidb')
    
    %If issuing a command, automatically create binding to tmp array with
    %results
    if (strcmp(lower(T.name(1:6)), 'scidb:'))
        
        queryStr= T.name(7:end);
        queryStr = strrep(queryStr,' ','%20');
        
        urlport = struct(DB).host;
        
        %Create Session
        [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' DBstruct.user ...
            ' --http-password=' DBstruct.pass]);
        
        sessionID = deblank(sessionID);
        
        %Get schema of output file
        syscommand = ['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
            '&query=show(''' queryStr ''',''afl'')&save=dcsv" --http-user=' DBstruct.user ...
            ' --http-password=' DBstruct.pass];
        
        [stat, tmp]=system(syscommand);
        [stat, tableData] = system(['wget -q -O - "' urlport 'read_lines?id=' ...
            sessionID '&release=1"  --http-user=' DBstruct.user ' --http-password=' ...
            DBstruct.pass]);
        
        
        [tabname,tabschema] = SplitSciDBstr(tableData);
        tabname=deblank(tabname);
        tabschema=deblank(tabschema);
        
        strrep(queryStr,' ','%20');
        tabschema=strrep(tabschema(1:end-1), ' ', '');%get rid of extra ' that shows up and replace space
        
        %Copy to array tmpname
        [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' DBstruct.user ...
            ' --http-password=' DBstruct.pass]);
        sessionID = deblank(sessionID);
        
        tmpname='d4m_temporary_table';
        
        T.d4mQuery=tablename;
        T.name=[tmpname tabschema];
        DB([tmpname tabschema]);

        syscommand = ['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
            '&query=store(' queryStr ',' tmpname ')&save=dcsv" --http-user=' DBstruct.user ...
            ' --http-password=' DBstruct.pass];
        
        
        [stat, tmp] = system(syscommand);
        
    end
    
    %T=class(T,'DBtable');
    
end

T=class(T,'DBtable');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
% Dr. Vijay Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

