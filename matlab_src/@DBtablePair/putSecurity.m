function T = putSecurity(T,secStr);
%putSecurity: Set the security label currently used by a table.
%Database table utility function.
%  Usage:
%    T = putSecurity(T,secStr)
%  Inputs:
%    T = database table object
%    secStr = string containing new security string to used for inserts and queries; default is empty
% Outputs:
%    T = database table object with a new security string

  T.security = secStr;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

