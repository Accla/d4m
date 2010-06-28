function AB = minus(A,B)
%MINUS "subtracts" one associative array from another.
  % Deal with value type mismatches.
  if ( not(isempty(A.val)) & not(isempty(B.val)) )
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

