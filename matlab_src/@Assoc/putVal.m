function A = putVal(A,val)
%putVal: Replaces the string values in an associative array; does no consistency checking.
%Associative array user function.
%  Usage:
%    A = putVal(A,val)
%  Inputs:
%    A = associative array with M unique string values
%    val = string list of length M
%  Outputs:
%    A = associative array with new string values
%  Example:
%    A = putVal(A,CatStr('Name,','|',Val(A)));    % Prepend 'Name|' to each value.

   A.val = val;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

