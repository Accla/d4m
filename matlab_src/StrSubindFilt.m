function si = StrSubindFilt(s,i)
%StrSubindFilt: Returns sub-strings i found in string list s via filtering.
%String utility function.
%  Usage:
%    si = StrSubindFilt(s,i)
%  Inputs:
%    s = list of strings
%    i = indices of strings to get from list
%  Outputs:
%    si = list of sub-strings from s (in their original order).

   [tmp isep tmp] = find(s == s(end));       % Find locations of string seperators.
   isep0 = [1 (isep + 1)];                   % String starts.
   isep1 = isep + 1;                         % String ends.
%   isep1(end) = isep(end);
   ii = i( (i > 0) & (i <= numel(isep)));    % Eliminate out of bounds.
%   si = s(logical(cumsum(  sparse(isep0(ii),1,1,isep(end),1) + sparse(isep1(ii),1,-1,isep(end),1)  )));
   si = s(logical(cumsum(  sparse(isep0(ii),1,1,isep(end)+1,1) + sparse(isep1(ii),1,-1,isep(end)+1,1)  )));
%keyboard

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

