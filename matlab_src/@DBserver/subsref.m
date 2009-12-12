function T = subsref(DB, s)
%SUBSREF Get/create table from DB.

  table = s.subs{1};

  % Check if table is in DB.
  if (StrSubsref(ls(DB),[table ' ']) < 1)
    disp(['Creating ' table ' in ' DB.host ' ' DB.type]);
    DBcreate(DB.host,table);  % Create table.
  end

  T = DBtable(DB,table);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
