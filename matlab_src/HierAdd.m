function Ahier = HierAdd(Ahier,A,nnzCut);
%HierAdd: Hierachicall add associative arrays in way that is more memory friendly.
%  Usage:
%     Ahier = HierAdd(Ahier,A,nnzCut);    % Compute out-degree
%  Input:
%     Ahier = Cell array of associtative arrays
%     A = new associative array to add.
%     nnzCut = nnz thresholds for each level of the hierarchy
%  Output:
%     Ahier = Updated cell array.

   Ahier{1} = Ahier{1} + A;
   for i=1:length(nnzCut)
     if (nnz(Ahier{i}) > nnzCut(i))
       Ahier{i+1} = Ahier{i+1} + Ahier{i};
       Ahier{i} = Assoc('','','');
     end
   end

return
end
