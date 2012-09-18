function[nu mu] = BipartiteIndexTree(k)
% Compute the index permuation required to
% seperate the result of the Kronecker product
% of two bipartite matrices.

nu = complex(zeros(k,2^(k-1)));
mu = nu;

im1 = complex(0,1);

nu(1,1) = 1;
mu(1,1) = im1;

for kk = 2:k
  for i = 1:2^(kk-2)
    nu(kk,2*i-1) = nu(kk-1,i) + 1;
    nu(kk,2*i) = mu(kk-1,i) + 1;
    mu(kk,2*i-1) = mu(kk-1,i) + im1;
    mu(kk,2*i) = nu(kk-1,i) + im1;
  end
end
