function TD = delete(T)
%DELETE deletes table from Database.

   DB = struct(T.DB);

   TD = T;
   r = input(['Delete ' T.name1 ' & ' T.name2 ' from ' DB.host ' ' DB.type '? (y/n) [n]: '],'s');
   if strcmp(r,'y')
     DBdelete(DB.host,T.name1);
     TD.name1 = '';
     DBdelete(DB.host,T.name2);
     TD.name2 = '';
   end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
