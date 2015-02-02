function AA = NoDiag(A)
%NoDiag: Remove diagonal (i.e., A(k,k) = 0) from associative array or matrix. 
%Associative array user function.
%  Usage:
%    AA = NoDiag(A)
%  Inputs:
%    A = associative array
% Outputs:
%    AA = associative array with diagonal removed

  if IsClass(A,'Assoc')
    if ischar(Row(A)) && ischar(Col(A))
      i = StrSubsref(Row(A),Col(A));
      j = StrSubsref(Col(A),Row(A));
      ij = sub2ind(size(A),i,j);
      AdjA = Adj(A);
      AdjA(ij) = 0;
      AA = putAdj(A,AdjA);
    else
      AA = putAdj(A,Adj(A) - diag(diag(A)));
    end
    AA = reAssoc(AA);
  else
    AA = A - diag(diag(A));
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
