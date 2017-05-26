function str = Mat2str(mat);
%Mat2str: Converts char matrix to a list of strings; inverse of Str2mat.
%String array user function.
%  Usage:
%    str = Mat2str(mat)
%  Inputs:
%    mat = char matrix
% Outputs:
%    str = string list of the non-zero entries in mat 

  [i j v] = find(transpose(mat));
  
  if sum(i) == j(end) %Check for a nx1 matrix
    str = char(v); 
  else
    str = char(transpose(v));
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

