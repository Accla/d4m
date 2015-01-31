function i = StrSearch(s1,s2)
%StrSearch: Finds index location of one string inside another.
%String utility function.
%  Usage:
%    i = StrSearch(s1,s2)
%  Inputs:
%    s1 = list of strings in sorted order
%    s2 = list of strings
%  Outputs:
%    i = index such that s1(i) =< s2 < s1(i+1); i < 1 if not exact match

  % Make seperators the same.
  [s1, s2] = StrSepsame(s1,s2);
  % Convert to matrices.
  s1mat = Str2mat(s1);
  s2mat = Str2mat(s2);

  % Get sizes.
  [N1, M1] = size(s1mat);
  [~, M2] = size(s2mat);
  % Pad so matrices are the same.
  if (M1 > M2)
   s2mat(1,M1) = 0;
  end
  if (M2 > M1)
   s1mat(1,M2) = 0;
  end

  % Concatenate.
  s12mat = [s1mat ; s2mat];
  % Find unique and sort.
  [~,  s12in2out,  s12out2in] = unique(s12mat,'rows','first');
  s1out2in = s12out2in((N1+1):end);
  % If every item in s2 has a match in s1
  % then i = s1out2in.
  i = s1out2in;

  % If not, need to do further parsing.
  if (numel(s12in2out) > N1)
    S2notInS1 = (s12in2out > N1);
    S2inS1 = (s12in2out <= N1);
    iS2inS1 = cumsum(double(S2inS1));
    i = iS2inS1(s1out2in);
    i(S2notInS1(s1out2in)) = -i(S2notInS1(s1out2in));
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

