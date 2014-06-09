function [tableName tableSchema] = SplitSciDBstr(table)
%SplitSciDBstr: splits SciDB table description into table and schema.
%Database internal function.
%  Usage:
%    [tableName tableSchema] = SplitSciDBstr(table)
%  Inputs:
%    table = SciDB table description string
% Outputs:
%    tableName = SciDB table name
%    tableSchema = SciDB table schema
%  Example:
%    [tableName tableSchema] = SplitSciDBstr(table)

  ilt = strfind(table,'<');                             % Look for schema seperator. 
  if nnz(ilt)
    tableName = table(1:(ilt(1)-1));                      % Get table name.
    tableSchema = table(ilt(1):end);                      % Get schema.
  else
    tableName = table;
    tableSchema = '';
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

