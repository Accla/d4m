function [K alphaStar] = ComputeDegreeCorrection(n1Sample,dmaxSample,fSample)
%ComputeDegreeCorrection: compute K coefficient that can be used correct sub-samples from a power law distribution.
%  Usage:
%     [K alphaStar] = ComputeDegreeCorrection(n1Sample,dmaxSample,fSample)
%  Inputs:
%    n1sample = number with degree 1 in sample
%    dmaxSample = maximum degree in sample
%    fSample = ratio of sample edges to total edges
%  Outputs:
%    K = coefficient for correcting sample degrees
%    alphasStar = estimate of power law slope of total sample

n1hat = n1Sample;   dmaxhat = dmaxSample;   Ntime = 1./fSample;

dhat = (1:10).';   % Set degree grid.

% Compute alpha range grid for finding alpha that best fits median.
alphamin = log(n1hat)./log(dmaxhat.*Ntime);
alphamax = log(n1hat)./log(dmaxhat);
alphagrid = alphamin:0.01:alphamax;
dhatMat = repmat(dhat,size(alphagrid));

alphaMat = repmat(alphagrid,size(dhat));  % Compute joint grid.

PDF_dhatalpha = ((1./Ntime).^(1 - alphaMat) .* dmaxhat.^alphaMat ./ n1hat) .* (  dhatMat.^(1 - alphaMat) .* exp(- (1./Ntime) .* dhatMat));

CDF_Dhatalpha = cumsum(((1./Ntime).^(1 - alphaMat) .* dmaxhat.^alphaMat ./ n1hat) .* (  dhatMat.^(1 - alphaMat) .* exp(- (1./Ntime) .* dhatMat)),1);

for i_alpha = 1:numel(alphagrid);
  CDF_i_Dhatalpha = CDF_Dhatalpha(:,i_alpha);
  di_med_alpha(i_alpha) = interp1(CDF_i_Dhatalpha,dhat,0.5);
end

[tmp i_alpha_min] = min(abs(CDF_Dhatalpha(end,:) - 1));


fNtime0est = 1./(1 + (log(di_med_alpha(i_alpha_min))./log(1./Ntime)));

K = fNtime0est;
alphaStar = alphagrid(i_alpha_min);

return
end