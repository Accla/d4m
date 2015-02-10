function [s1, s2] = SplitStr(s12,sep)
%SplitStr: Uncats list of strings into two separate lists of strings; inverse of CatStr.
%String array user function.
%  Usage:
%    [s1,s2] = SplitStr(s12,sep)
%  Inputs:
%    s12 = string by string concatenation of s1, sep, and s2.
%    sep = single character separator
%  Outputs:
%    s1 = string list of length 1 or n
%    s2 = string list of length 1 or n
%  Example:
%    [s1 s2] = SplitStr('a|1,b|2,c|3,','|')

  % Replace sep.
  s12sep = s12(end);
  s12(s12 == sep) = s12sep;

  % Convert to matrix.
  s12mat = Str2mat(s12);

  % Get two halfs.
  s1 = Mat2str(s12mat(1:2:end,:));
  s2 = Mat2str(s12mat(2:2:end,:));

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

