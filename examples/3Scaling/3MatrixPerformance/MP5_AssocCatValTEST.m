%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Benchmark associative array CatVal multiply performance.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                    % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

n = 2.^[6 6 7 8 9 10 11 12 13];       % Set matrix sizes to test.
%n = 2.^[6 6 7 8 9 10 11 12 13 14 15 16];  

K = 8;                                      % Approximate non-zeros per row.

MaxGB = 2;                                  % Machine memory size.
MaxGF = 4.*1.6;                             % Machine peak GigaFlops.

m = K.*n;                                   % Number of non-zeros to generate.
assoc_gbytes = zeros(1,numel(n));           % Memory used.
assoc_flops = zeros(1,numel(n));            % Operations performed.
assoc_gflops = zeros(1,numel(n));           % Gigaflops.
assoc_time = zeros(1,numel(n));             % Time to perform operation.

for i = 1:numel(n);
  % Create assoc indices and matrices.
  ii = sprintf('%d,',floor(rand(m(i),1).*n(i))+1);
  jj = sprintf('%d,',floor(rand(m(i),1).*n(i))+1);
  A = Assoc(ii,jj,ii);
  assoc_gbytes(i) = assoc_gbytes(i) + (numel(ii) + numel(jj) + 8.*m(i)) ./ 1e9;
  ii = sprintf('%d,',floor(rand(m(i),1).*n(i))+1);
  jj = sprintf('%d,',floor(rand(m(i),1).*n(i))+1);
  B = Assoc(ii,jj,jj);
  assoc_gbytes(i) = assoc_gbytes(i) + (numel(ii) + numel(jj) + 8.*m(i)) ./ 1e9;
  clear('ii','jj');                         % Clear indices.

  tic;
    C = CatValMul(A,B);                     % Perform associative array CatVal multiply.
  assoc_time(i) = toc;

  assoc_flops(i)  = full(2*sum(sum(Adj(A*B))));      % Compute flops to be credited.
  [ii jj vv] = find(C);
  assoc_gbytes(i) = assoc_gbytes(i) + (numel(ii) + numel(jj) + numel(vv) + 8.*m(i)) ./ 1e9;
  clear('ii','jj','vv','A','B','C');        % Clear matrices.
  assoc_gflops(i) = assoc_flops(i) ./ assoc_time(i) ./ 1.e9;
  disp(['Time: ' num2str(assoc_time(i)) ', GFlops: ' num2str(assoc_gflops(i)) ', GBytes: ' num2str(assoc_gbytes(i))]);
end

r = sprintf('%2.2d,',(1:numel(n)).');       % Size row string.

AcatValResults  = Assoc(r,'N,',n.') + Assoc(r,'M,',m.') + Assoc(r,'time,',assoc_time.') + Assoc(r,'GByte,',assoc_gbytes.') + Assoc(r,'GFlop,', assoc_gflops.') + Assoc(r,'MemFrac,',(assoc_gbytes./MaxGB).') + Assoc(r,'ProcFrac,', (assoc_gflops./MaxGF).');

displayFull(AcatValResults);                % Display results in tabular form.

save('data/MP5_AssocCatVal.mat','AcatValResults');         % Save output for comparison.

semilogx(full(Adj(AcatValResults(2:numel(n),'MemFrac,'))),full(Adj(AcatValResults(2:numel(n),'ProcFrac,'))));
xlabel('Fraction of Memory Used');  ylabel('Fraction of Peak Performance');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%