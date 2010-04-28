function TD = deleteForce(T)
%DELETEFORCE deletes table from Database without prompting.

   DB = struct(T.DB);

   TD = T;
   DBdelete(DB.host,T.name1);
   TD.name1 = '';
   DBdelete(DB.host,T.name2);
   TD.name2 = '';

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
