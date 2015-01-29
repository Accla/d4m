function [row, col, val] = catFind(r,c,v,A)
%catFind: Appends triples from an associative array to another set of triples; assumes each pair has same type.
%String list user function.
%  Usage:
%    [row col val] = catFind(r,c,v,A)
%  Inputs:
%    r = numeric index array or string list of length n1
%    c = numeric index array or string list of length n1
%    v = numeric index array or string list of length n1
%    A = associative array with n2 entries
% Outputs:
%    row = numeric index array or string list of length n1+n2
%    col = numeric index array or string list of length n1+n2
%    val = numeric index array or string list of length n1+n2

  % Get indices.
  [row, col, val] = find(A);
  [row, col, val] = CatTriple(r,c,v,row,col,val);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

