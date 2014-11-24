function s = size(T)
%SIZE returns size of table.

s = [1 1];

Tstruct = struct(T);
DB = struct(Tstruct.DB);

if strcmp(DB.type,'BigTableLike') || strcmp(DB.type,'Accumulo')
    
end
if strcmp(DB.type,'sqlserver')
    
    s = TsqlSize(T);
    
end

if strcmp(DB.type,'scidb')
    urlport = struct(DB).host;
    
    %Create Session
    [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' DB.user ...
        ' --http-password=' DB.pass]);
    
    sessionID = deblank(sessionID);
    [a,b] = SplitSciDBstr(struct(T).name);
    queryStr=['dimensions(' a ')'];
    
    %Get schema of output file
    syscommand = ['wget -q -O - "' urlport 'execute_query?id=' sessionID ...
        '&query=' queryStr '&save=dcsv" --http-user=' DB.user ...
        ' --http-password=' DB.pass];
    
    [stat, tmp]=system(syscommand);
    [stat, tableData] = system(['wget -q -O - "' urlport 'read_lines?id=' ...
        sessionID '&release=1"  --http-user=' DB.user ' --http-password=' ...
        DB.pass]);
    
    [rc v] = SplitStr(tableData,'}');
    vMat = Str2mat(v);
    retVals = Mat2str(vMat(2:end,2:end));
    [r c] = SplitStr(rc,',');
    cMat = Str2mat(c);
    retCols = Mat2str(cMat(2:end,:));
    rMat = Str2mat(r);
    retRows = Mat2str(rMat(2:end,2:end));
    s=v;
end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), Dr. Vijay
% Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

