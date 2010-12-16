function T = putSplit(T,rowSplitStr);
%PUTSPLIT sets splits for a table.

  DB = struct(T.DB);

  if strcmp(DB.type,'BigTableLike')

%    DBinsertSplit(DB.instanceName, DB.host, T.name, DB.user, DB.pass, rowSplitStr );

  end

% Need to make equivalent for Assoc?

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

