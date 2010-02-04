function s12 = CatStr(s1,sep,s2);
%CATSTR cats 2 array of strings.

  s1mat = Str2mat(s1);
  s1mat(s1mat == s1(end)) = sep;
  s2mat = Str2mat(s2);
  [N N1] = size(s1mat);
  [N N2] = size(s2mat);

  s12mat = zeros(2*N,max([N1 N2]));
  s12mat(1:2:end,1:N1) = s1mat;
  s12mat(2:2:end,1:N2) = s2mat;
  s12 = Mat2str(s12mat);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
