function n1d1  = PowerLawRebin(d0,n0,d1)

      d0Mat = repmat(d0,[numel(d1) 1]);
      d1Mat = repmat(d1,[numel(d0) 1]);
      [tmp imin] = min(abs(d0Mat - d1Mat.'));
      n1d1 = sparse(1,d1(imin),n0);

return
edn