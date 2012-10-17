function [row col val] = FindCSV(fname);
%FindCSV: Reads a CSV (or TSV) file into triples.
%IO user function.
%  Usage:
%    [row col val] = FindCSV(fname)
%  Inputs:
%    fname = CSV or TSV formatted file
%  Outputs:
%    row = list of n row strings
%    col = list of n column strings
%    col = list of n value strings

  fid = fopen(fname, 'r');
    CsvStr = fread(fid, inf, 'uint8=>char').';
  fclose(fid);

  % eol = [char(10) char(13)];  % DOS
  newLine = char(10);  % Newline/linefeed (UNIX).
  carriageReturn = char(13);  % Carriage return (Mac).

  NnewLine = numel(find(CsvStr == newLine));
  NcarriageReturn = numel(find(CsvStr == carriageReturn));

  if (NnewLine > NcarriageReturn)
    eol = newLine;
  end
  if (NnewLine < NcarriageReturn)
    eol = carriageReturn;
  end
  if (NnewLine == NcarriageReturn)
    eol = newLine;
    CsvStr = CsvStr(CsvStr~=carriageReturn);
  end


  % Put eol on the end if it needs it.
  if (CsvStr(end) ~= eol)
   CsvStr = [CsvStr eol];
  end

  % Find all rows.
  irowend = find(CsvStr == eol);

  % Set seperator ','.
  sep = ',';

  [dir file ext] = fileparts(fname);
  if strcmp(ext,'.tsv')
    sep = char(9);  % tab
  end

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

%  A = Assoc(row,col,val);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

