function TD = delete(T)
%SIZE deletes table from Database.

   TD = T;
   r = input(['Delete ' T.name ' from ' T.DB.host ' ' T.DB.type '? (y/n) [n]']);
   if strcmp(r,'y')
     DBdelete(T.DB.host,T.name);
     TD.name = '';
   end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
