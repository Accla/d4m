%Delete SciDB Array
function DB = deleteSciDBarray(DB, tablename)

DBstruct = struct(DB);

urlport = DBstruct.host;

[stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' ...
    DBstruct.user ' --http-password=' DBstruct.pass]);

sessionID = deblank(sessionID);

[stat, queryID] = system(['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
    '&query=remove(' tablename ')&release=1" --http-user=' DBstruct.user ' --http-password=' ...
    DBstruct.pass]);

end