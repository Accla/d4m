function str = StrLS(pathname)
%StrLS: Returns list of files in a directory formatted as a string list.
%IO user function.
%  Usage:
%    str = StrLS(pathname)
%  Inputs:
%    pathname = directory path
%  Outputs:
%    str = list of files in a directory formatted as a string list

% Returns list of files formatted as string array.

  if exist('OCTAVE_VERSION','builtin')
     str = ls(pathname);
     str(:,end+1) = char(10);
     str = reshape(str.',1,numel(str));
     str = str(str ~= 0);
  else
    str = strrep([strtrim(ls(pathname)) char(10)],char(9),char(10));
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

