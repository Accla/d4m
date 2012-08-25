function DBcreate(instanceName,host,table,user,pass,varargin)
%DBcreate: Create a table on a specific database.
%Database internal function for subsref.
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

optargin = size(varargin,2);
%nVargs=length(varargin);
cloudtype='Big';
if optargin > 0
  cloudtype=varargin{1};
end
%disp([' CloudType = ' cloudtype]);
% !!! DB variable doesn't exist in this context !!!
%if strcmp(DB.type,'BigTableLike')
    ops = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',instanceName, host, user, pass);
    ops.init(instanceName,host,user,pass,cloudtype);

    ops.createTable(table);
%end

% The following will work for any jdbc database. It should pick the relevant driver
% from the sql properties file, where it gets the username and password
% We should really be sending down a schema at Table create, so Alterops are avoided

if 0
  if strcmp(DB.type,'mysql')
    ops = DBaddJavaOps('edu.mit.ll.d4m.db.sql.D4mDbOperations',host);


    if (optargin == 0)
        ops.createTable(table);             % With default schema
    else
        ops.createTable(table,varargin{1}); % With schema
    end

  end
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

