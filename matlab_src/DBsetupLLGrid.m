function DB = DBsetupLLGrid();
  DBdir = fileparts(mfilename('fullpath'));   % Get DBdir.
  fid = fopen([DBdir '/.AccumuloUserKey']);
    AccumuloUserKey = fgetl(fid);
  fclose(fid);

  % Extract DNS entry file path.
  [tmp dnsName tmp] = fileparts(fileparts(DBdir));
  dnsName = strrep(dnsName,'_share','');

  % Create a DB.  
  DB = DBserver([dnsName '.llgrid.ll.mit.edu:2181'],'Accumulo','accumulo', 'AccumuloUser',AccumuloUserKey);

return
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

