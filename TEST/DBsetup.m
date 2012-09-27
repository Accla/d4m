if not(exist('DB', 'var'))
    global DB;
    
    % Create a DB.  
    %DB = DBserver('llgrid-db-00.llgrid.ll.mit.edu:2181','Accumulo','accumulo',user,password);
    %DB = DBserver('d4muser.llgrid.ll.mit.edu:2181','Accumulo','accumulo',user,password);
    DB = DBsetupD4Muser;       % Create binding to D4Muser.
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
% FOUO
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

