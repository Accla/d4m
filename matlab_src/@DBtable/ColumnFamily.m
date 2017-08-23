function colFam = ColumnFamily(T)
%ColumnFamily: Retrieve the column family currently used by a table.
%Database table utility function.
%  Usage:
%    colFam = ColumnFamily(T)
%  Inputs:
%    T = database table or table pair object
%  Outputs:
%    colFam = string containing current column family use for inserts and queries; default is empty

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

