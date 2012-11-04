function A = NewSep(A,s)
%NewSep: Replaces string separator in all string lists.
%Associative array utility function.
%  Usage:
%    A = NewSep(A,s)
%  Inputs:
%    A = associative array
%    s = single character string separator; typically newline (char(10)) or comma (',')
%  Outputs:
%    A = associative array where the separator in all row, column, and values strings is set to s.

   if not(isempty(A.row))
     str = A.row;   str(str==str(end)) = s;    A.row = str;
   end

   if not(isempty(A.col))
     str = A.col;   str(str==str(end)) = s;    A.col = str;
   end

   if not(isempty(A.val))
     str = A.val;   str(str==str(end)) = s;    A.val = str;
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

