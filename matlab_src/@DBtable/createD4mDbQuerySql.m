function query = createD4mDbQuerySql(T,conn)
%  createD4mDbQuerySql : Create a D4mDbQuerySql object to execute queries
%  Usage:
%     query = createD4mDbQuerySql(T,conn)
%  Inputs:
%     T = binding to an SQL table
%     conn : java.sql.Connection object
%
%  OUTPUT:
%     query : edu.mit.ll.d4m.db.cloud.sql.D4mDbQuerySql object
%
 
Tstruct = struct(T);
DB = struct(Tstruct.DB);


if strcmp(DB.type,'sqlserver') || strcmp(DB.type,'pgres') || strcmp(DB.type,'mysql')

      if(exist('OCTAVE_VERSION')) %
            query=javaObject('edu.mit.ll.d4m.db.cloud.sql.D4mDbQuerySql');
      else
            query = javaObject('edu.mit.ll.d4m.db.cloud.sql.D4mDbQuerySql');
      end
      query.setConn(conn);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), Dr. Vijay
%    Gadepally (vijayg@ll.mit.edu), Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
