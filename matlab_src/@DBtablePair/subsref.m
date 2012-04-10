function varargout = subsref(T, s)
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

  DB = struct(T.DB);

   % If there are arguments, the build a new query.
   if (numel(s.subs) > 0)
     row = s.subs{1};
     col = s.subs{2};
     T.d4mQuery.setCloudType(DB.type);
     T.d4mQuery.setLimit(T.numLimit);
     T.d4mQuery.reset();

     % Pick query direction.
%     if ( (numel(col) == 1) && (col == ':') )
     if ( not(strcmp(row,':')) || strcmp(col,':'))

       T.d4mQuery.setTableName(T.name1);
       T.d4mQuery.doMatlabQuery(row, col, T.columnfamily, T.security);

     else

       T.d4mQuery.setTableName(T.name2);
       T.d4mQuery.doMatlabQuery(col, row, T.columnfamily, T.security);

    end


   end

   % If there are no arguments then run the cached query.
   if (numel(s.subs) == 0)
     T.d4mQuery.next();
   end

   retRows = T.d4mQuery.getRowReturnString;
   retCols = T.d4mQuery.getColumnReturnString;
   retVals = T.d4mQuery.getValueReturnString;

   tablename = char(T.d4mQuery.getTableName());    % Returns the name of the table in the current query.
   if strcmp(tablename,T.name2)
     retRows1 = retRows;
     retRows = retCols;
     retCols = retRows1;
   end

   % Return associative array.
   if (nargout <= 1)
     varargout{1} = Assoc(char(retRows),char(retCols),char(retVals));
   end

   % Return triple.
   if (nargout == 3)
     varargout{1} = char(retRows);
     varargout{2} = char(retCols);
     varargout{3} = char(retVals);
   end

%keyboard

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

