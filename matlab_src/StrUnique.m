function  [strUniq in2out out2in] = StrUnique(str,firstlast)
%StrUnique: Finds unique strings in string array and index mapping between input and output.
%String utility function.
%  Usage:
%    [strUniq in2out out2in] = StrUnique(str,firstlast)
%  Inputs:
%    str = list of strings
%    firstlast = optional argument, typically is 'first' or 'last'
%  Outputs:
%    strUniq = list of unique strings in sorted order
%    in2out = index mapping of input strings to unique output strings
%    out2in = index mapping of unique output strings to input strings

  if (nargin == 1)
     firstlast = 'first';
  end

  mat = Str2mat(str);  % Convert to matrix.
%  mat = char(full(Str2sparse(str)));  % Convert to matrix.

  % Uniq and sort rows of matrix.
  [matUniq  in2out  out2in] = unique(mat,'rows',firstlast);

  strUniq = Mat2str(matUniq);  % Convert to string.

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

