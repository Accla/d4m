function s = TsqlSize(T)
%TsqlSize: Returns size of an SQL table.
%Database utility function.
%  Usage:
%    s = TsqlSize(T)
%  Inputs:
%    T = binding to an SQL table
%  Outputs:
%    s = number of rows and number of columns in the table

s = [1 1];

Tstruct = struct(T);
DB = struct(Tstruct.DB);
if strcmp(DB.type,'BigTableLike') || strcmp(DB.type,'Accumulo')
    
end



%Check if SQL or MYSQL database
if strcmp(DB.type,'sqlserver')||strcmp(DB.type,'mysql')
    
    Tstruct.d4mQuery.last();
    s(1) = Tstruct.d4mQuery.getRow();
    
    md = Tstruct.d4mQuery.getMetaData();
    s(2) = md.getColumnCount();
    
end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
% Dr. Vijay Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

