function Ax12o_x12k2 = ExtendPair(Ax12o_x12,k1,ss,T,k2,left,colTclut);
%ExtendPair: DEPRECATED. Complex analytic for extending pairs of keys. 
%Database utility function.
%  Usage:
%    Ax12o_x12k2 = ExtendPair(Ax12o_x12,k1,ss,T,k2,left,colTclut);
%  Inputs:
%    Ax12o_x12 = input associative array of pairs
%    k1 = left side of pair key type(s)
%    ss = string separator
%    T = database table binding
%    k2 = right side of pair key type(s)
%    left = direction flag
%    colTClut = list of column keys to avoid
% Outputs:
%    Ax12o_x12k2 = associative arrays of new pairs

  right = 1;
  if left
    right = 0;
  end

  [x1 x2] = SplitStr(Col(Ax12o_x12),ss);      % Split column key pairs.
  Ax1_x2 = Assoc(x1,x2,1);   % Create assoc mapping column key pairs.
  if left
    Ax1_x2 = Ax1_x2(k1,:);   % Apply k1 filter to left side of pair.
  else
    Ax1_x2 = Ax1_x2(:,k1);   % Apply k1 filter to right side of pair.
  end

  [x1k1 x2k1 tmp] = find(Ax1_x2);    % Get pairs that remain after k1 filter.
  if (numel(x1k1) < 1)
     Ax12o_x12k2 = Assoc('','','');
     return;
  end

  x12k1 = CatStr(x1k1,ss,x2k1);      % Recombine pairs.
  Ax12o_x12k1 = Ax12o_x12(:,x12k1);      % Get columns satisfying k1.

  [x1k1 x2k1] = SplitStr(Col(Ax12o_x12k1),ss);      % Split column key pairs.
  if left
    xk1 = x1k1;    % k1 is 1st.
  else
    xk1 = x2k1;  % k1 is 2nd.
  end
  Ax12k1_xk1 = Assoc(Col(Ax12o_x12k1),xk1,1);    % Create transformation from column key pairs to k1 key.
  AT_xk1 = T(:,xk1);
  AT = T(Row(AT_xk1),:);      % Get all rows containing a k1.
  AT = AT - AT(:,colTclut);   % Remove clutter columns.

  AT_xk2 = AT(:,k2);    % Limit to k2.

  Cxk1_xk2 = transpose(AT_xk1) * AT_xk2;   % Create transformation from k1 to k2.
  [x12k1 xk2 tmp] = find(Ax12k1_xk1 * Cxk1_xk2);   % Apply transformation and get k1 row key pairs and k2 column keys.
  if (numel(x12k1) < 1)
     Ax12o_x12k2 = Assoc('','','');
     return;
  end

  [x1k1 x2k1] = SplitStr(x12k1,ss);   % Split row key pairs.
  if right  % Pair choice.
    Dx12k1_x12k2 = Assoc(x12k1,CatStr(x1k1,ss, xk2),1);      % Create transformation from k1 row key pairs to k2 column key pairs.
  else
    Dx12k1_x12k2 = Assoc(x12k1,CatStr(xk2,ss, x2k1),1);     % Create transformation from k1 row key pairs to k2 column key pairs.
  end
  Ax12o_x12k2 = Ax12o_x12k1 * Dx12k1_x12k2;   % Apply transformation.

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
