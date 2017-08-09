% rcv2mat Helper function to convert r,c,v vectors from subsref to
% matrix
function y = rcv2mat(r, c, v)

% this is a temporary hack 
% assumptions : r, c, v are cell arrays and the expected output is
% a matrix of doubles

n = length(r); % total number of elements in matrix




