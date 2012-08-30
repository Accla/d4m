function [row col val] = CatTriple(r,c,v,rr,cc,vv);
%CatTriple: Appends r, c, v and rr, cc, vv; assumes each pair has same type.
%String list user function.
%  Usage:
%    [row col val] = CatTriple(r,c,v,rr,cc,vv)
%  Inputs:
%    r = numeric index array or string list of length n1
%    c = numeric index array or string list of length n1
%    v = numeric index array or string list of length n1
%    rr = numeric index array or string list of length n2
%    cc = numeric index array or string list of length n2
%    vv = numeric index array or string list of length n2
% Outputs:
%    row = numeric index array or string list of length n1+n2
%    col = numeric index array or string list of length n1+n2
%    val = numeric index array or string list of length n1+n2

  if ischar(r)
    row = [r rr];
  else
    row = [r ; rr];
  end

  if ischar(c)
    col = [c cc];
  else
    col = [c ; cc];
  end

  if ischar(v)
    val = [v vv];
  else
    val = [v ; vv];
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

