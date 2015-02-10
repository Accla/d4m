function A1 = Adj2OutInRow(A,rowSep,weightCol,outDegCol,inDegCol)
% Convert Adjacency Assoc to Single-Table format Assoc.
% Input:
%   A           - Adjacency Assoc
%   rowSep      - Character to separate nodes in row, e.g. '|'
%   weightCol   - Name of the column to put weights in, e.g. 'Weight,'
%   outDegCol   - For every node. '' means no out-degrees. E.g. 'OutDegree,'
%   inDegCol    - For every node. '' means no out-degrees. E.g. 'InDegree,'
% Output:
%   A1  - Single-Table Assoc
% Ex: Assoc('v1,','v2,',3) ==> Assoc('v1|v2,v1,v2,', ...
%                                    'Weight,OutDegree,InDegree,', ...
%                                    [3 1 1])
% For undirected graphs, ex: Adj2OutInRow(A,'|','Weight,','Degree,','')
if isempty(A)
    A1 = Assoc('','','');
    return
end
[r,c,v] = find(A);
r = CatStr(r,rowSep,c);
c = repmat(weightCol,1,NumStr(r));
%A1 = Assoc(r,weightCol,v);
if ~isempty(inDegCol)
    %A1 = A1 + putCol(sum(A.',2),inDegCol);
    [r,c,v] = catFind(r,c,v, putCol(sum(A.',2),inDegCol));
end
if ~isempty(outDegCol)
    %A1 = A1 + putCol(sum(A,2),outDegCol);
    [r,c,v] = catFind(r,c,v, putCol(sum(A,2),outDegCol));
end
%
A1 = Assoc(r,c,v);

end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%