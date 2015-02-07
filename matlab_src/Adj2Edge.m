function E = Adj2Edge(A,startPrefix,endPrefix,prefixSep,edgePrefix,zeropadEdge)
% Convert weighted, directed Adjacency Assoc to Incidence Assoc
%   startPrefix - prefix for the node an edge starts from, e.g. 'out,'
%   endPrefix   - prefix for the node an edge goes to, e.g. 'in,'
%   prefixSep   - separator between node prefix and node, e.g. '|'
%   edgePrefix  - prefix for new edge labels, e.g. 'e' creates labels 'e1,e2,...'
%   zeropadEdge - boolean switch to zeropad edge labels to constant width, e.g. 'e01,e02,...,e10,e11,...'

[ar,ac,av] = find(A);
startnode = CatStr(startPrefix,prefixSep,ar);
endnode = CatStr(endPrefix,prefixSep,ac);
if zeropadEdge
    numZero = ceil(log10(numel(av)));
    edgeConv = [edgePrefix '%0' num2str(numZero) 'd,'];
else
    edgeConv = [edgePrefix '%d,'];
end
edgelabel = num2str(1:numel(av),edgeConv);
E = Assoc([edgelabel edgelabel],[startnode endnode],[av; av]);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

