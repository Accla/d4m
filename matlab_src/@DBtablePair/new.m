function T1 = new(T0)
%new: Creates a new table object based on an old table.
%Database table internal function.
%  Usage:
%    T1 = new(T0)
%  Inputs:
%    T0 = original database table object
% Outputs:
%    T1 = new database table object

  Ts = struct(T0);
  T1 = DBtablePair(Ts.DB,Ts.name1,Ts.name2);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

