function si = StrSubind(s,i)
%StrSubind: Returns sub-strings i found in string list s.
%String utility function.
%  Usage:
%    si = StrSubind(s,i)
%  Inputs:
%    s = list of strings
%    i = indices of strings to get from list
%  Outputs:
%    si = list of sub-strings from s (in the order i).

  smat = Str2mat(s);
  si = Mat2str(smat(i,:));
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

