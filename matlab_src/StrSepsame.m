function [s1 s2] = StrSepsame(s1,s2)
%StrSepsame: Makes separators in two lists of strings the same.
%String utility function.
%  Usage:
%    [s1 s2] = StrSepsame(s1,s2)
%  Inputs:
%    s1 = list of strings
%    s2 = list of strings
%  Outputs:
%    s1 = list of strings
%    s2 = list of strings where separator is the same as s1

  s1sep = s1(end);  % Assume last entry is separator.
  s2sep = s2(end);

  if (s1sep ~= s2sep);
    s2(s2 == s2sep) = s1sep;
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

