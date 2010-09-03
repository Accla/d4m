function A = subsref(T, s)
%SUBSREF Get entries from DB table.

  row = s.subs{1};
  col = s.subs{2};

  DB = struct(T.DB);

  [retRows,retCols,retVals]=DBsubsrefFind(DB.instanceName,DB.host,T.name,DB.user,DB.pass,row,col);

  A = Assoc(char(retRows),char(retCols),char(retVals));

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

