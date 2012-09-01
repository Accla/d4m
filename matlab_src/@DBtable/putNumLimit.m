function T1 = putNumLimit(T0,nlimit);
%putNumLimit: Set the maximum number of items to return from a single query.
%Database table internal function for Iterator.
%  Usage:
%    T1 = putNumLimit(T0,nlimit)
%  Inputs:
%    T0 = database table binding
%    nlimit = maximum number items to return in a single query
% Outputs:
%    T1 = new database table binding with new limit

   T1 = new(T0);
   T1.numLimit = nlimit;

end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


