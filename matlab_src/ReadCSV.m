function A = ReadCSV(fname);
% READCSB: parse CSV file into an associative array.

  fid = fopen(fname, 'r');
    CsvStr = fread(fid, inf, 'uint8=>char').';
  fclose(fid);

  cr = char(13);  % Carriage return.

  % Put cr on the end if it needs it.
  if (CsvStr(end) ~= cr)
   CsvStr = [CsvStr cr];
  end

  % Find all rows.
  irowend = find(CsvStr == cr);

  % Replcase with ','.
  sep = ',';
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
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

