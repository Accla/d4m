function B = strcmp(arg1,arg2)
%strcmp: Compares the values of an associative array with a scalar.
%Associative array user function.
%  Usage:
%    B = strcmp(arg1,arg2)
%  Inputs:
%    arg1 = associative array or scalar with string value(s)
%    arg2 = associative array or scalar with string value(s)
%  Outputs:
%    B = associative array of all values that are equal to the scalar string

  B = Equallike(arg1,arg2,@strcmp);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

