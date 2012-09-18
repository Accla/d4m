function [N M] = PowerLawExactMulti(alpha,dmax,Nd);
% Estimate N and M from dmax and Nd.


N = Nd;  N(:) = 0;  M = N;

for i=1:numel(dmax(:,1))
  for j=1:numel(Nd(1,:))
     [N(i,j) M(i,j)] = PowerLawExact(alpha,dmax(i,j),Nd(i,j));
  end
end

return
end