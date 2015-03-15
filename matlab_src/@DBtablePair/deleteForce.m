function TD = deleteForce(T)
%deleteForce: Deletes a table from a database; does *not* prompt user before proceeding.
%Database table user function.
%  Usage:
%    deleteForce(T)
%    TD = deleteForce(T)
%  Inputs:
%    T = database table or table pair object
% Outputs:
%    TD = empty database table or table pair object

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
