function snew = StartsWith(s);
%STARTSWITH creates range strings out of s.

  sep = s(end);  % Get seperator.
  % Get min and max string.
%  smin = char(1);
  smin = char(0);
  smax = char(127);

  sMat = Str2mat(s);    % Create matrix.
  sSize = size(sMat);  % Get size;
  snewMat = char(zeros(3*sSize(1),sSize(2)+1));   % Creat new matrix.

  snewMat(1:3:end,:) = Str2mat(strrep(s,sep,[smin sep]));
  snewMat(2:3:end,1) = ':';
  snewMat(2:3:end,2) = sep;
  snewMat(3:3:end,:) = Str2mat(strrep(s,sep,[smax sep]));

  snew = Mat2str(snewMat);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

