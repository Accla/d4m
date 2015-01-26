function Ak = AdjBFS(A,v0,k,dmin,dmax)
% Degree-filtered Breadth First Search on Adjacency Assoc for nodes exactly k steps away.
% Return submatrix of adjacency matrix where 
% Rows are nodes reachable in (k-1) hops with dmin<=degree<=dmax,
% Cols are nodes reachable in k hops from the row nodes.
Aout = sum(A,2);
if nargin < 5
    dmax = Inf;
    if nargin < 4
        dmin = 1;
    end
end
vk = v0;
for i = 1:k
    % Note: Can use a RowFilter to handle the next line.
    uk = Row(dmin <= str2num(Aout(vk,:)) <= dmax); %filtered neighbors
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

