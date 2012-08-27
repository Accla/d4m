function A = InsertAssocFiles(T,filesStr)
%InsertAssocFiles: Inserts Assoc array files into a database table.
%Database utility function.
%  Usage:
%    A = InsertAssocFiles(T,filesStr)
%  Inputs:
%    T = table to insert into
%    fileStr = string list of filenames
%  Outputs:
%    A = associative array of the last file read

filesMat = Str2mat(filesStr);  % Put files into array.


for i = 1:NumStr(filesStr)    % Loop through all files.

  ifile = Mat2str(filesMat(i,:));
  ifile = ifile(1:end-1);      % Extract file name.

  load(ifile);    % Read in .mat file.

  if (nnz(A) > 0)
    put(T,A);   % Insert into DB table.
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
