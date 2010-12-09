function s = nnz(T)
%SIZE returns number of elements in DB table.
% Use edu.mit.ll.d4m.db.cloud.D4mDbTableOperations

   DB = struct(T.DB);
   tabname = T.name;
  %disp(T);
  if strcmp(DB.type,'BigTableLike')
     ops = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB.instanceName,DB.host,DB.user,DB.pass);

% Need to create a wrapper function that hides Matlab/Octave differences. 

     if exist('OCTAVE_VERSION','builtin')
       tablenameList = java_new('java.util.ArrayList');
     else
       tablenameList = javaObject('java.util.ArrayList');
     end
     tablenameList.add(tabname);
     s = ops.getNumberOfEntries(tablenameList);
  end

  if exist('OCTAVE_VERSION','builtin')
    s = s.toString();
    s = str2num(s);
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

