function ops = DBaddJavaOps(javaClass,instanceName,host,user,pass,varargin)
%DBaddJavaOps: Adds a java database operation.
%Database internal function for DBcreate, DBdelete, DBinsert, DBsubsrefFind
%  Usage:
%    ops = DBaddJavaOps(javaClass,instanceName,host,user,pass,varargin)
%  Inputs:
%    javaClass = java class that operates on database
%    instanceName = database instance name
%    host = database host name
%    user = username on database
%    pass = password on database
%    varargin = function arguments required by ops
% Outputs:
%    ops = java function that is ready to be called on database

if exist('OCTAVE_VERSION','builtin')
    ops=javaObject(javaClass,instanceName,host,user,pass,varargin{:});
 else
   import java.util.*;
   [~, jPath, jFunc] = fileparts(javaClass);
%   import ll.mit.edu.d4m.db.cloud.*;
   import([jPath '.*']);
%   ops = D4mDbTableOperations(host);
   ops = feval(jFunc(2:end),instanceName,host,user,pass,varargin{:});
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu),
%  Mr. Charles Yee (yee@ll.mit.edu), Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

