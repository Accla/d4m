% Basic Breadth First Search Demo
Amat = [0 1 1 1 0; % Input adjacency matrix
     1 0 1 0 1;
     1 1 0 1 0; 
     1 0 1 0 0;
     0 1 0 0 0];
A = Mat2Assoc(Amat,'v'); % Convert to Assoc, use 'v1,v2,...,' as labels.
displayFull(A)
Adeg = sum(A,2);         % Degree of each node.
v0 = 'v4,v5,';           % Starting vertices.    
kmax = 4;
dmin = 2;                % Minimum degree.
dmax = Inf;              % Maximum degree.

Ak = Assoc('','','');    % Initialize.
vk = v0;
for k = 1:kmax
    Ak = AdjBFS(A,Adeg,'',vk,1,dmin,dmax);
    fprintf('Step %d: %d start        : %s\n',k,NumStr(vk),vk);
    fprintf('        %d after filter : %s\n',NumStr(Row(Ak)),Row(Ak));
    fprintf('        %d one step away: %s\n',NumStr(Col(Ak)),Col(Ak));
    vk = Col(Ak);
end
displayFull(Ak)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
