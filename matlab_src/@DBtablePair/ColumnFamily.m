function colFam = ColumnFamily(T)
%COLUMNFAMILY gets column family for this table.
%   COLFAM = COLUMNFAMILTY(T)
%
%   This is the column family used for all inserts and retrieves
%   for this pointer to the table.  Different pointers can
%   have different column fomilies.

  colFam = T.columnfamily;

% Need to make NoOp stub for Assoc.

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

