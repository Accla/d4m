function T = subsref(DB, s)
%SUBSREF Get/create table from DB.

  subs = s.subs;


  if (numel(subs) == 1)
    table = subs{1};
    % Check if table is in DB.
    if isempty( StrSubsref(ls(DB),[table ' ']) )
      disp(['Creating ' table ' in ' DB.host ' ' DB.type]);
      DBcreate(DB.host,table);  % Create table.
    end
    T = DBtable(DB,table);
  end

  if (numel(subs) == 2)
    table1 = subs{1};
    % Check if tables is in DB.
    if isempty( StrSubsref(ls(DB),[table1 ' ']) )
      disp(['Creating ' table1 ' in ' DB.host ' ' DB.type]);
      DBcreate(DB.host,table1);  % Create table.
    end
    table2 = subs{2};
    % Check if tables is in DB.
    if isempty( StrSubsref(ls(DB),[table2 ' ']) )
      disp(['Creating ' table2 ' in ' DB.host ' ' DB.type]);
      DBcreate(DB.host,table2);  % Create table.
    end
    T = DBtablePair(DB,table1,table2);
  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
