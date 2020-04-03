function query = sqlCreateStatement(T,conn)
% sqlCreateStatement : Returns SQL Statement object from SQL Connection object
%Database utility function.
%  Usage:
%    query = sqlCreateStatement(T,conn)
%  Inputs:
%    T = binding to an SQL table
%    conn = java.sql.Connection object
%  Outputs:
%    query = java.sql.Statement object

Tstruct = struct(T);
DB = struct(Tstruct.DB);


if strcmp(DB.type,'sqlserver') || strcmp(DB.type,'pgres') || strcmp(DB.type,'mysql') %added pgres --sid

        if(exist('OCTAVE_VERSION')) %not working yet (VG)
           % Octave does not appear to recognize java.sql.ResultSet as class.
	   % In Java, java.sql.ResultSet is an interface,not a class 
	   %  java.sql.ResultSet.TYPE_SCROLL_SENSITIVE = 1005
	   %  java.sql.ResultSet.CONCUR_READ_ONLY = 1007
	   TYPE_SCROLL_SENSITIVE= 1005;
	   CONCUR_READ_ONLY = 1007;
           query = ...
                conn.createStatement( ...
                TYPE_SCROLL_SENSITIVE,CONCUR_READ_ONLY);

	else
            import java.sql.ResultSet;
            import java.sql.Statement;
        
            query = ...
                conn.createStatement(java.sql.ResultSet ...
                .TYPE_SCROLL_SENSITIVE,java.sql.ResultSet.CONCUR_READ_ONLY);
        
        end 

end

