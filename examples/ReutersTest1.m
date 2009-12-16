% Create a DB.
%DB = DBserver('f-2-1.llgrid.ll.mit.edu','cloudbase');
%M = DB('!METADATA');
%T = DB('ReutersData');
%DB


ReutersDir = 'ReutersData/';
ReutersFiles = dir([ReutersDir 'file_1*.txt']);

rows = ''; cols = ''; nl = char(10); sep = ' ';
tic;
  for i = 1:numel(ReutersFiles)
%  for i = 1:30
    fname_i = ReutersFiles(i).name;
    fid = fopen([ReutersDir fname_i],'r');
    text_i = lower(fread(fid,inf,'uint8=>char')).';
    text_i(text_i == nl) = sep;
    text_i(not(isstrprop(text_i,'alphanum'))) = sep;
    text_i = Str2mat(text_i);
    j = (text_i(:,1) ~= sep);
    text_i = Mat2str(text_i(j,:));
    rows = [rows repmat([fname_i sep],1,NumStr(text_i))];
    cols = [cols text_i];
    fclose(fid);
  end
readTime = toc

tic;
  A = Assoc(rows,cols,'1 ');
assocConstructTime = toc

%tic;
%  put(T,A);
%putTime = toc

%tic;
%  AT = T(:,:);
%getTime = toc

%rows ='1,2,3,8,9,10,';
%cols = '1,22,333,88888888,999999999,10101010101010101010,';

%[retRows,retCols,retVals]=DBsubsrefFind(host,db,rows,cols)
