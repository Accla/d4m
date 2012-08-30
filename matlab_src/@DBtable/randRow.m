function Asub = randRow(T,Ti,Nsub)
%randRow: Randomly selects rows from an associative array or database table.
%Associative array and database utility function.
%  Usage:
%     Asub = randRow(A,Nsub)
%     Asub = randRow(T,Ti,Nsub)
%  Inputs:
%    A = associative array
%    T = database table
%    Ti = database index table
%    Nsub = number of random rows to choose
%  Outputs:
%    Asub = associative array holding randomly selected rows

  % Get Ti parameters.
  ATiPar = double(Ti('IndexParameters,',:));
  N = Val(ATiPar(:,'length,'));
  w = Val(ATiPar(:,'width,'));

  % Generate random row index.  
%  rowIndStr = sprintf('%d,',randi(N,Nsub,1));
  rowIndStr = sprintf('%d,',randiTmp(N,Nsub,1));

  % Get the rows from T via Ti.
% DOESN'T work because can't use () inside a method.
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

