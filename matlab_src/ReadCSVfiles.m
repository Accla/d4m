function A = ReadCSVfiles(filesStr)
%ReadCSVfiles: Reads multiple CSV (or TSV) files into an associative array.
%IO user function.
%  Usage:
%    A = ReadCSVfiles(filesStr)
%  Inputs:
%    filesStr = string list of CSV or TSV formatted file names
%  Outputs:
%    A = combined associative array of files, with 1st column as row keys, and 1st row as column keys

filesMat = Str2mat(filesStr);  % Put files into array.

row = '';  col = ''; val = '';

for i = 1:NumStr(filesStr)    % Loop through all files.


  ifile = Mat2str(filesMat(i,:));
  ifile = ifile(1:end-1);      % Extract file name.

  [rowi coli vali] = FindCSV(ifile);

  [row col val] = CatTriple(row,col,val,rowi,coli,vali);

end

A = Assoc(row,col,val);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
