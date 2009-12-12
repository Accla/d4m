function A = subsref(T, row, col)
%SUBSREF Get endtries DB table.

  DB = struct(T.DB);

  [retRows,retCols,retVals]=DBsubsrefFind(DB.host,T.name,row,col);

  A = Assoc(char(retRows),char(recCols),char(retVals));

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
