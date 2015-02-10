function s12 = CatStr(s1,sep,s2)
%CatStr: Concatenates two string lists with a separator; inverse of StrSplit.
%String array user function.
%  Usage:
%    s12 = CatStr(s1,sep,s2)
%  Inputs:
%    s1 = string list of length 1 or n
%    sep = single character separator
%    s2 = string list of length 1 or n
%  Outputs:
%    s12 = string by string concatenation of s1, sep, and s2.
%  Example:
%    s12 = CatStr('a,b,c,','|','1,2,3,')


  % Replicate 'scalar' strings.
  Ns1 = NumStr(s1);  Ns2 = NumStr(s2);
  if ((Ns1 == 1) && (Ns2 > 1))
    s1 = repmat(s1,1,Ns2);
  end
  if ((Ns2 == 1) && (Ns1 > 1))
    s2 = repmat(s2,1,Ns1);
  end

  % Convert to matrices.
  s1mat = Str2mat(s1);
  s1mat(s1mat == s1(end)) = sep;
  s2mat = Str2mat(s2);
  [~, N1] = size(s1mat);
  [N, N2] = size(s2mat);

  % Append.
  s12mat = zeros(2*N,max([N1 N2]));
  s12mat(1:2:end,1:N1) = s1mat;
  s12mat(2:2:end,1:N2) = s2mat;
  s12 = Mat2str(s12mat);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

