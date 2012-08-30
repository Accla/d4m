function B = ne(arg1,arg2)
%~=,ne: Compares the values of an associative array with a scalar.
%Associative array user function.
%  Usage:
%    B = arg1 ~= arg2
%    B = ne(arg1,arg2)
%  Inputs:
%    arg1 = associative array or scalar numeric or string value
%    arg2 = associative array or scalar numeric or string value
%  Outputs:
%    B = associative array of all values that are not equal to the scalar value

%NE runs ~= operator on an associative array and a scalar.
  B = Equallike(arg1,arg2,@ne);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

