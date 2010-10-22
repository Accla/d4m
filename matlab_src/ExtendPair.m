function Ax12o_x12k2 = ExtendPair(Ax12o_x12,k1,ss,T,k2);
% 3rd arg is splitSep.  Can use to check which side of pair we are working with.

% TODO
% Make ExtendPair work for left/right pair choices.   Will '(1)//*|,'  work for 1st part of pair?
% Make work for multiple input and output key types.

  Ax12o_x12k1 = Ax12o_x12(:,k1);      % Get columns satisfying k1.
  [x1k1 x2k1] = SplitStr(Col(Ax12o_x12k1),ss);      % Split column key pairs.
  if (k1(1) == ss)
    xk1 = x2k1;    % k1 is 2nd.
  else
    xk1 = x1k1;    % k1 is 1st.
  end
  Ax12k1_xk1 = Assoc(Col(Ax12o_x12k1),xk1,1);    % Create transformation from column key pairs to k1 key.
  AT_xk1 = T(:,xk1);
  AT = T(Row(AT_xk1),:);      % Get all rows containing a k1.
  AT_xk2 = AT(:,k2);     % Limit to k2.
%  [tmp xk1_xk2 tmp] = find(AT(Row(sum(AT,2) == 2),:));     % Get column keys of rows containing k1 and k2.
%  [xk1 xk2] = SplitStr(xk1_xk2, xk1_xk2(end));        % "split" column keys.  Exploits fact that previous find should interleave desired results.  Depends upon specifics of key.
%  Cxk1_xk2 = Assoc(xk1,xk2,1);   % Create transformation from k1 to k2.
  Cxk1_xk2 = (AT_xk1.' * AT_xk2);   % Create transformation from k1 to k2.
  [x12k1 xk2 tmp] = find(Ax12k1_xk1 * Cxk1_xk2);   % Apply transformation and get k1 row key pairs and k2 column keys.
  [x1k1 x2k1] = SplitStr(x12k1,ss);   % Split row key pairs.
  if (k1(1) == ss)  % Pair choice.
    Dx12k1_x12k2 = Assoc(x12k1,CatStr(x1k1,'|', xk2),1);      % Create transformation from k1 row key pairs to k2 column key pairs.
  else
    Dx12k1_x12k2 = Assoc(x12k1,CatStr(xk2,'|', x2k1),1);     % Create transformation from k1 row key pairs to k2 column key pairs.
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
