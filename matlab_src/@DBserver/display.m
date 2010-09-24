function display(DB)
%DISPLAY shows contents of database.
  disp('Database Object');
  DBstr = struct(DB);
  if isfield(DBstr,'pass')
    DBstr = rmfield(DBstr,'pass');
  end
  disp(DBstr);

  % Get all tables in DB.
  tables = ls(DB);
  tabMat = Str2mat(tables);
  tabMat1 = tabMat;

  if strcmp(DB.type,'cloudbase')
    % Fix !METADATA
    i = StrSubsref(tables,['!METADATA' tables(end)]);
    if (i > 0)
      tabMat1(i,:) = 0;
      tabMat1(i,1:9) = ['METADATA' tables(end)];
    end
  end

  if strcmp(DB.type,'mysql')
     % Parse data from mysql response.
     
  end


  % Loop over all tables and create
  % corresponding table objects.
  for i=1:length(tabMat(:,1));
    tabName = deblank(tabMat(i,:));
    tabName1 = deblank(tabMat1(i,:));
    tmp = [tabName1 ' = DBtable(DB,tabName);'];
    eval(tmp);
  end

  tables = Mat2str(tabMat1);

  % List tables.
  disp('Tables in database:');
  eval(['whos ' tables]);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

