% Reads in Reuters Entity data and constructs
% an Associative array.

% Set data dir.
dataDir = 'reuters_entities/';

% Load data.
tic;
  %fid = fopen([dataDir 'orgs_docs.csv'], 'r');
  fid = fopen([dataDir 'all3_docs.csv'], 'r');
  Astr = fread(fid, inf, 'uint8=>char').';
  fclose(fid);
  %fid = fopen([dataDir 'orgs_docs_xy.csv'], 'r');
  fid = fopen([dataDir 'all3_docs_xy.csv'], 'r');
  IDstr = fread(fid, inf, 'uint8=>char').';
  fclose(fid);
readTime = toc


tic;
  % Make all , sep.
  Astr1 = Astr;
  Astr1(Astr1 == Astr1(end)) = ',';
  AstrMat = Str2mat(Astr1);
  Eid = Mat2str(AstrMat(1:3:end,:));
  Did = Mat2str(AstrMat(2:3:end,:));

  % Make all , sep.
  IDstr1 = IDstr;
  IDstr1(IDstr1 == IDstr1(end)) = ',';
  IDstrMat = Str2mat(IDstr1);
  ID = Mat2str(IDstrMat(1:5:end,:));
  catMat = IDstrMat(4:5:end,:);
  %category = Mat2str(IDstrMat(4:5:end,:));
  labelMat = IDstrMat(5:5:end,:);
  %label = Mat2str(IDstrMat(5:5:end,:));

  IDstrMat1 = IDstrMat;
  IDstrMat1(:) = 0;


  % Replace , with /.
  catMat(catMat == ',') = '/';
  IDstrMat1(4:5:end,:) = catMat;
  IDstrMat1(5:5:end,:) = labelMat;
  catlabel = Mat2str(IDstrMat1);


  [IDsort temp catlabelSort]= find(Assoc(ID,1,catlabel));
  catlabelSortMat = Str2mat(catlabelSort);

  i = StrSubsref(IDsort,Did);
  rowStr = Mat2str(catlabelSortMat(i,:));

  j = StrSubsref(IDsort,Eid);
  colStr = Mat2str(catlabelSortMat(j,:));
parseTime = toc


% Create assoc array.
tic
  A = Assoc(rowStr,colStr,1);
assocConstructTime = toc

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

