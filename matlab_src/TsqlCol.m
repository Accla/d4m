function retCols = TsqlCol(T)
%TsqlCol: Returns column names in an SQL table.
%Database utility function.
%  Usage:
%    retCols = TsqlCol(T)
%  Inputs:
%    T = binding to an SQL table
%  Outputs:
%    retCols = string list of table column names

%SIZE returns column names of table.

  retCols = '';  nl = char(10);
  Tstruct = struct(T);
  DB = struct(Tstruct.DB);

  if strcmp(DB.type,'BigTableLike') || strcmp(DB.type,'Accumulo')

  end
  if strcmp(DB.type,'sqlserver')

    md = Tstruct.d4mQuery.getMetaData();
    numCols = md.getColumnCount();
    for j=1:numCols
      jcol = char(md.getColumnName(j));
      if isempty(jcol)
        jcol = sprintf('%d',j);
      end
      retCols = [retCols jcol nl];
    end

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

