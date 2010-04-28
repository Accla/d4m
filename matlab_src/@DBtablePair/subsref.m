function A = subsref(T, s)
%SUBSREF Get entries from DB table pair.

  row = s.subs{1};
  col = s.subs{2};

  DB = struct(T.DB);

  if ( (numel(col) == 1) && (col == ':') )
    [retRows,retCols,retVals]=DBsubsrefFind(DB.host,T.name1,row,col);
    A = Assoc(char(retRows),char(retCols),char(retVals));
  else
    [retRows,retCols,retVals]=DBsubsrefFind(DB.host,T.name2,col,row);
    A = Assoc(char(retCols),char(retRows),char(retVals));
  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
