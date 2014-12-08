function T = rename(T,new_name)

%rename: Rename a SciDB table using the AFL "rename" command
%keyboard
Tstruct=struct(T);
DB=Tstruct.DB;
DBstruct = struct(DB);
[tablename, tableschema]=SplitSciDBstr(Tstruct.name);  % Set tablename.

if strcmp(DBstruct.type,'scidb')
    
    urlport = DBstruct.host;
    
    %Create Session
    [stat, sessionID] = system(['wget -q -O - "' urlport ...
        'new_session" --http-user=' DBstruct.user ...
        ' --http-password=' DBstruct.pass]);
    
    sessionID=deblank(sessionID);
    
    %Execute rename query
    syscommand = ['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
        '&query=store(' tablename ',' new_name ')&save=dcsv" --http-user=' ...
        DBstruct.user ' --http-password=' DBstruct.pass];
    
    [stat, tmp] = system(syscommand);
    
    %Release session
    [stat, tableData] = system(['wget -q -O - "' urlport 'read_lines?id=' ...
        sessionID '&release=1"  --http-user=' DBstruct.user ' --http-password=' ...
        DBstruct.pass]);
    
    [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' ...
        DBstruct.user ' --http-password=' DBstruct.pass]);
    
    sessionID = deblank(sessionID);
    
    [stat, queryID] = system(['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
        '&query=remove(' tablename ')&release=1" --http-user=' DBstruct.user ' --http-password=' ...
        DBstruct.pass]);
    
    Tstruct.name=[new_name tableschema];

else
    
    disp('Rename function only valid for SciDB Tables');
    
end

T=class(Tstruct,'DBtable');