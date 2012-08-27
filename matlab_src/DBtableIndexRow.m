function Ti = DBtableIndexRow(T,Ti,iStart)
%DBtableIndexRow: DEPRECATED. Create a numeric row index for a table and puts it into another table.
%Database utility function.
%  Usage:
%    Ti = DBtableIndexRow(T,Ti,iStart)
%  Inputs:
%    T = table to index
%    Ti = index table
%    iStart = starting point of index
%  Outputs:
%    Ti = index table

% Create row index table for T.

  Trow = Row(T(:,:));  % Get all row keys.
  sep = Trow(end);   % Get separator.
  Nrow = NumStr(Trow);  % Get number.
%  TrowInd = 1:Nrow;  % Create index values.
  TrowInd = iStart:(iStart + Nrow - 1);  % Create index values.
  TrowIndStr = sprintf(['%d' sep],TrowInd);   % Convert to string.

  % Create associative array with parameter row.
%  Ai = Assoc(TrowIndStr,Trow,'1,') ...
%       + Assoc(['IndexParameters' sep],['length' sep 'width' sep], ...
%               [num2str(Nrow) sep num2str(length(sprintf('%d',Nrow))) sep]);

  Ai = Assoc(TrowIndStr,Trow,'1,') ...
       + Assoc(['IndexParameters' sep],['length' sep ], ...
               [num2str(Nrow) sep ]);

  % Insert values.
  Ti = put(Ti,Ai);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
