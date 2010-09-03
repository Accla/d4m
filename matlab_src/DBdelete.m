
function DBdelete(instanceName,host,table,user,pass)
% Delete Database Tables
%
% Returns nothing.
%
% Example:

%
% host='host_name'
% table='table_name'
% DBdelete(host,table)
%
%
%
% For help on creating database tables;
% type help DBcreate

  javaClassName = 'edu.mit.ll.d4m.db.cloud.D4mDbTableOperations';

% !!! DB variable doesn't exist in this context.  !!!
%  switch lower(DB.type)
%  	case 'cloudbase'
  		javaClassName ='edu.mit.ll.d4m.db.cloud.D4mDbTableOperations';
%  	case 'jdbc' 
%  		javaClassName ='edu.mit.ll.d4m.db.sql.D4mDbOperations';
%  	otherwise
%  		javaClassName ='edu.mit.ll.d4m.db.sql.D4mDbOperations';
%  end


  ops = DBaddJavaOps(javaClassName,instanceName,host,user,pass);
  ops.deleteTable(table);




%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu),
%  Mr. Charles Yee (yee@ll.mit.edu), Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

