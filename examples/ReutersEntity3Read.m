function [A An]=ReutersEntity3Read()
% Reads in Reuters Entity data and constructs
% an Associative array.

  % Set data dir.
  dataDir = 'reuters_entities3/';


tic;    
  [vA vB vC vD vE vF vG vH vI vJ vK vL vM vN vO vP vQ vR vS vT vU vV vW vX vY vZ ...
  vAA vAB vAC vAD vAE vAF vAG vAH vAI vAJ vAK vAL vAM vAN vAO vAP vAQ vAR vAS vAT] ...
   = ParseFile([dataDir '10k_reuters_extracted_content.txt'],'|');
readTime = toc; disp(['Read time: ' num2str(readTime)]);

tic;
  docID = vA;  charPos = vH; docName = vV;   entCat = vAD;  ent = vAE;

  % Find document boundaries.
  docIDmat = Str2mat(docID);
  docIDdiff = sum(abs(docIDmat(1:end-1,:) - docIDmat(2:end,:)),2);
  i = find(docIDdiff < 1) + 1;

  % Eliminate first row of doc.
  charPosMat = Str2mat(charPos); charPos = Mat2str(charPosMat(i,:));
  docNameMat = Str2mat(docName); docName = Mat2str(docNameMat(i,:));
  entCatMat = Str2mat(entCat); entCat = Mat2str(entCatMat(i,:));
  entMat = Str2mat(ent); ent = Mat2str(entMat(i,:));

  % Convert character position to a numeric vector.
  charPos0 = charPos;
  charPos(charPos == charPos(end)) = ' ';  charPosNum = str2num(charPos) + 1;

  % Concatenate entity categories and entities.
  entity = CatStr(entCat,'/',ent);
parseTime = toc; disp(['Parse time: ' num2str(parseTime)]);

tic;
  % Construct associative array.
  A = Assoc(docName,entity,charPos0,@AssocCatStrFunc);
  An = Assoc(docName,entity,1,@sum);
assocTime = toc; disp(['Assoc time: ' num2str(assocTime)]);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


