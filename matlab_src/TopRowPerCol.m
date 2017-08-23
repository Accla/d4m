function Atop = TopRowPerCol(A,Ntop)
%TopRowPerCol: Picks the top valued rows for each column.
%Associative user function.
%  Usage:
%    Atop = TopRowPerCol(A,Ntop)
%  Inputs:
%    A = associative array
%    Ntop = maximum number of top values to return per column
%  Outputs:
%    Atop = associative array where there are no more than Ntop entries per column

% Picks Ntop rows for each column.

   Asize = size(A);

   if (Asize(1) <= Ntop)
     Atop = A;
     return
   end

   AdjA = Adj(A);
   [orderV order] = sort(AdjA,1,'descend');
   orderTop = order(1:Ntop,:);
   orderVtop = orderV(1:Ntop,:);
   orderTop = orderTop(logical(orderVtop));
   [tmp tmp r] = find(orderTop);
   [tmp c v] = find(orderVtop);
   AdjAtop = sparse(r,c,v);
%   Atop = reAssoc(putAdj(A,AdjAtop));
   Atop = putAdj(A,AdjAtop);
   Atop = Atop(unique(r),:);


end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%