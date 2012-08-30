function Asub = randCol(A,Msub)
%randCol: Randomly selects Msub cols from an associative array.
%Associative array utility function.
%  Usage:
%     Asub = randCol(A,Msub)
%  Inputs:
%    A = associative array
%    Msub = number of random columns to choose
%  Outputs:
%    Asub = associative array holding randomly selected columns

%RANDCOL returns up to Msub random subcols of an associative array.
   [N M] = size(A.A);

   Asub = A;  % Copy input.
   if (Msub < M)
%     isub = unique(randi(M,Msub,1));   % Generate sub-indices.
     isub = unique(randiTmp(M,Msub,1));   % Generate sub-indices.
     Asub.A = Asub.A(:,isub);    % Copy Adj matrix.
     if not(isempty(A.col))
       colMat = Str2mat(A.col);
       Asub.col = Mat2str(colMat(isub,:));    % Copy col keys.
     end
  end

  Asub = reAssoc(Asub);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

