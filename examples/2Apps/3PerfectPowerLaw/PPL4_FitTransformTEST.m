%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Generate a perfect power law graph and look at its statistics.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')                          % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

alpha = 1.3;  dmax = 2000;  Nd = 60;             % Set power law parameters.

[di ni] = PowerLawDist(alpha,dmax,Nd);           % Degree distribution.
N = sum(ni);                                     % Number of vertices.
M = sum(ni.*di);                                 % Number of edges.
A = RandPowerLawMatrix(alpha,dmax,Nd);           % Create randomized power law graph.

A1 = triu(A + A.');                              % Undirected.
A1 = double(logical(A1));                        % Undirected, unweighted.
A1 = A1 - diag(diag(A1));                        % Undirected, unweighted, no self-loops.

[tmp A1di A1ni] = find(OutDegree(A1));           % Degree distribution.

A1alpha = log(A1ni(1))./log(A1di(end));          % Compute slope.
A1M = sum(sum(A1));                              % Count edges.

[di_Fit ni_Fit] = PowerLawFit(A1alpha,N,A1M);    % Vest fit distribution.
nd_Fit  = PowerLawRebin(A1di,A1ni,di_Fit);       % Rebin data to best fit degrees.

echo('off')

figure; loglog( [1 di(end)],[ni(1) 1],'k-' ); hold('on');
loglog(A1di,A1ni ,'o'); loglog([1 A1di(end)],[A1ni(1) 1],'-' ); loglog(di_Fit, ni_Fit,'k^'); loglog( full(nd_Fit) ,'r+'); 
hold('off'); legend('input','transformed','\alpha','model fit','rebin')
xlabel('degree'); ylabel('count'); title('fit to undirected, unweighted, no self-loops.');


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
