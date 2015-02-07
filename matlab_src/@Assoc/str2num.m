function A = str2num(A)
%str2num: Converts string values in an associative array to numeric values.
%Associative array user function.
%  Usage:
%    A = str2num(A)
%  Inputs:
%    A = associative array with string values
%  Outputs:
%    A = associative array with numeric values
%  Example:
%    A = str2num(T(:,:));     % Convert to numerical values after retrieving strings from a database table.

  if isempty(A.val)
%    A.A = double(A.A);
  else
    [r, c, v] = find(A);
    v(v == v(end)) = ' ';
    A = Assoc(r,c,str2num(v));
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

