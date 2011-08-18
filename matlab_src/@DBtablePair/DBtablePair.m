function T = DBtablePair(DB,tablename1,tablename2)
%DBTABLEPAIR Table reference to a pair of transposed tables
%   T = DBTABLEPAIR(DB, TABLE1, TABLE2) retruns a table reference to the
%   paired tables TABLE1 and TABLE2 where TABLE1 = TABLE2.'.
%
%   T = DB(TABLE1, TABLE2) is equivalent to the above.
%
%   See also: DBTABLEPAIR/DELETE,
%             DBTABLEPAIR/NNZ,
%             DBTABLEPAIR/PUT,
%             DBTABLEPAIR/PUTTRIPLE,
%             DBTABLEPAIR/RANDROW,
%             DBTABLEPAIR/SUBSREF

  T.DB = DB;
  T.name1 = tablename1;
  T.name2 = tablename2;

  T.security = '';    % Set default security authorizations.
  T.numLimit = 0;  % max number of results to return from a query; default 0 - return all
  DBstruct = struct(DB);

  if strcmp(DBstruct.type,'BigTableLike')
    %T.columnfamily = 'vertexFamily';   
    T.columnfamily = '';   
  end
  
  T=class(T,'DBtablePair');

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

