function [tableValueStr] = ls(DB)
%LS lists tables in a DB.

  if strcmp(DB.type,'cloudbase')
     ops = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbInfo',DB.host);
     tableValueStr = char(ops.getTableList());
  end

  if strcmp(DB.type,'mysql')
     % Send SQL command:  SHOW FULL TABLES.

  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

