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
    if (strncmpi(T.name, 'scidb:', 6)) % only check first 6 chars
        % Will evaluate to false if T.name  does not have at least 6 characters
        
        queryStr= T.name(7:end);
        queryStr = strrep(queryStr,' ','%20');
        urlport = struct(DB).host;
        
        %Create Session
        [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' DBstruct.user ...
            ' --http-password=' DBstruct.pass]);
        if stat>0
            error('Unable to create new SciDB session');
        end
        sessionID = deblank(sessionID);
        
        %Get schema of output file
        tmpQuery = sprintf('wget -q -O - "%sexecute_query?id=%s&query=show(''%s'',''afl'')&save=dcsv"', ...
            urlport, sessionID, queryStr);
        syscommand = sprintf('%s --http-user=%s --http-password=%s', tmpQuery, DBstruct.user, DBstruct.pass);
        
        [stat, tmp] = system(syscommand);
        if stat>0
            error('Error running query:\n%s\n%s', tmpQuery, tmp);
        end
        
        [stat, tableData] = system(['wget -q -O - "' urlport 'read_lines?id=' ...
            sessionID '&release=1"  --http-user=' DBstruct.user ' --http-password=' ...
            DBstruct.pass]);
        if stat>0
            error('Unable to read query results');
        end

        [tabname,tabschema] = SplitSciDBstr(tableData);
        tabname = deblank(tabname); %#ok<NASGU> % tabname is not used anywhere. remove it ?
        tabschema = deblank(tabschema);
        
        strrep(queryStr,' ','%20'); % this command doesn't seemt to do anything - we are not assigning the output to any string
        tabschema=strrep(tabschema(1:end-1), ' ', ''); %get rid of extra ' that shows up and replace space
        
        %Copy to array tmpname
        [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' DBstruct.user ...
            ' --http-password=' DBstruct.pass]);
        if stat>0
            error('Unable to create new SciDB session');
        end
        sessionID = deblank(sessionID);
        
        tmpname='d4m_temporary_table'; %Hardcoded temporary variable name similar to 'ans'
        
        T.d4mQuery=tablename;
        T.name=[tmpname tabschema];
        DB([tmpname tabschema]);
        
        tmpQuery = sprintf('wget -q -O - "%sexecute_query?id=%s&query=store(%s,%s)&save=dcsv"', urlport, sessionID, queryStr, tmpname);
        syscommand = sprintf('%s --http-user=%s --http-password=%s' , tmpQuery, DBstruct.user, DBstruct.pass);
        [stat, ~] = system(syscommand);
        if stat>0
            error('Unable to run query:\n%s', tmpQuery);
        end
        
    elseif (strncmpi(T.name, 'afl:', 4)) %Just do the raw command, don't copy anywhere
        
        queryStr= T.name(5:end);
        queryStr = strrep(queryStr,' ','%20');
        urlport = struct(DB).host;
        
        %Create Session
        [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' DBstruct.user ...
            ' --http-password=' DBstruct.pass]);
        if stat>0
            error('Unable to create new SciDB session');
        end
        sessionID = deblank(sessionID);
        
        %Execute Query
        tmpQuery = sprintf('wget -q -O - "%sexecute_query?id=%s&query=%s&save=dcsv"', urlport, sessionID, queryStr);
        syscommand = sprintf('%s --http-user=%s --http-password=%s', tmpQuery, DBstruct.user, DBstruct.pass);
        [stat, ~]=system(syscommand);
        if stat>0
            error('Unable to execute query:\n%s', tmpQuery);
        end
        
        %Release Session
        [stat, ~] = system(['wget -q -O - "' urlport 'read_lines?id=' ...
            sessionID '&release=1"  --http-user=' DBstruct.user ' --http-password=' ...
            DBstruct.pass]);
        if stat>0
            error('Unable to read query output');
        end
        
        T.d4mQuery=queryStr;
        
    end
end

T=class(T,'DBtable');
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

