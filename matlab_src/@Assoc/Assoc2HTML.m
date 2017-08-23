function Assoc2HTML(A,fileName)
%Assoc2CSV: Writes an associative array to an HTML table file.
%IO user function.
%  Usage:
%    Assoc2HTML(A,fileName)
%  Inputs:
%    A = associative array
%    fnameName = HTML file name
%  Outputs:
%    

  TAB = char(9);  NL = char(10);
  tsvStr = Assoc2CSVstr(A,NL,TAB);

  htmlStr = ['<table border="1"><tr><td>' strrep(strrep(tsvStr,NL,['</td></tr>' NL '<tr><td>']),TAB,'</td><td>')];
  htmlStr = [htmlStr(1:end-9) '</table>'];

  fid = fopen(fileName,'w+');
  fwrite(fid,htmlStr);
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

