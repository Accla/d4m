function Asub = DBtableRandRow(T,Ti,Nsub)
%DBtableRandRow: DEPRECATED. Randomly selects rows from a table using a numeric index table.
%Database utility function.
%  Usage:
%     Asub = DBtableRandRow(T,Ti,Nsub)
%  Inputs:
%    T = table to select from
%    Ti = index table
%    Nsub = number of rows to select
%  Outputs:
%    Asub = associative array holding random rows

%RANDROW returns up to Nsub random subrows of a table.

  if IsClass(T,'Assoc')
   Asub = randRow(T,Nsub);
  else
    % Get Ti parameters.
    ATiPar = str2num(Ti('IndexParameters,',:))
    N = Val(ATiPar(:,'length,'));
    w = Val(ATiPar(:,'width,'));

  % Generate random row index.  
%  rowIndStr = sprintf('%d,',randi(N,Nsub,1));
    rowIndStr = sprintf('%d,',randiTmp(N,Nsub,1));

    % Get the rows from T via Ti.
    Asub = T(Col(Ti(rowIndStr,:)),:);
  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

