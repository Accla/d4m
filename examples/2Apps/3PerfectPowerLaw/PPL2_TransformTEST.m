%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Generate a perfect power law graph and apply various transformations.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')                   % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

alpha = 1.3;  dmax = 1000;  Nd = 30;      % Set power law parameters.

A = RandPowerLawMatrix(alpha,dmax,Nd);    % Create randomized power law graph.

Aa    = double(logical(A));               % Unweighted. 
Ab    = triu(A + A.');                    % Undirected.
Ac    = A - diag(diag(A));                % Eliminate self-loops.
Ad    = triu(A.' * A);                    % Upper part of correlation matrix.

Aac   = Aa - diag(diag(Aa));              % Unweighted, no self-loops.
Aacd  = triu(Aac.' * Aac);                % Upper part of correlation matrix.
Aacdc = Aacd - diag(diag(Aacd));          % Upper part of correlation matrix, no diagonal.

echo('off');

figure; loglog(full(OutDegree(A)),'^k');                            % Plot original distribution.
hold('on');  loglog( full(OutDegree(Aa)) ,'o'); hold('off');        % Plot out-degrees.
xlabel('degree'); ylabel('count'); title('unweighted');

figure; loglog(full(OutDegree(A)),'^k');                            % Plot original distribution.
hold('on');  loglog( full(OutDegree(Ab)) ,'o'); hold('off');        % Plot out-degrees.
xlabel('degree'); ylabel('count'); title('undirected');

figure; loglog(full(OutDegree(A)),'^k');                            % Plot original distribution.
hold('on');  loglog( full(OutDegree(Ac)) ,'o'); hold('off');        % Plot out-degrees.
xlabel('degree'); ylabel('count'); title('no self-loops');

figure; loglog(full(OutDegree(A)),'^k');                            % Plot original distribution.
hold('on');  loglog( full(OutDegree(Ad)) ,'o'); hold('off');        % Plot out-degrees.
xlabel('degree'); ylabel('count'); title('upper correlation matrix');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
