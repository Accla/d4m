function T = put(T,A);
%PUT inserts associative array in DB table.
%
%    T  table
%    A  associative array
%
  % Set chunk size in chars.
  chunkBytes = 5e5;
  M = nnz(A);

  [row col val] = find(A);


% !!! DB does not exist.

  % For MySQL, check/create column names.
if 0
  if strcmp(DB.type,'mysql')
     % Get list of all column names in table: tableColNameStr.
     % SQL command:  SELECT name FROM syscolumns
     %                 WHERE id = (SELECT id FROM sysobjects
     %                 WHERE name= T.name)
     %                 ORDER by colorder

     % Sort tableColNameStr.
     %[tableColNameStrUniq in2out out2in] = StrUnique(tableColNameStr);

     % Compare list with unique colInputString
     %AcolNameStr = Col(A);
     %i = StrSearch(tableColNameStrUniq,AcolNameStr);
     % Where i < 0, there is no match.
     % Create these columns in the table.
     %iNoMatch = find(i < 0);
     %if not(isempty(iNoMatch))
     %  newTableColNameStr = StrSubsref(AcolNameStr,iNoMatch);
     %  Send SQL commands.
     %     ALTER TABLE T.name
     %     ADD newTableColNameStr varchar(20)
     %end

     % Handling the Primary Key.
     % Four cases:
     % (1)  A.row is a string and SQL table has no primary key
     %      Create a column called "PRIMARY_KEY".  Go to (2).
     % (2)  A.row is a string and SQL table has a primary key
     %      Make sure row key is inserted in primary key column
     % (3)  A.row is empty and SQL does not have a primary key.
     %      Append data to table without any key.
     % (4)  A.row is empty and SQL does have a primary key.
     %      Throw an error?

  end
end

  rowMat = Str2mat(row);
  colMat = Str2mat(col);
  valMat = Str2mat(val);
  [temp rowMax] = size(rowMat);
  [temp colMax] = size(colMat);
  [temp valMax] = size(valMat);

  totMax = rowMax + colMax + valMax;
  chunkSize = min(max(1,round(chunkBytes/totMax)),M);

  DB = struct(T.DB);

  for i=1:chunkSize:M
  insert_t = tic;
    i1 = min(i + chunkSize - 1,M);
    r = Mat2str(rowMat(i:i1,:)); c = Mat2str(colMat(i:i1,:));  v = Mat2str(valMat(i:i1,:));

    DBinsert(DB.instanceName, DB.host, T.name, DB.user, DB.pass, r, c, v, T.columnfamily, T.security,DB.type);
  insert_t = toc(insert_t);  disp(['Insert time: ' num2str(insert_t)]);

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

