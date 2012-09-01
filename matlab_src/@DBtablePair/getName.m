function [name1,name2] = getName(T)
%getName: Retrieve the names of the table.
%Database table utility function.
%  Usage:
%    [name1,name2] = getName(T)
%  Inputs:
%    T = database table pair object
% Outputs:
%    name1 = string containing the table name
%    name1 = string containing the table transpose name

   name1 = T.name1;
   name2 = T.name2;

end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
