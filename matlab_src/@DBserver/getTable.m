%
function schema = getTable(DB, tableName)

urlport = DB.host;

cmd = ['wget -q -O - "' urlport 'new_session" --http-user=' DB.user ' --http-password=' DB.pass];

[stat, sessionID] = system(cmd);
if stat>0
    error('Unable to create a new SciDB session');
end
sessionID = deblank(sessionID);

% use "-S" to save HTTP headers and examine them for error codes
cmd = ['wget -S -q -O - "' urlport 'execute_query?id=' sessionID ...
       '&query=show(' tableName ')&save=dcsv" --http-user=' DB.user ' --http-password=' DB.pass];

[stat, queryID] = system(cmd);
if stat>0
    % error response : could be scidb error or the table may not exist
    pattern = 'HTTP/1.1 \d*'; % look for HTTP error codes
    y = regexp(queryID, pattern, 'match');
    if length(y) > 1
        % shim returns two http codes. the second one is relevant to us
        y = y{2};
    end
    y = strsplit(y, ' ');
    if str2double(y{2}) == 500
        % table does not exist
        schema = {};
        % function should return from here
    else
        error('Unable to execute query\nSciDB error code : %s', y{2});
    end
else
    % table exists. read schema
    cmd = ['wget -q -O - "' urlport 'read_lines?id=' sessionID '&n=0" --http-user=' DB.user ' --http-password=' DB.pass];

    [stat, schema] = system(cmd);
    if stat>0
        error('Unable to retrieve query resutls');
    end

    cmd = ['wget -q -O - "' urlport 'release_session?id=' sessionID '" --http-user=' DB.user ' --http-password=' DB.pass];
    [stat, sessionID] = system(cmd);

    % updated 6/19
    pat = '<|]' ; 
    z = regexp(schema, pat);
    schema = schema(z(1) : z(2));
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
% Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%




