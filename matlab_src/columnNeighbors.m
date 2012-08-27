function graphSet = columnNeighbors(T,startVertex,colTypes,colClut,graphDepth)
%columnNeighbors: DEPRECATED. Using a starting set of column keys find graph neighbors to specified depth. 
%Associative array or database table utility function.
%  Usage:
%    graphSet = columnNeighbors(T,startVertex,colTypes,colClut,graphDepth)
%  Inputs:
%    T = associative array or database table
%    startVertex = list of column keys to start graph traversal
%    colTypes = list of ranges or regular expressions to narrow set of columns to work with
%    colClut = list of column keys to avoid
%    graphDepth = depth of of neighbors to find (>1)
% Outputs:
%    graphSet = list column keys of vertex neighbors

  graphSet = startVertex;
  
  for k=1:graphDepth
     AT = T(Row(T(:,graphSet)),:);
     AT = AT(:,colTypes);
     graphSet = Col(AT - AT(:,colClut));
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
