function query = createD4mDbQuerySql(T,conn)
%  Create a D4mDbQuerySql object to execute query
%  conn : java.sql.Connection object
%
%  OUTPUT:
%     query : D4mDbQuerySql object
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
