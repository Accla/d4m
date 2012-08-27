function Apair = PairCheck(T,x12,ss)
%PairCheck: DEPRECATED. Checks if a set of string pairs is in a database table. 
%Database utility function.
%  Usage:
%    Apair = PairCheck(T,x12,ss)
%  Inputs:
%    T = database table binding
%    x12 = list of strings where each string is a pair separated by ss
%    ss = pair separator
% Outputs:
%    Apair = associative arrays of pairs found in T

  % Split pair.
  [x1 x2] = SplitStr(x12,ss);

  % Create pair transforms.
  A_x1_x12 = Assoc(x1,x12,1);
  A_x2_x12 = Assoc(x2,x12,1);

  % Get columns from T.
  AT_x1 = double(logical(T(:,x1)));
  AT_x2 = double(logical(T(:,x2)));

  % Find pairs.
  Apair = double( ((AT_x1*A_x1_x12) + (AT_x2*A_x2_x12)) > 1);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%