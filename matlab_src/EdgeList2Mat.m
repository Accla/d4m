function Emat = EdgeList2Mat(startV,endV)
% Convert start vertex and end vertex arrays (of equal length) 
% into a sparse incidence matrix. Eliminate self-edges and duplicate edges.

SelfEdge = startV == endV;                       % Indices of self-edges
Vert = [startV(~SelfEdge).', endV(~SelfEdge).']; % Form Mx2 matrix

% Eliminate duplicate edges.
Vert = unique(Vert,'rows');  % eliminate duplicate start->end, start->end
% Tricky: eliminate duplicate start->end, end->start
[DupRevIdx,DupRevIdxPos] = ismember(Vert,fliplr(Vert),'rows');
% ~DupRevIdx are non-duplicate rows
% DupRevIdxPos > (1:size(Vert,1)).' selects ONE of each duplicate row
Vert = Vert(~DupRevIdx | DupRevIdxPos > (1:size(Vert,1)).', :);

M = size(Vert,1);  % Number of edges (not self-edges, not duplicate)
Emat = logical(sparse(repmat(1:M,1,2),Vert,1));

end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%