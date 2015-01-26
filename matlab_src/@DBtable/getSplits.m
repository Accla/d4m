function [splitString, varargout] = getSplits(T)
%GETSPLITS gets the current splits for a table
%   OUTPUT splitString: the splits of T in the format f,p,r2,w,
%   OPTIONAL 2nd OUTPUT: a comma-delimited string that holds N+1 numbers
%   where N is the number of splits and the (i)th number is the number of
%   entries in tablet holding the (i-1)st split and the (i)th split.
  T_s = struct(T);
  DB_s = struct(T_s.DB);
  javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
  retArray = javaOp.getSplits(T_s.name);

%  splitString = cell2mat(cell(retArray(1)));
%  splitCount = cell2mat(cell(retArray(2)));
%  splitServer = cell2mat(cell(retArray(3)));

  splitString = char(retArray(1));
  splitCount = char(retArray(2));
  splitServer = char(retArray(3));

  if nargout > 1
    varargout{1} = splitCount;
  end
  if nargout > 2
    varargout{2} = splitServer;
  end

return
end
