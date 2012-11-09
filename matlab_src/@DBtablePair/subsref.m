function varargout = subsref(T, s)
%(),subsref: Selects rows and columns from an associative array or database table.
%Associative array and database user function.
%  Usage:
%    Asub = A(row,col)
%    [r c v] = A(row,col)
%  Inputs:
%    A = associative array or database table
%    col = row keys to select; can be also be numeric if A is an associative array
%    col = column keys to select; can be also be numeric if A is an associative array
%  Outputs:
%    Asub = sub associative array of all the non-empty rows and columns
%  Examples:
%    row and col can take one of the following formats:
%       :                 - Returns every value
%       value1,value2,... - Returns every row or column called value1, value2, ...
%       start,:,end,      - Returns every row or column between start and
%                           end inclusive lexographically (! comes before
%                           all alpha numberic values, and ~ comes after
%                           all alpha numeric values).
%       prefix.*,         - Returns ever row or column that starts with
%                           prefix. This is can ve slow. It is generally
%                           recommended that instead of 'prefix.*,' you use
%                           something to the effect of 'prefix!,:,prefix~,'
%                           as the latter will be much faster. The full
%                           operation of the * operatior is not entirely
%                           known.
%   NOTE: Each key is a string, and the last character represents the
%   delimiter to separate arguments. For these layouts we will use a
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
       T.d4mQuery.doMatlabQuery(row, col, T.columnfamily1, T.security);

     else

       T.d4mQuery.setTableName(T.name2);
       T.d4mQuery.doMatlabQuery(col, row, T.columnfamily2, T.security);

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

