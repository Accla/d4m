function AB = or(A,B)
%|,or: Performs logical or of two associative arrays; output value is the min of the two input values.
%Associative array user function.
%  Usage:
%    AB = A | B
%    AB = or(A,B)
%  Inputs:
%    A = associative array
%    B = associative array
%  Outputs:
%    AB = associative array that is the union of the keys of A and B

  % Deal with value type mismatches.
  if ( not(isempty(A.val)) && not(isempty(B.val)) )
    % OK.
  else
    if not(isempty(A.val))
       A = logical(A);
    end
    if not(isempty(B.val))
       B = logical(B);
    end
  end

  AB = Pluslike(A,B,@ormin);

end

function y = ormin(x)
  y = min(x(x ~= 0));
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

