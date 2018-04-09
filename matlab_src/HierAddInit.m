function Ahier = HierAddInit(nnzCut);
%HierAddInit: Initialize hierachical add of associative arrays.
%  Usage:
%     Ahier = HierAddFinal(nnzCut);    % Initialize add.
%  Input:
%     nnzCut = vector of nnz cut thresholds.
%  Output:
%     Ahier = Cell array of associtative arrays

   for i=1:(length(nnzCut)+1)
     Ahier{i} = Assoc('','','');
   end

return
end
