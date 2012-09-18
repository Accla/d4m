function v = EdgesFromDist(di,ni);
%EdgesFromDist: Compute start vertex of edge (half an edge) from a degree distribution.
%  Usage:
%     v = EdgesFromDist(di,ni)
%  Input:
%     di = vector of degrees
%     ni = vector of counts
%  Output:
%     v = vector of vertices

  Nd_bar = numel(di);
  A1 = sparse(1:Nd_bar,ni,di);
  A2 = fliplr(cumsum(fliplr(A1),2));
  [tmp tmp d] = find(A2);
  A3 = sparse(1:numel(d),d,1);
  A4 = fliplr(cumsum(fliplr(A3),2));
  [v tmp tmp] = find(A4);

return
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
