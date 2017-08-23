% y = runSciDBQuery(DB, query)
function y = runSciDBQuery(DB, query)

% connect to database
DB = struct(DB);
cmd = ['wget -q -O - "' DB.host 'new_session" --http-user=' DB.user ' --http-password=' DB.pass];
[stat, sessionID] = system(cmd);

if stat>0
    error('Unable to connect to database. Error: %s\n', geterrormsg(stat));
end
sessionID = deblank(sessionID);

cmd = sprintf('wget -q -O - "%sexecute_query?id=%s&query=%s&save=dcsv" --http-user=%s --http-password=%s', ...
    DB.host, sessionID, query, DB.user, DB.pass);

[stat, message] = system(cmd);
if stat>0
    error('Unable to query database. Cause : %s\n', message);
end

disp('querying database');
cmd = sprintf('wget -q -O - "%sread_lines?id=%s&release=1" --http-user=%s --http-password=%s', ...
    DB.host, sessionID, DB.user, DB.pass);
[stat, tableData] = system(cmd);
if stat>0
    error('Unable to read query results');
end

id = strfind(tableData, sprintf('\n'));
tableData = tableData(id(1)+1:end);
[~, y] = regexp(tableData, '{\S*}', 'match', 'split');
if isempty(y{1})
    y = y(2:end);
end
y = str2double(y); % this takes a long time
