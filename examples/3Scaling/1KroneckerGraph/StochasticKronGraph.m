function [ii jj] = StochasticKronGraph(G1,lgNv,Ne2Nv)
% Implementation of the Kronecker power-law graph
% generation algorithm.
% This is a "nice" implementation in that
% it is completely embarrassingly parallel
% and does not require ever forming the
% adjacency matrix.
%
% Example parameters:
%
% Power Law fit (Leskovec)
%   a=1; b=0.1;
%   G1 = [a a a a; a a a b; a b a b; a b b a];
%   lgNv = 5;  Ne2Nv = 8;
%
% Original GraphAnalysis benchmark parameters (http://www.graphanalysis.org/benchmark)/
%   a = 0.55; b = 0.1; c = 0.1; d = 0.25;
%   lgNv = 12; Ne2Nv = 8;
%   G1 = [a b; c d];
%
% Graph500 parameters.
%   a = 0.57; b = 0.19; c = 0.19; d = 0.05;
%   G1 = [a b; c d];
%   lgNv = 12; Ne2Nv = 16;
%

k = lgNv;

% Set base size of kernel matrx.
baseN = length(G1);

% Set number of vertices.
Nv = baseN^lgNv;

% Set number of edges.
Ne = Ne2Nv.*Nv;

% Normalize.
G1 = G1 ./ sum(sum(G1));

% Compute sums.
G1colSums = sum(G1,1);
G1rowSums = sum(G1,2);
G1rowCumSums = [0; cumsum(G1rowSums,1)];

% Create index arrays.
ii = ones(Ne,1);  jj = ii;
ii_bit = ii;  jj_bit = jj;

% Create normalized column probabilities.
G1colNorm = G1;
for i_base=1:baseN
  G1colNorm(:,i_base) = G1(:,i_base)./G1rowSums;
end
G1colCumSums = [zeros(baseN,1) cumsum(G1colNorm,2)];
 
% Loop over each order of bit.
for ib = 1:lgNv
  % Create random numbers.
  ii_rand = rand(Ne,1);
  jj_rand = rand(Ne,1);

  % Loop over each "bit" and compare with cumulative probability.
  for i_base=1:baseN
    ii_bit(find( ...
      (G1rowCumSums(i_base,1) < ii_rand) & ...
      (ii_rand < G1rowCumSums(i_base+1,1)) ...
    )) = i_base-1;
  end

  % Loop over each "bit" and compare with cumulative probability.
  for j_base=1:baseN
    jj_bit(find( ...
      (G1colCumSums(ii_bit+1,j_base) < jj_rand) & ...
      (jj_rand < G1colCumSums(ii_bit+1,j_base+1)) ...
    )) = j_base-1;
  end

  % multilply base and set value of this "bit".
  ii = ii + (baseN.^(ib-1)).*ii_bit;
  jj = jj + (baseN.^(ib-1)).*jj_bit;
end