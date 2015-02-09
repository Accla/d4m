function AB = times(A,B)
%.*,times: Multiplies the numeric values of one associative array by the values of another.
%Associative array user function.
%  Usage:
%    AB = A .* B
%    AB = times(A,B)
%  Inputs:
%    A = associative array with numeric values
%    B = associative array with numeric values
%  Outputs:
%    AB = associative array that is the intersection of the keys of A and B and product of values

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

  AB = Pluslike(A,B,@andtimes);
end

function y = andtimes(x)
  if (numel(x) == 1)
    y = 0;
  else
    y = x(1) .* x(2);
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

