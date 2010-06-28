function AB = or(A,B)
%NE performs or operation on two associative arrays.

  % Deal with value type mismatches.
  if ( not(isempty(A.val)) & not(isempty(B.val)) )
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

