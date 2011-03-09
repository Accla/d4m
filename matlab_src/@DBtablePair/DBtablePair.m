function T = DBtablePair(DB,tablename1,tablename2);
%DBSERVER constructs DBtable object.

  T.DB = DB;
  T.name1 = tablename1;
  T.name2 = tablename2;

  T.security = '';    % Set default security authorizations.

  DBstruct = struct(DB);

  if strcmp(DBstruct.type,'BigTableLike')
    %T.columnfamily = 'vertexFamily';   
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

