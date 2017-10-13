% merge Merges given list of arrays into specified array
% Usage:
%     merge(Tdest, T1, T2, T3, ...)
%
function varargout = merge(Tdest, varargin)

time = 0;

destName = strsplit(getName(Tdest), '<');
destName = destName{1};

T = struct(Tdest);
DB = T.DB;

% connect to database
DB = struct(DB);
cmd = ['wget -q -O - "' DB.host 'new_session" --http-user=' DB.user ' --http-password=' DB.pass];
[stat, sessionID] = system(cmd);

if stat>0
    error('Unable to connect to database. Error: %s\n', geterrormsg(stat));
end
sessionID = deblank(sessionID);

for k = 1:length(varargin)
    fprintf('array %d of %d\n', k, length(varargin));
    query = sprintf('store(merge(%s, %s), %s)', destName, varargin{k}, destName);
    cmd = sprintf('wget -q -O - "%sexecute_query?id=%s&query=%s" --http-user=%s --http-password=%s', ...
                  DB.host, sessionID, query, DB.user, DB.pass);

    %disp(query)
    tic;
    [stat, ~] = system(cmd);
    time = time + toc;
    if stat>0
        error('Unable to query database');
    end

end

if ~isempty(nargout)
    varargout{1} = time;
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), 
%                    Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
