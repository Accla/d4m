function s12 = KronCatStr(s1,sep,s2);
% Concatenates permutations of two arrays of strings with a seperator.

  Ns1 = NumStr(s1);  Ns2 = NumStr(s2);
  s1mat = Str2mat(s1);

  s12 = char(zeros(1,Ns1*Ns2));
  istart = 1;
  for i=1:Ns1
    s12i = CatStr(Mat2str(s1mat(i,:)),sep,s2);
    iend = istart + numel(s12i) - 1;
    s12(istart:iend) = s12i;
    istart = iend + 1;
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

