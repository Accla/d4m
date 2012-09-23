%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Generate a perfect power law graph and then sample it.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')                         % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Set power law parameters and sample fraction.
alpha = 1.3;  dmax = 2000;  Nd = 60;  fSamp = 1/40; 

[di ni] = PowerLawDist(alpha,dmax,Nd);           % Perfect power law degree distribution.
N = sum(ni);                                     % Number of vertices.
M = sum(ni.*di);                                 % Number of edges.
disp(['Vertices: ' num2str(N) ', Edges: ' num2str(M) ', Ratio: ' num2str(M/N)]);

v = EdgesFromDist(di,ni);                        % Create vertices from degree distribution.

eperm = randperm(numel(v));                      % Randomize edge order.
vperm = randperm(max(v));                        % Randomize vertex label

v = vperm(v(eperm));                             % Randomize both.
vSamp = v(1:ceil(M.*fSamp));                     % Pick a sub-sample.

[tmp v dv] = find(sparse(1,v,1));                % Compute degree
[tmp vSamp dvSamp] = find(sparse(1,vSamp,1));    % Compute sample degree.

n1Samp = nnz(dvSamp == 1);                       % Compute n1 of sample.
dmaxSamp = max(dvSamp);                          % Compute dmax of sample.

[K alphaSamp] = ComputeDegreeCorrection(n1Samp,dmaxSamp,fSamp);       % Compute correction.
dvSampCorr = ApplyDegreeCorrection(dvSamp,fSamp,K);                   % Apply correction.

[tmp vSort] = sort(dv,2,'descend');              % Sort each vertex for plotting.
[tmp vSortSort] = sort(vSort);

echo('off');

figure; loglog(vSortSort(v),dv,'.');                                 % Plot.
hold('on'); loglog(vSortSort(vSamp), dvSamp,'k.'); hold('off');
xlabel('in vertex');  ylabel('in degree');  title('uncorrected sample');

figure; loglog(vSortSort(v),dv,'.');                                 % Plot.
hold('on'); loglog(vSortSort(vSamp), dvSamp ./ fSamp,'k.'); hold('off');
xlabel('in vertex');  ylabel('in degree');  title('linear corrected sample');

figure; loglog(vSortSort(v),dv,'.');                                 % Plot.
hold('on'); loglog(vSortSort(vSamp), dvSampCorr,'k.'); hold('off');
xlabel('in vertex');  ylabel('in degree');  title('non-linear corrected sample');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
