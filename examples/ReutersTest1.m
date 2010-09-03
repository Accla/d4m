% Create a DB.
DB = DBserver('f-2-9.llgrid.ll.mit.edu', 'cloudbase', 'cloudbase', 'root', 'secret');
M = DB('!METADATA');
T = DB('ReutersData');
DB


ReutersDir = 'ReutersData/';
ReutersFiles = dir([ReutersDir 'file_*.txt']);
Nfiles = numel(ReutersFiles);
disp(['# files: ' num2str(Nfiles)]);

% Preallocate buffers.
tic;
  rows = char(zeros(1,1e8));
  cols = rows;
bufferAllocTime = toc

nl = char(10); sep = ' ';
irow0 = 1;  icol0 = 1;
tic;
  for i = 1:Nfiles
%  for i = 1:30
    fname_i = ReutersFiles(i).name;
    fid = fopen([ReutersDir fname_i],'r');
    text_i = lower(fread(fid,inf,'uint8=>char')).';
    fclose(fid);
    text_i(text_i == nl) = sep;
    text_i(not(isstrprop(text_i,'alphanum'))) = sep;
    text_i = Str2mat(text_i);
    j = (text_i(:,1) ~= sep);
    text_i = Mat2str(text_i(j,:));
    row_i = repmat([fname_i sep],1,NumStr(text_i));
    icol1 = icol0 + numel(text_i) - 1;
    irow1 = irow0 + numel(row_i) - 1;
    rows(irow0:irow1) = row_i;
    cols(icol0:icol1) = text_i;

%    rows = [rows repmat([fname_i sep],1,NumStr(text_i))];
%    cols = [cols text_i];
    irow0 = irow1 + 1;
    icol0 = icol1 + 1;
  end
  rows = rows(1:(irow0-1));
  cols = cols(1:(icol0-1));
readTime = toc

tic;
  A = Assoc(rows,cols,'1 ');
assocConstructTime = toc

tic;
%  put(T,A(1:1000,:));
putTime = toc

tic;
%  AT = T(:,:);
getTime = toc

