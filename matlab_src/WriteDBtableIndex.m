function  WriteDBtableIndex(T,filebase,Ti,NrowBlock)
%WriteDBtableIndex: DEPRECATED. Uses and index table to write out a table.
%Database utility function.
%  Usage:
%    WriteDBtableIndex(T,filebase,Ti,NrowBlock)
%  Inputs:
%    T = table
%    filebase = beginning name of file
%    Ti = index table
%    NrowBlock = number of rows to put in each file
%  Outputs:
%    

% Uses and index table to write out a table.

  % Get Ti parameters.
  ATiPar = str2num(Ti('IndexParameters,',:))
  N = Val(ATiPar(:,'length,'));
  w = Val(ATiPar(:,'width,'));

  % Loop through all rows in blocks.
  for i = 1:NrowBlock:N 

    % Generate row index range.
    i1str = sprintf('%d,',i);
    i2str = sprintf('%d,',i+NrowBlock);
    rowIndStr = [i1str ':,' i2str];

    % Get the rows from T via Ti.
    A = T(Col(Ti(rowIndStr,:)),:);

    % Save into a file.
    if (nnz(A) > 0)
      ifile = [filebase i1str '.mat'];
      save(ifile,'A','-v6');   % Save file.
    end

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
