% Basic Breadth First Search Demo

%%
disp('Adjacency Version:')
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
    fprintf('        %d start nodes : %s\n',NumStr(vk),vk);
for k = 1:kmax
    Ak = AdjBFS(A,Adeg,'',vk,1,dmin,dmax,false);
    fprintf('Step %d: %d after filter: %s\n',k,NumStr(Row(Ak)),Row(Ak));
    fprintf('        %d a step away : %s\n',NumStr(Col(Ak)),Col(Ak));
    vk = Col(Ak);
end
displayFull(Ak)


%% 
disp('Single-Table Version:')
A1 = Adj2OutInRow(A,'|','Weight,','OutDegree,','InDegree,');
displayFull(A1)
fprintf('Exactly 1 step  from %s\n',v0)
Ak = AdjBFS(A1,A1,'OutDegree,',v0,1,2,Inf,false,true,'|','InDegree,');
displayFull(Ak)
fprintf('Exactly 2 steps from %s\n',v0)
Ak = AdjBFS(A1,A1,'OutDegree,',v0,2,2,Inf,false,true,'|','InDegree,');
displayFull(Ak)

%%
disp('Incidence Version:')
E = Adj2Edge(A,'start,','end,','|','e',true); % Convert Adjacency to Incidence Assoc 
displayFull(E)                                % with edge labels 'e01,e02,...,'
Edeg = sum(E,1).';       % In- and Out-degrees of each node.
v0 = 'v4,v5,';           % Starting vertices.    
kmax = 4;
dmin = 2;                % Minimum out-degree.
dmax = Inf;              % Maximum out-degree.

vk = v0;
fprintf('Starting %2d nodes: %s\n',NumStr(vk),vk);
for k = 1:kmax
    [vk,uk,Ek] = EdgeBFS(E,'start,','end,','|',Edeg,vk,1,dmin,dmax,false);
    fprintf('Step %2d: %2d nodes after filter: %s\n',k,NumStr(uk),uk);
    fprintf('         %2d edges traversed:    %s\n',NumStr(Row(Ek)),Row(Ek));
    fprintf('         %2d nodes reached:      %s\n',NumStr(vk),vk);
end




%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
