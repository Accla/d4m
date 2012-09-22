%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Benchmark sparse matrix multiply performance.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                    % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

n = 2.^[6 6 7 8 9 10 11 12 13 14 15 16 17 18];    % Set matrix sizes to test.
%n = 2.^[10 10 11 12 13 14 15 16 17 18 19 20 21];

K = 8;                                      % Approximate non-zeros per row.

MaxGB = 2;                                  % Machine memory size.
MaxGF = 4.*1.6;                             % Machine peak GigaFlops.

m = K.*n;                                   % Number of non-zeros to generate.
sparse_gbytes = 9.*8.*m ./ 1e9;             % Memory used.
sparse_flops = zeros(1,numel(n));           % Operations performed.
sparse_gflops = zeros(1,numel(n));          % Gigaflops.
sparse_time = zeros(1,numel(n));            % Time to perform operation.

for i = 1:numel(n);
  % Create sparse indices and matrices and convert to ones.
  ii = floor(rand(m(i),1).*n(i))+1;
  jj = floor(rand(m(i),1).*n(i))+1;
  A = double(logical(sparse(ii,jj,1,n(i),n(i))));
  ii = floor(rand(m(i),1).*n(i))+1;
  jj = floor(rand(m(i),1).*n(i))+1;
  B = double(logical(sparse(ii,jj,1,n(i),n(i))));
  clear('ii','jj');                        % Clear indices.

  tic;
    C = A*B;                               % Perform sparse matrix multiply.
  sparse_time(i) = toc;

  sparse_flops(i)  = full(2*sum(sum(C)));  % Compute flops to be credited.
  clear('A','B','C');                      % Clear matrices.
  sparse_gflops(i) = sparse_flops(i) ./ sparse_time(i) ./ 1.e9;   % Compute gigaflops.
  disp(['Time: ' num2str(sparse_time(i)) ', GFlops: ' num2str(sparse_gflops(i)) ', GBytes: ' num2str(sparse_gbytes(i))]);
end

r = sprintf('%2.2d,',(1:numel(n)).');      % Size row string.

AsparseResults  = Assoc(r,'N,',n.') + Assoc(r,'M,',m.') + Assoc(r,'time,',sparse_time.') +  Assoc(r,'GByte,',sparse_gbytes.') + Assoc(r,'GFlop,', sparse_gflops.') + Assoc(r,'MemFrac,',(sparse_gbytes./MaxGB).') + Assoc(r,'ProcFrac,', (sparse_gflops./MaxGF).');

displayFull(AsparseResults);               % Show results in tabular form.

save('data/MP2_Sparse.mat','AsparseResults');     % Save output for comparison.
 
semilogx(full(Adj(AsparseResults(2:numel(n),'MemFrac,'))),full(Adj(AsparseResults(2:numel(n),'ProcFrac,'))));
xlabel('Fraction of Memory Used');  ylabel('Fraction of Peak Performance');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%