function [vk,uk,ek] = EdgeBFS(E,startPrefix,endPrefix,prefixSep,Edeg,v0,k,dmin,dmax,takeunion)
% Out-degree-filtered Breadth First Search on Incidence Assoc/DB.
% Conceptually k iterations of: v0 ==startPrefix==> edge ==endPrefix==> v1
% Input:
%   E           - Incidence Assoc or Database Table
%   startPrefix - "Out" node prefix from nodes to edges, e.g. 'out,'
%   endPrefix   - "In" node prefix from edges to nodes, e.g. 'in,'
%   prefixSep   - Separator between node prefix and node, e.g. '|'
%   Edeg        - Assoc or DB Table of node out-degrees, e.g. sum(E,1).'
%                 Ex format: ('out|v1,','Degree,',2), ('in|v1,','Degree,',1)
%   v0          - Starting vertex set, e.g. 'v4,v5,'
%   k           - Number of BFS steps
%   dmin        - Minimum allowed out-degree, e.g. 1
%   dmax        - Maximum allowed out-degree, e.g. Inf
%   takeunion   - false to return nodes reachable in EXACTLY k steps;
%                 true to return nodes reachable in UP TO k steps
% Output:
%   vk - Nodes reachable in exactly k steps (up to k steps if takeunion=true)
%   uk - (optional) Nodes searched from to obtain the vk nodes that pass the out-degree filter
%   ek - (optional) Edges from uk nodes and to vk nodes

% To consider: can add degreeColumn fairly easily, but I doubt users need it.

vk = v0;
if takeunion
    vkall = vk;
    ukall = '';
    ekall = '';
end

for i=1:k
    if isempty(vk) % meaning no nodes reachable from previous step's uk (or v0 is '').
        fprintf('Stopped BFS after step %d; no nodes in search set.\n',i);
        ek = ''; uk = '';
        break
    end
    % Filter vk by node out-degrees into uk.
    uk = Row(dmin <= str2num(Edeg(CatStr(startPrefix,prefixSep,vk),:)) <= dmax);
    if isempty(uk)
        fprintf('Stopped BFS after step %d filtering; all nodes filtered away.\n',i);
        ek = ''; vk = '';
        break      % All nodes filtered out.
    end
    [~,uk] = SplitStr(uk,prefixSep);   % uk is non-empty
    if takeunion && nargout >= 2
        ukall = StrUnique([ukall uk]);
    end

    % Get edges starting from uk.
    ek = Row(E(:,CatStr(startPrefix,prefixSep,uk)));
    % Note: The following section will never occur by construction of the degree
    % table Edeg.  If uk is non-empty, then there will be at least dmin edges present.
%     if isempty(ek)
%         disp('Stopped BFS after edge step; no edges present from uk.');
%         break
%     end
    if takeunion && nargout >= 3
        ekall = StrUnique([ekall ek]);
    end

    % Get nodes that ek goes into.
    vk = Col(E(ek,StartsWith(endPrefix)));
    if ~isempty(vk)
        [~,vk] = SplitStr(vk,prefixSep);
        if takeunion
            vkall = StrUnique([vkall vk]);
        end
    end
end
if takeunion
    vk = vkall;
    uk = ukall;
    ek = ekall;
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