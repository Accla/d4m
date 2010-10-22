function Asub = DBtableRandRow(T,Ti,Nsub)
%RANDROW returns up to Nsub random subrows of a table.

  % Get Ti parameters.
  ATiPar = str2num(Ti('IndexParameters,',:))
  N = Val(ATiPar(:,'length,'));
  w = Val(ATiPar(:,'width,'));

  % Generate random row index.  
  rowIndStr = sprintf('%d,',randi(N,Nsub,1));

  % Get the rows from T via Ti.
  Asub = T(Col(Ti(rowIndStr,:)),:);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

