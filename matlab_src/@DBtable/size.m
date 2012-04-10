function s = size(T)
%SIZE returns size of table.

  s = [1 1];

  Tstruct = struct(T);
  DB = struct(Tstruct.DB);

  if strcmp(DB.type,'BigTableLike') || strcmp(DB.type,'Accumulo')

  end
  if strcmp(DB.type,'sqlserver')

    s = TsqlSize(T);

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

