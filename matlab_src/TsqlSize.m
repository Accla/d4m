function s = TsqlSize(T)
%SIZE returns size of table.

  s = [1 1];

  Tstruct = struct(T);
  DB = struct(Tstruct.DB);

  if strcmp(DB.type,'BigTableLike')

  end
  if strcmp(DB.type,'sqlserver')

    [r c v] = T(:,'count(*),');
    s(1) = str2num(v);

    [r c v] = T(1,:);
    s(2) = NumStr(v);

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

