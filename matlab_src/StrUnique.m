function  [strUniq in2out out2in] = StrUnique(str,firstlast);
%STRUNIQUE finds unique strings in string array.
  % row : array of separated strings
  % firstlast : optional argument to unique function

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

