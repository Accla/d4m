function DBdelete(instanceName,host,table,user,pass,varargin)
%DBcreate: Delete a table on a specific database.
%Database internal function for delete and deleteForce.
%  Usage:
%    ops = DBaddJavaOps(javaClass,instanceName,host,user,pass,varargin)
%  Inputs:
%    instanceName = database instance name
%    host = database host name
%    table = name of table to create
%    user = username on database
%    pass = password on database
%    varargin = optional argument used to specify the type of database to connect to
% Outputs:
%    

  javaClassName = 'edu.mit.ll.d4m.db.cloud.D4mDbTableOperations';

  nVargs = length(varargin);
  cloudtype='BigTableLike';
  if nVargs > 0
    cloudtype=varargin{1};
  end

  ops = DBaddJavaOps(javaClassName,instanceName,host,user,pass);
  ops.init(instanceName,host,user,pass,cloudtype);
  ops.deleteTable(table);

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

