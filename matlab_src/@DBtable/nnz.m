function s = nnz(T)
%nnz: Returns number of non-zeros in an associative array or database table.
%Associative array or database user function.
%  Usage:
%    n = nnz(A)
%  Inputs:
%    A = associative array or database table
%  Outputs:
%    n = number of non-zero (or non-empty) entries in an associative array or database table; same as the number of triples

% Use edu.mit.ll.d4m.db.cloud.D4mDbTableOperations

DB = struct(T.DB);
tabname = struct(T).name;
%disp(T);
if strcmp(DB.type,'BigTableLike') || strcmp(DB.type,'Accumulo')
    ops = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB.instanceName,DB.host,DB.user,DB.pass);
    %ops.init(DB.instanceName,DB.host,DB.user,DB.pass, DB.type);
    ops.setCloudType(DB.type);
    
    % Need to create a wrapper function that hides Matlab/Octave differences.
    
    %     if exist('OCTAVE_VERSION','builtin')
    %       tablenameList = java_new('java.util.ArrayList');
    %     else
    tablenameList = javaObject('java.util.ArrayList');
    %     end
    tablenameList.add(tabname);
    s = ops.getNumberOfEntries(tablenameList);
    %  end
    
    %  if exist('OCTAVE_VERSION','builtin')
    %    s = s.toString();
    %    s = str2num(s);
end

if strcmp(DB.type,'scidb')
    urlport = struct(DB).host;
    
    %Create Session
    [stat, sessionID] = system(['wget -q -O - "' urlport 'new_session" --http-user=' DB.user ...
        ' --http-password=' DB.pass]);
    
    sessionID = deblank(sessionID);
    [a,b] = SplitSciDBstr(tabname);
    queryStr=['aggregate(' a ',count(*))'];
    
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

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), Dr. Vijay
% Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

