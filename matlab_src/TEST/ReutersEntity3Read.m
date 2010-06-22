function [As An]=ReutersEntity3Read(fname)
% Reads in Reuters Entity data and constructs
% an Associative array.

  % Set data dir.
%  fname = 'reuters_entities3/mergedfiles/output_03.txt';


tic;    
%  [vA vB vC vD vE vF vG vH vI vJ vK vL vM vN vO vP vQ vR vS vT vU vV vW vX vY vZ ...
%  vAA vAB vAC vAD vAE vAF vAG vAH vAI vAJ vAK vAL vAM vAN vAO vAP vAQ vAR vAS vAT] ...
%   = ParseFile([dataDir '10k_reuters_extracted_content.txt'],'|');
%  [vA vF vH vV vAD vAE] ...
%   = ParseFileCols([dataDir '10k_reuters_extracted_content.txt'],'|',46,[1 6 8 22 30 31]);
  [vA vF vH vV vAD vAE] ...
   = ParseFileCols(fname,'|',46,[1 6 8 22 30 31]);
readTime = toc; disp(['Read time: ' num2str(readTime)]);

tic;
  docID = vA;  sourceText = vF; charPos = vH; docName = vV;   entCat = vAD;  ent = vAE;


  % Find document boundaries.
  docIDmat = Str2mat(docID);
  docIDdiff = sum(abs(docIDmat(1:end-1,:) - docIDmat(2:end,:)),2);
  i = find(docIDdiff < 1) + 1;

  % Eliminate first row of doc entry.
  charPosMat = Str2mat(charPos); charPos = Mat2str(charPosMat(i,:));
  docNameMat = Str2mat(docName); docName = Mat2str(docNameMat(i,:));
  entCatMat = Str2mat(entCat); entCat = Mat2str(entCatMat(i,:));
  entMat = Str2mat(ent); ent = Mat2str(entMat(i,:));
  sourceTextMat = Str2mat(sourceText); sourceText = Mat2str(sourceTextMat(i,:));

  % Convert character position to a numeric vector.
  sep = charPos(end);
  charPos(charPos == charPos(end)) = ' ';  charPosNum = str2num(charPos) + 1;
  charPos = sprintf(['%d' sep],charPosNum);

  % Concatenate entity categories and entities.
  entity = CatStr(entCat,'/',ent);

  % Find entity doubles.
%  entityMat = Str2mat(entity);
%  entityDiff = sum(abs(entityMat(iover,:) - entityMat(iover+1,:)),2);
%  ient = find(entityDiff < 1));

  % Compute source text length.
  sourceTextMat = Str2mat(sourceText);
  sourceTextLen = (sum(sourceTextMat > 0,2) - 1).';

  % Find overlaps.
  iover = find((charPosNum(1:end-1) + sourceTextLen(1:end-1) -1) - charPosNum(2:end) > 0);

  % Find overlaps that resolve to the same entity.
  entityMat = Str2mat(entity);
  entityDiff = sum(abs(entityMat(iover,:) - entityMat(iover+1,:)),2);
  ii = iover(find(entityDiff < 1))+1;
  i = ones(1,NumStr(entity));  i(ii) = 0;  i = find(i);


  % Eliminate overlaps.
  charPosMat = Str2mat(charPos); charPos = Mat2str(charPosMat(i,:));
  docNameMat = Str2mat(docName); docName = Mat2str(docNameMat(i,:));
  entityMat = Str2mat(entity); entity = Mat2str(entityMat(i,:));
  sourceTextMat = Str2mat(sourceText); sourceText = Mat2str(sourceTextMat(i,:));

parseTime = toc; disp(['Parse time: ' num2str(parseTime)]);

tic;
  % Construct associative array.
  As = Assoc(docName,entity,charPos,@AssocCatStrFunc);
  An = Assoc(docName,entity,1,@sum);
assocTime = toc; disp(['Assoc time: ' num2str(assocTime)]);



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

