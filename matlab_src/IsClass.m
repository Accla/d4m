function y = IsClass(A,name)
%IsClass: Tests if an object is a specific classname.
%Associative array utility function.
%  Usage:
%    y = IsClass(A,name)
%  Inputs:
%    A = variable to test
%    name = class name to compare with
%  Outputs:
%    y = returns 1 if variable class and name are the same

  y = strcmp(class(A),name);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

