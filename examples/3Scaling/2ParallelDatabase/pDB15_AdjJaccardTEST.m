%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute Jaccard coefficients for a subgraph.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite: pDB14_AdjBFSTEST
DoDB = false;
echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Use Ak from previous script. Take union of all nodes within kmax steps of previous script's v0str.
A = Assoc('','','');
for k = 1:size(Ak,1)
    A = A + Ak{k};
end
A = dblLogi(A + A.'); % Convert to unweighted, undirected matrix.

tic;
    J = Jaccard(A);   % Compute Jaccard coefficients in strict upper triangle of J.
Jtime = toc; fprintf('Jaccard Time: %.2f  #Nodes=%d  #Edges=%d  Edges/sec=%.2f\n', ...
    Jtime,NumStr(Row(A)),nnz(A)./2,nnz(A)./2./Jtime)

figure; colormap(flipud(colormap('gray')));
imagesc(Adj(J)); xlabel('end vertex'); ylabel('start vertex');
title('Jaccard Coefficients'); colorbar;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

