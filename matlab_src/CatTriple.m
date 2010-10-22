function [row col val] = CatTriple(r,c,v,rr,cc,vv);
%CATTRIPLE Appends r, c, v and rr, cc, vv. Asummes all have same type.

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

