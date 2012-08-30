function s = size(A,varargin)
%size: Returns the dimensions of an associative array.
%Associative array user function.
%  Usage:
%    s = size(A)
%    s = size(A,dim)
%  Inputs:
%    A = associative array
%    dim = dimension to get the size of; 1 = rows, 2 = columns
%  Outputs:
%    s =  1 or 2 element vector containing the size of the dimensions

   if nargin == 1
     s = size(A.A);
   end
   if nargin == 2
     s = size(A.A,varargin{1});
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

