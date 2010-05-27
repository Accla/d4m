% Insert Reuters data into database.

fdir = 'reuters_entities3/mergedfiles/';

fnames = dir([fdir 'output_*_A.mat']);

% Create a DB.
DB = DBserver('f-2-4.llgrid.ll.mit.edu','cloudbase');
%[stat,host] = system('hostname -s');
%DB = DBserver([host(1:end-1) '.llgrid.ll.mit.edu'],'cloudbase');
%T = DB('ReutersData');
T = DB('ReutersData','ReutersDataT');
%T = DB('ReutersDataTest','ReutersDataTestT');
nl = char(13);

%for i = 1:1
%for i = 1:numel(fnames)
for i = 2:numel(fnames)
  tic;
    fname = fnames(i).name;
    disp(fname);
    load([fdir fname],'As');
%    put(T,As);
%   Change seperator to nl (since | causes an error).
    [r c v] = find(As);
    r(r == r(end)) = nl;  c(c == c(end)) = nl;  v(v == v(end)) = nl;
    A = Assoc(r,c,v);
  convertTime = toc; disp(['Convert time: ' num2str(convertTime)]);
  tic;
    put(T,A);
  putTime = toc; disp(['DB put time: ' num2str(putTime)]);
  putRate = nnz(As) / putTime; disp(['DB put rate: ' num2str(putRate)]);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



