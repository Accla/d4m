function[result] = bipartite_krons_split(n1,m1,n2,m2)
% Compute the index permuation required to
% seperate the result of the Kronecker product
% of two bipartite matrices.

result = zeros(1,(n1+m1)*(n2+m2));


for i=0:(n1-1)
  for j=1:n2
    ii =  i*(n2+m2) + j;
    jj  = i*n2 + j;
    result( ii ) = jj;
  end
end

for i=0:(n1-1)
  for j=1:m2
    ii = i*(n2+m2) + n2 + j;
    jj  = (n1*n2 + m1*m2) + n2*m1 + i*m2 + j;
    result( ii ) = jj;
  end
end

for i=0:(m1-1)
  for j=1:n2
    ii = (n1+i)*(n2+m2) + j;
    jj = (n1*n2 +  m1*m2) + i*n2 + j;
    result( ii ) = jj;
  end
end

for i=0:(m1-1)
  for j=1:m2
    ii =  (n1+i)*(n2+m2) + n2 + j;
    jj = n1*n2 + i*m2 + j;
    result( ii ) = jj;
  end
end



