function dispTypeStats(A,splitSep)
% DISPTYPESTATS display column type sums and covariance.
  [r c v] = find(A);
  [cType cVal] = SplitStr(c,splitSep);
  AA = Assoc(r,cType,1);

  AAsum = sum(AA,1);
  sAA= size(AA);
  disp('Type Sums:');
  displayFull(AAsum.');

  AAcov = (AA.' * AA);
  AAcov = putAdj(AAcov,round(Adj(AAcov)./(sAA(1)./100)));
  disp('Covariance percentages:');
  displayFull(AAcov);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
