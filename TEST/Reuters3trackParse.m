
% Read in reuters files and parse into associatie array.
fdir = 'reuters_entities3/mergedfiles/';
fnames = dir([fdir 'output_*_A.mat']);

nl = char(10);

% Specify track keys.
t=['TIME/*' nl];    l=['NE_LOCATION/*' nl];   p=['NE_PERSON*' nl];

for i = 1:numel(fnames)
  tic;
    fname = fnames(i).name;
    disp(fname);
    load([fdir fname],'As');
    [r c v] = find(As);
    r(r == r(end)) = nl;  c(c == c(end)) = nl;  v(v == v(end)) = nl;
    A = Assoc(r,c,v);
  convertTime = toc; disp(['Convert time: ' num2str(convertTime)]);
  tic;
    Tr = Reuters3FindTracks(A,t,p,l);
  trackTime = toc; disp(['Track time: ' num2str(trackTime)]);
  [pathstr,name,ext] = fileparts(fname);
  save([fdir name 'track.mat'],'Tr','-v6');
end



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



