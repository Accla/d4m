function T = putColumnFamily(T,varargin)
%putColumnFamily: Set the column family currently used by a table.
%Database table utility function.
%  Usage:
%    T = putColumnFamily(T,colFam1)
%    T = putColumnFamily(T,colFam1,colFam2)
%  Inputs:
%    colFam1 = string containing new column family to be used for inserts and queries; default is empty
%    colFam2 = string containing new column family to be used for inserts and queries; default is empty
% Outputs:
%    T = database table object with a new column family string

  if (nargin == 2)
    T.columnfamily1 = varargin{1};
    T.columnfamily2 = varargin{1};
  end
  if (nargin == 3)
    T.columnfamily1 = varargin{1};
    T.columnfamily2 = varargin{2};
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

