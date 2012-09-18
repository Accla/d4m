function [di ni] = PowerLawDist(alpha,dmax,Nd);
%PowerLawDist: Compute a perfect power law distribution.
%  Usage:
%     [di ni] = PowerLawDist(alpha,dmax,Nd)
%  Input:
%     alpha = (negative) power law exponent
%     dmax = maximum vertex degree
%     Nd = approximate number of degree bins
%  Output:
%     di = vector of degrees
%     ni = vector of counts

  n1 = dmax.^alpha;
  delta = 1./Nd;

  % Compute power law graph from these parameters.
  n1 = dmax.^alpha;
  logdi = (0:Nd).*log(dmax)./Nd;
  di = round(exp(logdi));
  di = unique(di);
  logn1 = log(dmax).*alpha;
  logni = logn1 - alpha*log(di);
  ni = round(exp(logni));

return
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
