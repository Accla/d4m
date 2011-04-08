function T = putTriple(T,r,c,v);
%PUT inserts triples and its transpose in DB tables.

  DB = struct(T.DB);
  DBinsert(DB.instanceName, DB.host, T.name1, DB.user, DB.pass, r, c, v, T.columnfamily, T.security );
  DBinsert(DB.instanceName, DB.host, T.name2, DB.user, DB.pass, c, r, v, T.columnfamily, T.security );    % Insert transpose.

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

