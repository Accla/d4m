function A = num2str(A)
%num2str: Converts numeric values in an associative array to string values.
%Associative array user function.
%  Usage:
%    A = num2str(A)
%  Inputs:
%    A = associative array with numeric values
%  Outputs:
%    A = associative array with string values
%  Example:
%    T = put(T,num2str(A));     % Convert numerical values for inserting into a database table.

  if isempty(A.val)
    [r, c, v] = find(A);
    A = Assoc(r,c,sprintf('%g,',v));
  else
%    A.A = double(A.A);
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

