function Assoc2CSV(A,rowSep,colSep,fileName)
%Assoc2CSV: Writes an associative array to a CSV file.
%IO user function.
%  Usage:
%    Assoc2CSV(A,rowSep,colSep,fileName)
%  Inputs:
%    A = associative array
%    rowSep = character used to separate rows; usually newline (char(10))
%    colSep = character used to separate columns; usually comma (',')
%    fnameName = CSV file name
%  Outputs:
%    

  CsvStr = Assoc2CSVstr(A,rowSep,colSep);

  fid = fopen(fileName,'w+');
  fwrite(fid,CsvStr);
  fclose(fid);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

