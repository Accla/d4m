function Atop = TopColPerRow(A,Ntop)
%TopColPerRow: Picks the Ntop valued columns for each row.
%Associative user function.
%  Usage:
%    Atop = TopColPerRow(A,Ntop)
%  Inputs:
%    A = associative array
%    Ntop = maximum number of top values to return per row
%  Outputs:
%    Atop = associative array where there are no more than Ntop entries per row

   Asize = size(A);

   if (Asize(2) <= Ntop)
     Atop = A;
     return
   end

   AdjA = Adj(A);
   [tmp order] = sort(AdjA,2,'descend');
   orderTop = order(:,1:Ntop);

   AdjAtop = AdjA;
   AdjAtop(:) = 0;

   for i = 1:Asize(1);
      AdjAtop(i,orderTop(i,:)) = AdjA(i,orderTop(i,:));
   end

   Atop = reAssoc(putAdj(A,AdjAtop));

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%