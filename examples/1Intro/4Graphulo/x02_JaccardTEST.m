% Basic Jaccard Demo
Amat = [0 1 1 1 0; % Input adjacency matrix
     1 0 1 0 1;
     1 1 0 1 0; 
     1 0 1 0 0;
     0 1 0 0 0];
A = Mat2Assoc(Amat,'v'); % Convert to Assoc, use 'v1,v2,...,' as labels.
displayFull(A)
J = Jaccard(A);
displayFull(J)

% Same result if we pass in the upper triangle of A
AmatUp = triu(Amat,1);
AUp = Mat2Assoc(AmatUp,'v'); % Convert to Assoc, use 'v1,v2,...,' as labels.
JUp = Jaccard(AUp);
assert(isempty(J - JUp))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
