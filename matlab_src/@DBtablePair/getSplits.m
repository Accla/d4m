function [splitString, varargout] = getSplits(T, whichTable)
%GETSPLITS gets the current splits for a table
%   OUTPUT splitString: the splits of T in the format f,p,r2,w,
%   OPTIONAL 2nd OUTPUT: a comma-delimited string that holds N+1 numbers
%   where N is the number of splits and the (i)th number is the number of
%   entries in tablet holding the (i-1)st split and the (i)th split.
%whichTable is either 'first' or 'transpose'
T_s = struct(T);
DB_s = struct(T_s.DB);
if strcmpi(whichTable,'first')
    Table = T_s.name1;
elseif strcmpi(whichTable,'transpose')
    Table = T_s.name2;
else
    error('Please specify whichTable correctly');
end
javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
if nargout <= 1
    splitString = javaOp.getSplits(Table);
else
    %user wants the count in each tablet
    retArray = javaOp.getSplits(Table, true);
    splitString = cell2mat(cell(retArray(1)));
    varargout(1) = cell(retArray(2));
end
