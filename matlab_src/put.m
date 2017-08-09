function T = put(T,varargin);
%put: Inserts data into a database table.
%Database user function.
%  Usage:
%    T = put(T,A)
%    T = put(T,r,c,v)
%  Inputs:
%    T = database table binding
%    A = Associative array to insert into table (normally has string rows, columns, and values).
%    r = string list of n row keys
%    c = string list of n column keys
%    v = string list of n values
%  Outputs:
%    T = database table binding

  if nargin == 2
    [row col val] = find(varargin{1});
  end
  if nargin == 4
    row = varargin{1};
    col = varargin{2};
    val = varargin{3};
  end

  T = putTriple(T,row,col,val);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
