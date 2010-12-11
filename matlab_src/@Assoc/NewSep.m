function A = NewSep(A,s)
%NEWSEP replaces all string seperators in A.

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

