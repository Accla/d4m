function A = double(A)
%double: Converts associative array adjacency matrix to a double.
%Associative array user function.
%  Usage:
%    A = double(A)
%  Inputs:
%    A = associative array
%  Outputs:
%    A = associative array with numeric values

  if isempty(A.val)
    A.A = double(A.A);
  else
    A.val = '';
    A.A = double(A.A);
%    [r c v] = find(A);
%    v(v == v(end)) = ' ';
%    A = Assoc(r,c,str2num(v));
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

