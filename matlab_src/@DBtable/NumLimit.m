function numLimit = NumLimit(T)
%NumLimit: Gets the maximum number of items to return from a single query.
%Database table internal function for Iterator.
%  Usage:
%    numLimit = NumLimit(T)
%  Inputs:
%    T = database table binding
% Outputs:
%    numLimit = maximum number items to return in a single query

  numLimit = T.numLimit;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

