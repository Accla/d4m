function DB = DBsetupSciDB(dbname)

username = 'SciDBUser';
hostname = ['http://' dbname '.cloud.llgrid.txe1.mit.edu:8080/'];
DBdir = fileparts(mfilename('fullpath'));   % Get DBdir.
fid = fopen([DBdir '/../../../tools/groups/databases/' dbname '/scidb_shim_password.txt']);
scidbShimPassword = fgetl(fid);
fclose(fid);

DB = DBserver(hostname,'scidb','',username, scidbShimPassword);  % Creates binding.

return 
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), Dr. Vijay
% Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


