function ops = DBaddJavaOps(javaClass,instanceName,host,user,pass,varargin);

if exist('OCTAVE_VERSION','builtin')
%Do Octave
    ops=java_new(javaClass,instanceName,host,user,pass,varargin{:});
 else
   import java.util.*;
   [temp jPath jFunc] = fileparts(javaClass);
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

