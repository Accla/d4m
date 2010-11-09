function A = IndexAssocFiles(T,filesStr,Ti)
% Inserts Assoc array files into a table and creates a row index at the same time.

filesMat = Str2mat(filesStr);  % Put files into array.

Nrow = 1;

for i = 1:NumStr(filesStr)    % Loop through all files.

  ifile = Mat2str(filesMat(i,:));
  ifile = ifile(1:end-1);      % Extract file name.

  load(ifile);    % Read in .mat file.

  s = size(A); Nrow_i = s(1);  Ncol_i = s(2);

  if (nnz(A) > 0)
    put(T,A);   % Insert into DB table.
    DBtableIndexRow(A,Ti,Nrow);
    Nrow = Nrow + Nrow_i;
  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
