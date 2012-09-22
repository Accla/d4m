%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Benchmark dense matrix multiply performance.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                    % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

n = 2.^[6 6 7 8 9 10 11 12];                   % Set matrix sizes to test.
%n = 2.^[6 6 7 8 9 10 11 12 13];              

MaxGB = 2;                                  % Machine memory size.
MaxGF = 4.*1.6;                             % Machine peak GigaFlops.


m = n.^2;                                   % Number entries.
dense_gbytes = 3.*8.*m ./ 1e9;              % Memory usage.
dense_flops  = 2.*n.^3;                     % Number of operations.
dense_gflops = zeros(1,numel(n));           % Gigaflops.
desnse_time = zeros(1,numel(n));            % Time to perform operation.

for i = 1:numel(n);                         % Loop over each matrix size.
  A = rand(n(i),n(i));                      % Create dense matrix.
  B = rand(n(i),n(i));                      % Create dense matrix.

  tic;
    C = A*B;                                % Perform dense matrix multiply.
  dense_time(i) = toc;

  clear('A','B','C');                       % Clear matrices.           
  dense_gflops(i) = dense_flops(i) ./ dense_time(i) ./ 1.e9;      % Compute peformance.
  disp(['Time: ' num2str(dense_time(i)) ', GFlops: ' num2str(dense_gflops(i)) ', GBytes: ' num2str(dense_gbytes(i))]);
end

r = sprintf('%2.2d,',(1:numel(n)).');       % Size row string.

AdenseResults  = Assoc(r,'N,',n.') + Assoc(r,'M,',m.') + Assoc(r,'time,',dense_time.') +  Assoc(r,'GByte,',dense_gbytes.') + Assoc(r,'GFlop,', dense_gflops.') + Assoc(r,'MemFrac,',(dense_gbytes./MaxGB).') + Assoc(r,'ProcFrac,', (dense_gflops./MaxGF).');

displayFull(AdenseResults);

save('data/MP1_Dense.mat','AdenseResults');

semilogx(full(Adj(AdenseResults(2:numel(n),'MemFrac,'))),full(Adj(AdenseResults(2:numel(n),'ProcFrac,'))));
xlabel('Fraction of Memory Used');  ylabel('Fraction of Peak Performance');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
