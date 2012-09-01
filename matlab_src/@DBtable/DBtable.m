function T = DBtable(DB,tablename)
%DBtable: Create DBtable object binding to a specific database table.
%Database table internal function.
%  Usage:
%    T = DBtable(DB,tablename)
%  Inputs:
%    DB = database object with a binding to a specific database
%    table = name of table in database
% Outputs:
%    T = database table object

  T.DB = DB;   % Copy table.
  T.name = tablename;  % Set tablename.
  T.security = '';    % Set default security authorizations.
  T.numLimit = 0;    % Set default results limit - infinite.
  T.numRow = 0;    % Set default results limit - infinite.
  T.columnfamily = '';   
  T.putBytes = 5e5;  % Set defaul put chunk size.

  DBstruct = struct(DB);

  T.d4mQuery = '';

  if strcmp(DBstruct.type,'BigTableLike') || strcmp(DBstruct.type,'Accumulo')

    T.d4mQuery = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDataSearch',DBstruct.instanceName, DBstruct.host, T.name, DBstruct.user,DBstruct.pass);
    T.d4mQuery.setCloudType(DBstruct.type);
    
  end

  if strcmp(DBstruct.type,'sqlserver')

    import java.sql.ResultSet;
    import java.sql.Statement;

    queryStr = ['select * from ' T.name];
    if (strcmp(lower(T.name(1:7)),'select '))
      queryStr = T.name;
    end
    conn = DBsqlConnect(T.DB);
    query = conn.createStatement(java.sql.ResultSet.TYPE_SCROLL_SENSITIVE,java.sql.ResultSet.CONCUR_READ_ONLY);
    T.d4mQuery = query.executeQuery(queryStr);

  end

  T=class(T,'DBtable');

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

