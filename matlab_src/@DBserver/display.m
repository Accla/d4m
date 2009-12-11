function display(DB)
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
    tabName1 = tabName;
    % Fix !METADATA
    if (strcmp(tabName,'!METADATA'))
      tabMat(i,:) = 0;
      tabMat(i,1:9) = ['METADATA' tables(end)];
      tabName1 = deblank(tabMat(i,:));
    end
    tmp = [tabName1 ' = DBtable(DB,tabName);'];
    eval(tmp);
  end

  tables = Mat2str(tabMat);

  % List tables.
  disp('Tables in database:');
  eval(['whos ' tables]);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
