function T = putColumnFamily(T,colFam)
%putColumnFamily: Set the column family currently used by a table.
%Database table utility function.
%  Usage:
%    T = putColumnFamily(T,colFam)
%  Inputs:
%    T = database table object
%    colFam = string containing new column family to be used for inserts and queries; default is empty
% Outputs:
%    T = database table object with a new column family string

  T.columnfamily = colFam;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

