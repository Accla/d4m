function T = put(T,varargin);
%PUT inserts associative array in DB table.
%
%    T  table
%    A  associative array
%
  if nargin == 2
    [row col val] = find(varargin{1});
  end
  if nargin == 4
    row = varargin{1};
    col = varargin{2};
    val = varargin{3};
  end

  T = putTriple(T,row,col,val)

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
