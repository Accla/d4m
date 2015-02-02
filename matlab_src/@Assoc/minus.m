function AB = minus(A,B)
%-,minus: Subtracts one associative array from another.
%Associative array user function.
%  Usage:
%    AB = A - B
%    AB = minus(A,B)
%  Inputs:
%    A = associative array
%    B = associative array
%  Outputs:
%    AB = associative array that is the complement of the intersection of the keys of A and B

  % Deal with value type mismatches.
  if ~isempty(A.val) && ~isempty(B.val)
    AB = xor(A,B);
  else
    if not(isempty(A.val))
       A = double(logical(A));
    end
    if not(isempty(B.val))
       B = double(logical(B));
    end
    B.A = -B.A;
    AB = Pluslike(A,B,@sum);
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

