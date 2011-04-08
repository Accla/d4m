% Insert Reuters data into database.

fdir = 'reuters_entities3/mergedfiles/';

fnames = dir([fdir 'output_*_Atrack.mat']);

% Create a DB table.
DbTr = DB('ReutersTracksTEST','ReutersTracksTESTt');
nl = char(10);

%for i = 1:1
for i = 1:numel(fnames)
  tic;
    fname = fnames(i).name;
    disp(fname);
    load([fdir fname],'Tr');
  tic;
    put(DbTr,Tr);
  putTime = toc; disp(['DB put time: ' num2str(putTime)]);
  putRate = nnz(Tr) / putTime; disp(['DB put rate: ' num2str(putRate)]);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

