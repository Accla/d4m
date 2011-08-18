function A = subsref(T, s)
%SUBSREF Get entries from DB table pair.
%   A = T(:, COLKEY) returns an Assoc object representing the subset of the
%   table that matches the COLKEY.
%
%   A = T(ROWKEY, :) retruns an Assoc object representing the subset of the
%   table that matches ROWKEY.
%
%   ROWKEY and COLKEY can take one of the following formats:
%
%       value1,value2,... - Returns every row or column called value1,
%                           value2, ...
%
%       start,:,end,      - Returns every row or column between start and
%                           end inclusive lexographically  by ASCII order.
%                           Standard printable ASCII can be seen with 
%                           char(32:127), but the sequence effectively
%                           starts with ' ' and ends with '~'.
%
%       prefix.*,         - Retruns ever row or column that starts with
%                           prefix. This is very slow as the entire
%                           database is scanned. It is recommended that
%                           instead of 'prefix.*,' you use something to the
%                           effect of 'prefix!,:,prefix~,' as the latter
%                           will be much faster.
%
%   NOTE: Each key is a string, and the last character represents the
%   delimiter to seperate arguments. For these layouts we will use a
%   comma, but in practice you could use any single character.

  row = s.subs{1};
  col = s.subs{2};

  DB = struct(T.DB);


  if ( (numel(col) == 1) && (col == ':') )
    [retRows,retCols,retVals]=DBsubsrefFind(DB.instanceName,DB.host,T.name1,DB.user,DB.pass,row,col, T.columnfamily,T.security,T.numLimit);
    A = Assoc(char(retRows),char(retCols),char(retVals));
  else
    [retRows,retCols,retVals]=DBsubsrefFind(DB.instanceName,DB.host,T.name2,DB.user,DB.pass,col,row, T.columnfamily,T.security, T.numLimit);
    A = Assoc(char(retCols),char(retRows),char(retVals));
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

