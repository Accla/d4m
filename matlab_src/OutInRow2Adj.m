function A = OutInRow2Adj(A1,rowSep,weightCol)
% Convert Single-Table format Assoc to Adjacency Assoc.
% Throws away degree information, if present in A1.
% Input:
%   A           - Adjacency Assoc
%   rowSep      - Character to separate nodes in row, e.g. '|'
%   weightCol   - Name of the column to put weights in, e.g. 'Weight,'
% Output:
%   A1  - Single-Table Assoc
% Ex: Assoc('v1|v2,v1,v2,', 'Weight,OutDegree,InDegree,', [3 1 1])
%     ==> Assoc('v1,','v2,',3)

if isempty(A1)
    A = Assoc('','','');
    return
end
[r,~,v] = find(A1(:,weightCol));
[r,c] = SplitStr(r,rowSep);
A = Assoc(r,c,v);
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%