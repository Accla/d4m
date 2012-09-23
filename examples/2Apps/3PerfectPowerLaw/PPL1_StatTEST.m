%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Generate a perfect power law graph and look at its statistics.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')                   % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


alpha = 1.3;  dmax = 1000;  Nd = 30;      % Set power law parameters.
%alpha = 0.7;  dmax = 2000;  Nd = 60;
%alpha = 1.0;  dmax = 2000;  Nd = 60;
%alpha = 1.3;  dmax = 2000;  Nd = 60;
%alpha = 1.3;  dmax = 1000;  Nd = 30;
%alpha = 1.6;  dmax = 1000;  Nd = 30;
%alpha = 1.9;  dmax =  500;  Nd = 30; 

[di ni] = PowerLawDist(alpha,dmax,Nd);    % Perfect power law degree distribution.

figure; loglog(di,ni,'^k');               % Plot distribution.
xlabel('degree'); ylabel('count'); title('degree distribution');    

N = sum(ni);                              % Number of vertices.
M = sum(ni.*di);                          % Number of edges.
disp(['Vertices: ' num2str(N) ', Edges: ' num2str(M) ', Ratio: ' num2str(M/N)]);

v = EdgesFromDist(di,ni);                 % Create vertices from degree distribution.

% Create edge and vertex permutations.
eoutperm = randperm(numel(v));     einperm = randperm(numel(v));   % Randomize edge order.
voutperm = randperm(max(v));       vinperm = randperm(max(v));     % Randomize vertex label

A0 = sparse(v,v,1);                                                % No permutations.
A1 = sparse(v(eoutperm),v(einperm),1);                             % Randomize edges.
A2 = sparse(voutperm(v),vinperm(v),1);                             % Randomize vertices.
A3 = sparse(voutperm(v(eoutperm)), vinperm(v(einperm)),1);         % Randomize both.


figure; spy(A0);                                                   % Show adjacency matrix
xlabel('end vertex'); ylabel('start vertex'); title('no permutations');  

figure; spy(A1);                                                   % Show adjacency matrix
xlabel('end vertex'); ylabel('start vertex'); title('randomized edges');

figure; spy(A2);                                                   % Show adjacency matrix
xlabel('end vertex'); ylabel('start vertex'); title('randomized vertices');

figure; spy(A3);                                                   % Show adjacency matrix
xlabel('end vertex'); ylabel('start vertex'); title('randomized edges & vertices');

figure; loglog(di,ni,'^k');                                         % Plot original distribution.
hold('on');  loglog( full(OutDegree(A3)) ,'o');                           % Plot randomized out degrees.
loglog( full(OutDegree(A3.')) ,'o'); hold('off');                         % Plot randomized in degrees.
xlabel('degree'); ylabel('count'); title('degree distribution');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
