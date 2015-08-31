%% This script factors a table A into W and H,
%% such that A is approximately equal to W and H
%% and W and H are somewhat sparse.

DBsetup;
existingTables = ls(DB);
disp('DBsetup complete.');

% Params
nTopics = 25;
maxiter = 50;             % Maximum number of NMD iterations.
cutoffThreshold = 0.000;  % Set entries less than this threshold to 0.
maxColsPerTopic = 10;     % Only keep the top x words per topic
doFullGraphulo = false;   % Whether to use fully distributed Graphulo or a version that works in memory of a client
                          % Warning: full Graphulo is computationally expensive!

if ~doFullGraphulo
    tic;
    G.NMF_Client(tnTfidf, false, tnW, false, tnH, ...
                 false, nTopics, maxiter, cutoffThreshold, maxColsPerTopic, [], []);
    t = toc;
    fprintf('[%s] NMF Completed: W and H stored as new Accumulo tables. Time elapsed = %f\n',datestr(clock,0), t);
    % W = patientsTopics, H = topicsWords

else
    G.NMF(tnTfidf, tnTfidfT,...
          tnW, tnWT, tnH, tnHT,...
          nTopics, maxiter, true, cutoffThreshold, maxColsPerTopic);
end

