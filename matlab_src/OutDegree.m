function ndout = OutDegree(A);
%OutDegree: Compute out-degree distribution of an adjacency matrix.
%  Usage:
%     ndout = OutDegree(A);    % Compute out-degree
%     ndin = OutDegree(A.');   % Compute in-degree
%  Input:
%     A = adjacency matrix
%  Output:
%     ndout = sparse vector where ndout(d) is the count of vertices of degree d

  dout = sum(A,2);
  [dout_i dout_j dout_v] = find(dout);
  ndout = sum(sparse(dout_i,dout_v,1),1);
return
end