function dEst = ApplyDegreeCorrection(dSample,fSample,K)
%ApplyDegreeCorrection: correct sampled degree using K coefficient.
%  Usage:
%    dEst = ApplyDegreeCorrection(dSample,fSample,K)
%  Inputs:
%    dSample = degree value(s) of sample to be corrected
%    fSample = ratio of sample edges to total edges
%    K = correction coefficient
% Outputs:
%    dEst = better estimate of true median degree given observation

dEst = dSample.* ((1./fSample).^(1 - 1./(K.*dSample)));

return
end