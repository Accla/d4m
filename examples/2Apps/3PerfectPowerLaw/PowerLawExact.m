function [N M] = PowerLawExact(alpha,dmax,Nd);
% Estimate N and M from dmax and Nd.

[di ni] = PowerLawDist(alpha,dmax,Nd);

N = sum(ni);
M = sum(ni.*di);

return
end