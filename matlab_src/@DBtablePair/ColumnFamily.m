function [colFam1 colFam2] = ColumnFamily(T)
%ColumnFamily: Retrieve the column family currently used by a table.
%Database table utility function.
%  Usage:
%    [colFam1 colFam2] = ColumnFamily(T)
%  Inputs:
%    T = database table or table pair object
% Outputs:
%    colFam1 = string containing current column family use for inserts and queries; default is empty
%    colFam2 = string containing current column family use for inserts and queries; default is empty

  colFam1 = T.columnfamily1;
  colFam2 = T.columnfamily2;

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

