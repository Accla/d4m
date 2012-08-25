function [row col val] = FindCSVsimple(fname);
%FindCSVsimple: Fast read of a CSV (or TSV) file into simple triples.
%IO user function.
%  Usage:
%    [row col val] = FindCSVsimple(fname)
%  Inputs:
%    fname = CSV or TSV formatted file
%  Outputs:
%    row = array of n row indices
%    col = array of n column indices
%    col = list of n value strings


% Parse CSV file into triples using numeric column and row keys.

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
    CsvStr = CsvStr(CsvStr ~= carriageReturn);
  end

  Nrow = max(NnewLine,NcarriageReturn);

  % Put eol on the end if it needs it.
  if (CsvStr(end) ~= eol)
   CsvStr = [CsvStr eol];
   Nrow = Nrow + 1;
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


  val = CsvStr;
  col =repmat((1:Ncol),[1 Nrow]).';
  row = reshape(repmat((1:Nrow).',[1 Ncol]).',[Nrow.*Ncol 1]);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

