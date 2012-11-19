function DB = DBsetupLLGrid(dbname);
%DBsetupLLGrid: Create database binding on LLGrid.
%Database internal function.
%  Usage:
%    DB = DBsetupLLGrid(group)
%  Inputs:
%    group = String containing name group that database lives in.
%  Outputs:
%    DB = database binding

  DBdir = fileparts(mfilename('fullpath'));   % Get DBdir.
  fid = fopen([DBdir '/../../groups/databases/' dbname '/accumulo_user_password.txt']);
    AccumuloUserKey = fgetl(fid);
  fclose(fid);

  fid = fopen([DBdir '/../../groups/databases/' dbname '/dnsname']);
    dnsName = fgetl(fid);
  fclose(fid);

  % Create a DB.  
  DB = DBserver([dnsName ':2181'],'Accumulo',dbname,'AccumuloUser',AccumuloUserKey);

return
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

