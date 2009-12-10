function dispplay(DB)
%DISPLAY shows contents of database.
  disp('Database Object');
  disp(struct(DB));

  % Get all tables in DB.
  tables = ls(DB);
  tabMat = Str2mat(tables);

  % Loop over all tables and create
  % corresponding table objects.
  for i=1:length(tabMat(:,1));
    tabName = deblank(tabMat(i,:));
    eval([tabName ' = DBtable(DB,tabName)']);
  end

  % List tables.
  eval(['whos ' tables]);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
