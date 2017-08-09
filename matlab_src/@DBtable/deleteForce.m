function T = deleteForce(T)
%deleteForce: Deletes a table from a database; does *not* prompt user before proceeding.
%Database table user function.
%  Usage:
%    deleteForce(T)
%    TD = deleteForce(T)
%  Inputs:
%    T = database table or table pair object
% Outputs:
%    TD = empty database table or table pair object

DB = struct(T.DB);

TD = T;

if strcmp(DB.type, 'Accumulo')
    DBdelete(DB.instanceName,DB.host,T.name,DB.user,DB.pass, DB.type);
elseif strcmp(DB.type,'scidb')
    [tableName tableSchema] = SplitSciDBstr(T.name);
    urlport = DB.host;
    [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' ...
        DB.user ' --http-password=' DB.pass]);
    sessionID = deblank(sessionID);
    [stat, queryID] = system(['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
        '&query=remove(' tableName ')&release=1" --http-user=' DB.user ' --http-password=' ...
        DB.pass]);
    
end
%   TD.name = '';


TD.name = '';


end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
%    Dr. Vijay Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

