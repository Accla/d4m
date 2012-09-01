function TD = delete(T)
%delete: Deletes a table from a database; prompts user before proceeding.
%Database table user function.
%  Usage:
%    delete(T)
%    TD = delete(T)
%  Inputs:
%    T = database table or table pair object
% Outputs:
%    TD = empty database table or table pair object

   DB = struct(T.DB);

   TD = T;
   r = input(['Delete ' T.name1 ' & ' T.name2 ' from ' DB.host ' ' DB.type '? (y/n) [n]: '],'s');
   if strcmp(r,'y')
     DBdelete(DB.instanceName,DB.host,T.name1,DB.user,DB.pass,DB.type);
     TD.name1 = '';
     DBdelete(DB.instanceName,DB.host,T.name2,DB.user,DB.pass, DB.type);
     TD.name2 = '';
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

