function Ak = AdjBFS(A,Adeg,AdegCol,v0,k,dmin,dmax)
% Degree-filtered Breadth First Search on Adjacency Assoc for nodes exactly k steps away.
% Adeg holds the degrees of A in a single column. If not already computed, use Adeg = sum(A,2);
% AdegCol is the (single) column of Adeg that holds the degree information to use.
% Return submatrix of adjacency matrix where 
%   Rows are nodes reachable in (k-1) hops with dmin<=degree<=dmax,
%   Cols are nodes reachable in k hops from the row nodes.
% Ex: Ak = AdjBFS(Tadj,TadjDeg,'OutDeg,',v0str,3,5,15);
% Ex: Ak = AdjBFS(A,Adeg,'',v0str,3,5,15);
if nargin < 7
    dmax = Inf;
    if nargin < 6
        dmin = 1;
    end
end

vk = v0;
for i = 1:k
    % Note: Can use a RowFilter to handle the next line.
    if isempty(AdegCol)  % Simple case: No column to select.
        uk = Row(dmin <= str2num(Adeg(vk,:)) <= dmax); %filtered neighbors
    else
        dk = Adeg(vk,:); % Select correct column.
        uk = Row(dmin <= str2num(dk(:,AdegCol)) <= dmax); %filtered neighbors
    end
    Ak = A(uk,:);   % submatrix within reach of filtered neighbors
    vk = Col(Ak);   % nodes exactly k hops away
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

