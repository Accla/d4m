function A = CSVstr2assoc(CsvStr,rowSep,colSep)
%CSVstr2assoc: Converts a CSV formatted string to an associative array.
%IO user function.
%  Usage:
%    A = CSVstr2assoc(CsvStr,rowSep,colSep)
%  Inputs:
%    CsvStr = CSV formatted string
%    rowSep = character to use as the row separator; typically newline (char(10))
%    colSep = character to use as the row separator; typically comma (',')
%  Outputs:
%    A = associative arrays formed from CSV formatted string

  if isempty(CsvStr)
    A = Assoc('','','');
    return;
  end

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

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

