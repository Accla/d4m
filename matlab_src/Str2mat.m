function mat = Str2mat(str)
%Str2mat: Converts list of strings to char matrix; inverse of Mat2str.
%String array user function.
%  Usage:
%    mat = Str2mat(str)
%  Inputs:
%    str = list of N strings, with longest string of length M.  
% Outputs:
%    mat = NxM char matrix

  sep = str(end);  % Assume last entry is separator.

  % Find the end and start locations of each string.
  strSep = find(str == sep);
  strEnd = strSep - 1;
  strStart = [1 (strSep(1:end-1) + 1)];
  strLen = strEnd - strStart + 1;

  % Compute row index for each character.
  x = double(str);   x(:) = 0;
  x(strSep(1:(end-1))+1) = 1;
  i = cumsum(x) + 1;

  % Compute col index for each character.
  x(:) = 1;
  x(strStart(2:end)) = -strLen(1:(end-1));
  j = cumsum(x);

  % Compute matrix dimensions.
  N = i(end);
  M = max(j);
  mat = char(zeros(N,M,'int8'));

  % Compute offset into matrix.
  ind = i + (j-1)*N;
  mat(ind) = str;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

