
function DBcreate(host,table)
% Create Database Tables
%
% Returns nothing.
%
% Example:

%
% host='host_name'
% table='table_name'
% DBcreate(host,table)
%
%
%
% For help on deleting database tables;
% type help DBdelete

  if strcmp(DB.type,'cloudbase')
    ops = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',host);
    ops.createTable(table);
  end

  if strcmp(DB.type,'mysql')
     % Send SQL command:  CREATE TABLE table
     % Columns will get added dynamically later on insert.
     % Yikes!
  end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu),
%  Mr. Charles Yee (yee@ll.mit.edu), Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

