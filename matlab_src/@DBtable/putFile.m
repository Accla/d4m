function T = putFile(T,bulkfile);
%STUB for inserting a native DB file into the table.

  DB = struct(T.DB);

  if strcmp(DB.type,'BigTableLike') || strcmp(DB.type, 'Accumulo')

%    DBinsertFile(DB.instanceName, DB.host, T.name, DB.user, DB.pass, bulkfile );

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

