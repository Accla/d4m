if not(exist('DB', 'var'))
    global DB;
    
    % Create a DB.  
    %DB = DBserver('classdb01.cloud.llgrid.ll.mit.edu:2181','Accumulo','accumulo',user,password);

	instanceName = 'txg-scaletest-4n32c';
	host = 'txg-scaletest-4n32c.cloud.llgrid.ll.mit.edu:2181';
	username = 'AccumuloUser';
	password = 'ZcIn_EBHdVzF5J4Dvn3AIpwck';
DB = DBserver(host,'Accumulo',instanceName,username,password);
    %DB = DBsetupLLGrid('classdb06');
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

