function A = put(A,varargin)
%PUT inserts associative array in DB table.
%
%    T  table
%    A  associative array
%

if nargin == 2
    [row, col, val] = find(varargin{1});
elseif nargin == 4
    row = varargin{1};
    col = varargin{2};
    val = varargin{3};
else
    error('Please provide 2 or 4 arguments to insert an Assoc or a triple.')
end

A = putTriple(A,row,col,val);

end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

