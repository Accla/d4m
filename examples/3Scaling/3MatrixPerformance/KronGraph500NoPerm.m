function [StartVertex EndVertex] = KronGraph500NoPerm(SCALE,EdgesPerVertex)
%Graph500NoPerm: Generates graph edges using the same 2x2 Kronecker algorithm (R-MAT) as the Graph500 benchmark, but no permutation of vertex labels is performed.
%IO user function.
%  Usage:
%    [StartVertex EndVertex] = Graph500NoPerm(SCALE,edgefactor)
%  Inputs:
%    SCALE = integer scale factor that sets the max number of vertices to 2^SCALE
%    EdgesPerVertex = sets the total number of edges to M = K*N;
% Outputs:
%    StartVertex = Mx1 vector of integer start vertices in the range [1,N]
%    EndVertex = Mx1 vector of integer end vertices in the range [1,N]
  
  N = 2.^SCALE;               % Set  power of number of vertices..

  M = round(EdgesPerVertex .* N);     % Compute total number of edges to generate.

  A = 0.57; B = 0.19;  C = 0.19;   D = 1-(A+B+C);  % Set R-MAT (2x2 Kronecker) coefficeints.
 
  ij = ones (2, M);           % Initialize index arrays.
  ab = A + B;                 % Normalize coefficients.
  c_norm = C/(1 - (A + B));
  a_norm = A/(A + B);

  for ib = 1:SCALE            % Loop over each scale.
    ii_bit = rand(1, M) > ab;
    jj_bit = rand(1, M) > ( c_norm * ii_bit + a_norm * not (ii_bit) );
    ij = ij + 2^(ib-1) * [ii_bit; jj_bit];
  end

  StartVertex = ij(1,:).';     % Copy to output.
  EndVertex = ij(2,:).';       % Copy to output.
