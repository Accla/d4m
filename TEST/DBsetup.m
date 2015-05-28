if not(exist('DB', 'var'))
    global DB;
    
    % Create a DB.  
    %DB = DBserver('classdb01.cloud.llgrid.ll.mit.edu:2181','Accumulo','accumulo',user,password);
    %DB = DBsetupLLGrid('classdb51','/usr/local/tools');
    DB = DBserver('localhost:2181','Accumulo','instance','root','secret');
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

