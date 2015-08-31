% The mtimesCustom function performs Associative Array multiplication 
% with arbitrary, user-defined + and * functions
% and user-defined additive identity.
% Defaults are + as @plus, * as @times, and the additive identity as 0.
% Associative array matrix multiplication over an arbitrary ring.  It could use some optimization and generalization to non-numeric rings.

A = Assoc('r1,r1,r2,r2,','c1,c2,c1,c2,','1,2,3,4,');
A2collide = A * A.';
A2normal  = mtimesCustom(A,A.',@plus,@times,0);
A2minplus = mtimesCustom(A,A.',@min,@plus,Inf);

displayFull(A)
%c1,c2,
%r1,1, 2, 
%r2,3, 4, 
displayFull(A.')
%r1,r2,
%c1,1, 3, 
%c2,2, 4, 
displayFull(A2collide)
%r1,r2,
%r1,2, 2, 
%r2,2, 2, 
displayFull(A2normal)
%r1,r2,
%r1,5, 11,
%r2,11,25,
displayFull(A2minplus)
%r1,r2,
%r1,2, 4, 
%r2,4, 6, 

