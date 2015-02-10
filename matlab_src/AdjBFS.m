function Ak = AdjBFS(A,Adeg,AdegCol,v0,k,dmin,dmax,takeunion,outInRow,rowSep)
% Out-degree-filtered Breadth First Search on Adjacency Assoc/DB.
% Input:
%   A       - Adjacency Assoc or Database Table
%   Adeg    - Out-degrees of A, e.g., sum(A,2)
%             Ex format: ('v1,','deg,',3)
%   AdegCol - The column of Adeg for degree lookups. If '', assumes only 1 Adeg column.
%             Useful when there are multiple columns in Adeg, e.g. 'outDeg,' and 'inDeg,'
%   k       - Number of BFS steps
%   dmin    - Minimum allowed out-degree, e.g. 1
%   dmax    - Maximum allowed out-degree, e.g. Inf
%   takeunion   - false to return subgraph on EXACTLY the kth step;
%                 true to return subgraph reachable in UP TO k steps
%   outInRow    - (optional) See Single-Table Format below.
%   rowSep      - (required if outInRow=true) See Single-Table Format below.
% Output (for outInRow=false):
%   Ak - Subgraph of A where 
%     if takeunion=false,
%       Row(Ak) = nodes reachable in EXACTLY (k-1) hops that pass the out-degree filter
%       Col(Ak) = nodes reachable in EXACTLY k hops from the row nodes.
%     if takeunion=true,
%       Row(Ak) = nodes reachable in UP TO (k-1) hops that pass the out-degree filter
%       Col(Ak) = nodes reachable in UP TO k hops from the row nodes.
% Single-Table Format (for outInRow=true):
%     Row 'v1|v2,' ==> Column 'Weight,' ==> Value 1
%     Row 'v1,' ==> Columns 'OutDegree,' and 'InDegree,' with numeric values
%   Pass table as A, Adeg=A also, AdegCol='OutDegree,', outInRow=true, rowSep='|'
%   No need to specify 'Weight,' col (we assume no other cols present in 'v1|v2,' rows)
%   Returns Ak in single-table format.
%   **NOTE: does not return degree columns, only returns weight columns.

%#ok<*CHAIN,*ST2NM> Suppress Matlab warnings related to Assoc operations.
if nargin < 9
    outInRow = false;
end

vk = v0;
if takeunion
    Akall = Assoc('','','');
end
for i = 1:k
    if isempty(vk) % meaning no nodes reachable from previous step's uk (or v0 is '').
        Ak = Assoc('','','');
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
        if outInRow
            uk = StartsWith( strrep(uk,uk(end),[rowSep uk(end)]) ); % v1|*,v2|*,...
            % This ensures we do not scan any degrees. Assume single column so : is ok. 
        end
        Ak = A(uk,:);   % submatrix within reach of filtered neighbors
        if takeunion
            Akall = Ak + Akall;
        end
    end
    if outInRow
        if isempty(Row(Ak))
            vk = [];
        else
            [~,vk] = SplitStr(Row(Ak),rowSep); %v1|v2,v1|v3, ==> v2,v3,
        end
    else
        vk = Col(Ak);   % nodes exactly k hops away
    end
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