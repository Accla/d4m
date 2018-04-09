function ndbin = BinDegree(nd,dstep);
%OutDegree: Compute out-degree distribution of an adjacency matrix.
%  Usage:
%     ndbin = BinDegree(nd,bin_step);    % Compute out-degree
%  Input:
%     nd = degree distribution matrix
%     dstep = logarithmic bin step size.
%  Output:
%     ndbin = sparse vector where ndbin(d) is the count of vertices of degree d

  dMax = length(nd);
  dMaxBin =  round(dstep.^(ceil(log(dMax)./log(dstep))));
  ndbinNNZ = round(log(dMaxBin)./log(dstep));
  ndbin = spalloc(dMaxBin,1,ndbinNNZ);
  ndbin(1) = nd(1);
  
  if (ndbinNNZ > 1)
    for i = 1:ndbinNNZ
      di0 = dstep.^(i-1);
      di1 = dstep.^i;
      ndbin(di1) = sum(nd((di0+1):min(di1,dMax)));
    end
  end


return
end
