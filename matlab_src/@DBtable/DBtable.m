function T = DBtable(DB,tablename)
%DBTABLE Table reference
%   T = DBTABLE(DB, TABLE) retruns a table reference to TABLE
%
%   T = DB(TABLE) is equivalent to the above.
%
%   See also: DBTABLE/DELETE,
%             DBTABLE/NNZ,
%             DBTABLE/PUT,
%             DBTABLE/PUTTRIPLE,
%             DBTABLE/SUBSREF

  T.DB = DB;   % Copy table.
  T.name = tablename;  % Set tablename
  T.security = '';    % Set default security authorizations.
  T.numLimit = 0;    % Set default results limit - infinite.
  DBstruct = struct(DB);

  T.d4mQuery =DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbQuery',DBstruct.instanceName, DBstruct.host, T.name, DBstruct.user,DBstruct.pass);

  if strcmp(DBstruct.type,'BigTableLike')
    %T.columnfamily = 'vertexFamily';   
    T.columnfamily = '';   
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

