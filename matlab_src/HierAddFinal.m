function A = HierAddFinal(Ahier);
%HierAddFinal: Finalize hierachical add of associative arrays.
%  Usage:
%     A = HierAddFinal(Ahier);    % Finalize add.
%  Input:
%     Ahier = Cell array of associtative arrays
%  Output:
%     A = final sum of Ahier.

   A = Assoc('','','');
   for i=1:length(Ahier)
     A = A + Ahier{i};
   end

return
end
