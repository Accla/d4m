% Computes Facets in Reuters Entity data.

% Parse Doc ID into time entity.
tic;
  DocID = Row(A);
  DocIDmat = Str2mat(DocID);
  DocIDwTimeMat = DocIDmat(StrSubsref(DocID,'TXT/199*,'),:);
  [Nt temp] = size(DocIDwTimeMat);
  DocTimeEntMat = repmat('NE_TIME/    -  -  ,',Nt,1);
  DocTimeEntMat(:,[ 9 10 11 12 14 15 17 18]) = DocIDwTimeMat(:,[5 6 7 8 9 10 11 12]);
  DocIDwTime = Mat2str(DocIDwTimeMat);
  DocTime = Mat2str(DocTimeEntMat);

  % Add time entities to A.
  A = A + Assoc(DocIDwTime,DocTime,1);

parseTime = toc; disp(['parseTime = ' num2str(parseTime)]);
