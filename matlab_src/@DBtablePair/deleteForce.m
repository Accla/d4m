function TD = deleteForce(T)
%DELETEFORCE deletes table from Database without prompting.
%   DELETEFORCE(T) removes DBtablePair T from the database

   DB = struct(T.DB);

   TD = T;
   DBdelete(DB.instanceName,DB.host,T.name1,DB.user,DB.pass,DB.type);
   TD.name1 = '';
   DBdelete(DB.instanceName,DB.host,T.name2,DB.user,DB.pass, DB.type);
   TD.name2 = '';

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
