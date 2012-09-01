function T = DBtablePair(DB,tablename1,tablename2)
%DBtablePair: Create DBtable object binding to a specific database table pair.
%Database table internal function.
%  Usage:
%    T = DBtablePair(DB,tablename1,tablename2)
%  Inputs:
%    DB = database object with a binding to a specific database
%    tablename1 = name of table in database
%    tablename2 = name of table transpose in database
% Outputs:
%    T = database table pair object

%DBTABLEPAIR Table reference to a pair of transposed tables
%   T = DBTABLEPAIR(DB, TABLE1, TABLE2) retruns a table reference to the
%   paired tables TABLE1 and TABLE2 where TABLE1 = TABLE2.'.
%
%   T = DB(TABLE1, TABLE2) is equivalent to the above.
%
%   See also: DBTABLEPAIR/DELETE,
%             DBTABLEPAIR/NNZ,
%             DBTABLEPAIR/PUT,
%             DBTABLEPAIR/PUTTRIPLE,
%             DBTABLEPAIR/RANDROW,
%             DBTABLEPAIR/SUBSREF

  T.DB = DB;   % Copy table.
  T.name1 = tablename1;
  T.name2 = tablename2;
  T.security = '';    % Set default security authorizations.
  T.numLimit = 0;    % Set default results limit - infinite.
  T.putBytes = 5e5;  % Set default put chunk size.

  DBstruct = struct(DB);

  T.d4mQuery = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDataSearch',DBstruct.instanceName, DBstruct.host, T.name1, DBstruct.user,DBstruct.pass);
  T.d4mQuery.setCloudType(DBstruct.type);
  if strcmp(DBstruct.type,'BigTableLike') ||  strcmp(DBstruct.type,'Accumulo')
    T.columnfamily = '';   
  end

  T=class(T,'DBtablePair');

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

