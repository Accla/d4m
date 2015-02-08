function Ak = AdjBFS(A,Adeg,AdegCol,v0,k,dmin,dmax,takeunion)
% Out-degree-filtered Breadth First Search on Adjacency Assoc/DB.
% Input:
%   A       - Adjacency Assoc or Database Table
%   Adeg    - Out-degrees of A, e.g., sum(A,2)
%             Ex format: ('v1,','deg,',3)
%   AdegCol - The column of Adeg to use for degree lookups. If '', assumes only one Adeg column.
%             Useful when there are multiple columns in Adeg, e.g. 'outDeg,' and 'inDeg,'
%   k       - Number of BFS steps
%   dmin    - Minimum allowed out-degree, e.g. 1
%   dmax    - Maximum allowed out-degree, e.g. Inf
%   takeunion   - false to return subgraph on EXACTLY the kth step;
%                 true to return subgraph reachable in UP TO k steps
% Output:
%   Ak - Subgraph of A where 
%     if takeunion=false,
%       Row(Ak) = nodes reachable in EXACTLY (k-1) hops that pass the out-degree filter
%       Col(Ak) = nodes reachable in EXACTLY k hops from the row nodes.
%     if takeunion=true,
%       Row(Ak) = nodes reachable in UP TO (k-1) hops that pass the out-degree filter
%       Col(Ak) = nodes reachable in UP TO k hops from the row nodes.

vk = v0;
if takeunion
    Akall = Assoc('','','');
end
for i = 1:k
    if isempty(vk) % meaning no nodes reachable from previous step's uk (or v0 is '').
        fprintf('Stopped BFS after step %d; no nodes in search set.\n',i);
        break
    end
    % Note: Can use a RowFilter to handle the next line.
    if isempty(AdegCol)  % Simple case: No column to select.
        uk = Row(dmin <= str2num(Adeg(vk,:)) <= dmax); %filtered neighbors
    else
        dk = Adeg(vk,:); % Select correct column.
        uk = Row(dmin <= str2num(dk(:,AdegCol)) <= dmax); %filtered neighbors
    end
    if isempty(uk)
        Ak = Assoc('','','');
    else
        Ak = A(uk,:);   % submatrix within reach of filtered neighbors
        if takeunion
            Akall = Ak + Akall;
        end
    end
    vk = Col(Ak);   % nodes exactly k hops away
end
if takeunion
    Ak = Akall;
end

end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%