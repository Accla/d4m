function [s1 s2] = SplitStr(s12,sep);
%SPLITSTR uncats array of strings.

  % Replace sep.
  s12sep = s12(end);
  s12(s12 == sep) = s12sep;

  % Convert to matrix.
  s12mat = Str2mat(s12);

  % Get two halfs.
  s1 = Mat2str(s12mat(1:2:end,:));
  s2 = Mat2str(s12mat(2:2:end,:));

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

