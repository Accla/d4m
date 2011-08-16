if not(exist('DB', 'var'))
    global DB;
    
    % Create a DB.  
    DB = DBserver('f-2-8.llgrid.ll.mit.edu','BigTableLike','cloudbase');
    %DB = DBserver('f-2-10.llgrid.ll.mit.edu','BigTableLike','cloudbase','root', drowssap);   % 1.2
    %DB = DBserver('f-2-15.llgrid.ll.mit.edu','BigTableLike','cloudbase','root',drowssap);   % 1.2
    %DB = DBserver('unity3d.llgrid.ll.mit.edu','BigTableLike','cloudbase','root',drowssap);   % 1.2
    %[stat,host] = system('hostname -s');
    %DB = DBserver([host(1:end-1) '.llgrid.ll.mit.edu'],'BigTableLike','cloudbase','root',drowssap); 

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

