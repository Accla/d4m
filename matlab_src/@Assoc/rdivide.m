function AB = rdvide(A,B)
%AND performs divide of two associative arrays.

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

  AB = Pluslike(A,B,@anddivide);
end

function y = anddivide(x)
  if (numel(x) == 1)
    y = 0;
  else
    y = x(1) ./ x(2);
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

