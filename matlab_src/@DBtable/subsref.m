function varargout = subsref(T, s)
%SUBSREF Get entries from DB table.

  DB = struct(T.DB);

   % If there are arguments, the build a new query.
   if (numel(s.subs) > 0)
     row = s.subs{1};
     col = s.subs{2};

     T.d4mQuery.setLimit(T.numLimit);
     T.d4mQuery.reset();
     T.d4mQuery.doMatlabQuery(row, col, T.columnfamily, T.security);
   end

   % If there are no arguments the run the cached query.
   if (numel(s.subs) == 0)
     T.d4mQuery.next();
   end

   retRows = T.d4mQuery.getRowReturnString;
   retCols = T.d4mQuery.getColumnReturnString;
   retVals = T.d4mQuery.getValueReturnString;


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

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

