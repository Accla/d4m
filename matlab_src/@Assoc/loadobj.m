function b = loadobj(a)
%loadobj: Used to load associative array from a file.
%Associative internal function used by load.
%  Usage:
%    b = loadobj(a)
%  Inputs:
%    a = saved associative array data
%  Outputs:
%    b = associative array object

  b = Assoc(1,1,1);
  b.row = a.row;  b.col = a.col;  b.val = a.val;  b.A = a.A;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

