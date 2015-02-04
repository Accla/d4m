function AB = and(A,B)
%&,and: Performs logical and of two associative arrays; output value is the min of the two input values.
%Associative array user function.
%  Usage:
%    AB = A & B
%    AB = and(A,B)
%  Inputs:
%    A = associative array
%    B = associative array
%  Outputs:
%    AB = associative array that is the intersection of the keys of A and B

  if ( isempty(A) || isempty(B) )  % Short circuit if nothing in A or B.
    AB = Assoc('','','');  return;
  end

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

  AB = Pluslike(A,B,@andmin);
end

function y = andmin(x)
  y = min(x);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

