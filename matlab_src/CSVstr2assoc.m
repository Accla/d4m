function A = CSVstr2assoc(CsvStr,rowSep,colSep)
%CSVstr2assoc converts a CSV formatted string to an associative array.

  % Put row seperator on the end if it needs it.
  if (CsvStr(end) ~= rowSep)
   CsvStr = [CsvStr rowSep];
  end

  % Find all rows.
  irowend = find(CsvStr == rowSep);

  % Replace with column separator.
  sep = colSep;
  CsvStr(irowend) = sep;

  % Get first row.
  firstRowMat = Str2mat(CsvStr(1:irowend(1)));

  % Get size based on first row.
  [Ncol temp] = size(firstRowMat);  

  % Get col keys and vals.
  colValMat = Str2mat(CsvStr(irowend(1)+1:end));

  rowKeyMat = colValMat(1:Ncol:end,:);
  [Nrow temp] = size(rowKeyMat);

  % Initialize
  row = '';  col = ''; val = '';

  % Loop through each row.
  for i=2:Ncol
    colName = Mat2str(firstRowMat(i,:));
    irowKeyMat = colValMat(i:Ncol:end,:);
    iok = find(irowKeyMat(:,1) ~= sep);
    row = [row Mat2str(rowKeyMat(iok,:))];
    col = [col repmat(colName,[1 numel(iok)])];
    val = [val Mat2str(irowKeyMat(iok,:))];
  end

  A = Assoc(row,col,val);

end

