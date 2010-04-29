% Read in reuters files and parse into associatie array.

fdir = 'reuters_entities3/mergedfiles/';

fnames = dir([fdir 'output_*.txt']);


%for i = 1:8
for i = 1:numel(fnames)
  fname = fnames(i).name;
  [As An]=ReutersEntity3Read([fdir fname]);
  [pathstr,name,ext] = fileparts(fname);
  save([fdir name '_A.mat'],'As','An','-v6');
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



