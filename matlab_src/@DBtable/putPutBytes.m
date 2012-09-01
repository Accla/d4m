function T = putPutBytes(T,chunksize)
%putPutBytes: Set the chunk size for table inserts used by the put function.
%Database table internal function.
%  Usage:
%    T = putPutBytes(T,chunksize)
%  Inputs:
%    T = database table binding
%    chunksize = approximate number of bytes to insert at one time
% Outputs:
%    T = database table binding with a new chunk size

  T.putBytes = chunksize;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

