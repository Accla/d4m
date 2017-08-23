% querySciDB ASSUMES WE WANT 4 OUTPUTS - SHOULD RENAME THIS
% [rowidx, colidx, sliceidx, values] = querySciDB(...)
% DBQuerySciDB(DB, query)
%This is how any query is run on SciDB:
%-- new_session
%--- execute_query
%--- read_lines
%--- release_session
function varargout = querySciDB(T, DB, varargin)

[tableName, tableSchema] = SplitSciDBstr(T.name);

% It seems that SciDB array dimensions are always int64. Convert the retCols and retRows to double and the actual data
% to the data type from the schema
pattern = '<\S*>';
y = regexp(tableSchema, pattern, 'match'); % regexp will return cell array
y = y{1};
y = strsplit(y(2:end-1), {':',','});

% these are the columns extracted from the database and the data types :
%valueNames = y(1:2:end);
%valueAttribs = y(2:2:end);

% determine dimension names
pattern = '\[\S.*\]';
y = regexp(tableSchema, pattern, 'match'); % regexp will return cell array
y = y{1}(2:end-1); % get rid of '[' and ']'
y = strsplit(y, {'=',','});
dimNames = y(1:4:end);
dimLimits = y(2:4:end); % this is a cell array of strings like this : dimLimits = {'1:3815', '1:3803', '1:202'}

% Figure out the indices we have to extract for all dimensions. These are
% in varargin

arrayLimits = zeros(length(varargin)*2, 1);
N = length(varargin);
for k=1:N
    indices = varargin{k};
    if ischar(indices) && isequal(indices,':')  % this might be ':'
        x = strsplit(dimLimits{k}, ':');
        arrayLimits(k) = str2double(x(1));
        arrayLimits(k+N) = str2double(x(2));
    else
        arrayLimits(k) = min(indices);
        arrayLimits(k+N) = max(indices);
    end
end
str = sprintf('%d,', arrayLimits);
str = str(1:end-1); % remove training ','
query = sprintf('between(%s,%s)', tableName, str);

%disp(query);

% connect to database
DB = struct(DB);
cmd = ['wget -q -O - "' DB.host 'new_session" --http-user=' DB.user ' --http-password=' DB.pass];
[stat, sessionID] = system(cmd);

if stat>0
    error('Unable to connect to database. Error: %s\n', geterrormsg(stat));
end
sessionID = deblank(sessionID);
%tic
cmd = sprintf('wget -q -O - "%sexecute_query?id=%s&query=%s&save=dcsv" --http-user=%s --http-password=%s', ...
    DB.host, sessionID, query, DB.user, DB.pass);

[stat, ~] = system(cmd);
if stat>0
    error('Unable to query database');
end

%disp('querying database');
%tic;
%[tableData,success]=urlread([urlport 'read_lines?id=' sessionID '&release=1']);
cmd = sprintf('wget -q -O - "%sread_lines?id=%s&release=1" --http-user=%s --http-password=%s', ...
    DB.host, sessionID, DB.user, DB.pass);
[stat, tableData] = system(cmd);
if stat>0
    error('Unable to read query results');
end
%toc
%tic

%[rc, v] = SplitStr(tableData,'}');
%vMat = Str2mat(v);
%retVals = Mat2str(vMat(2:end,2:end));

% now parse the output
id = strfind(tableData, sprintf('\n'));
y = tableData(id(1)+1:end);
% original implementation below:
%y = strsplit(strrep(strrep(strrep(strrep(y, '{', ''), '}', ','), sprintf('\n'), ','), ' ' ,''), ',');
%if isempty(y{end})
%    y = y(1:end-1);
%end
% new implementation:
% assumes all values are doubles
y = strrep(strrep(strrep(strrep(y, '{', ''), '}', ''), ',', ' '), sprintf('\n'), ' ');
y = sscanf(y, '%f'); % this is MUCH faster than str2double() on large arrays
%
%disp('done');
%toc
% parse out the values extracted from the database
%values = ....
if nargout>0
    %{
    % The following works great
    for k = 1:nargout
        varargout{k} = y(k:4:end); %#ok<AGROW>
    end
    %}
    if nargout == 4
        % extracting 3d data : [ir ic iz data]
        varargout{1} = y(1:4:end);
        varargout{2} = y(2:4:end);
        varargout{3} = y(3:4:end);
        varargout{4} = y(4:4:end);
    else 
        % extracting 2d data : [ir ic data]
        varargout{1} = y(1:3:end);
        varargout{2} = y(2:3:end);
        varargout{3} = y(3:3:end);
    end
end

%{
% assume for now that only one attribute is being extracted
retVals = zeros(size(vMat,1)-1, 1, valueAttribs{1});
retRows = zeros(size(vMat,1)-1, 1);
retCols = zeros(size(vMat,1)-1, 1);
for nn = 2:size(vMat,1)
    retVals(nn-1) = cast(str2double( vMat(nn,:) ), valueAttribs{1});
    retRows(nn-1) = str2double( rMat(nn,2:end) );
    retCols(nn-1) = str2double( cMat(nn,:) );
end
%}


