function A = reAssoc(A)
%reAssoc: Rebuilds an associative array so that all keys are in their correct sorted order.
%Associative array user function.
%  Usage:
%    A = reAssoc(A)
%  Inputs:
%    A = associative array
%  Outputs:
%    A = associative array with all keys in correct order
%  Example:
%    A = reAssoc(putRow(A,CatStr(Row(A),'|','Name,'))));    % Append '|Name' to each row key.

% Rebuild A.
  [r c v] = find(A);
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

