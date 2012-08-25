function T1 = putNumRow(T0,nlimit);
%putNumRow: STUB. Set the maximum number of rows to return from a single query.
%Database internal function for Iterator.
%  Usage:
%    T1 = putNumRow(T0,nlimit)
%  Inputs:
%    T0 = database table binding
%    nlimit = maximum number rows to return in a single query
% Outputs:
%    T1 = new database table binding with new limit

   T1 = new(T0);
   T1.numRow = nlimit;

end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


