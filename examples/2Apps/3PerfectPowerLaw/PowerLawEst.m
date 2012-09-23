function [N M] = PowerLawEst(alpha,dmax,Nd);
% Estimate N and M from dmax and Nd.

n1 = dmax.^alpha;
delta = 1./Nd;

Nc = (log(1) - log(1 - 1./dmax.^delta))./(log(dmax.^delta));

%Nc = round(Nc);

dc = dmax.^(delta .* Nc);
%dc = round(dc);

%M = (dc - 1 + Nd - Nc).*dmax;
M = (dc - 0 + Nd - Nc).*dmax;  % ????

psi1 = -0.577215664901533;       % psi(1) = -(euler constant).
Hn = log(dc-1) - psi1 + 1./(2.*(dc-1));  % Closed form approximation.  
%Hn = sum(1./(1:floor(dc-1))) + (dc -1 - floor(dc-1)).*(1./ceil(dc-1));   % Direct sum.
if (alpha ~= 1)
  Hn = ((dc-1).^(1-alpha) - 1)./(1 - alpha) - psi1 + 1./(2.*(dc-1));  % Closed form approximation.
  %Hn = sum(1./(1:floor(dc-1)).^alpha) + (dc -1 - floor(dc-1)).*(1./ceil(dc-1).^alpha);  % Direct sum.
  Hn2 = ((dc-1).^(2-alpha) - 1)./(2 - alpha) - psi1 + 1./(2.*(dc-1));  % Closed form approximation.
  %Hn2 = sum(1./(1:floor(dc-1)).^(1-alpha)) + (dc -1 - floor(dc-1)).*(1./ceil(dc-1).^(1-alpha));  % Direct sum.

  M = n1.*Hn2 + dmax + n1.*(dmax.^(Nc.*(1-alpha).*delta) - dmax.^(1-alpha))./(1 - dmax.^((1-alpha).*delta));

end

N = n1.*Hn + (n1.^(1 - delta.*Nc) - 1)./(1 - (1./n1.^delta));

i1 = (Nc < 0.6);
if 0
  N1 = 1 + (n1 - 1)./(1 - 1./n1.^delta);
  M1 = (Nd+1).*n1;  % If alpha = 1;
  if (alpha ~= 1)
    M1 = dmax + (n1 - dmax)./(1 - dmax.^(delta.*(1-alpha)));
  end
  N(i1) = N1(i1);
  M(i1) = M1(i1);
  N(i1) = 0;
  M(i1) = 0;
  
end

return
end