function A = randiTmp(imax,varargin)
%randiTmp: DEPRECATED. Stub for randi if it isn't available.
%Associative array internal function.
%  Usage:
%    A = randiTmp(imax,varargin)
%  Inputs:
%    imax = max random integer to generate
%    varargin = optional arguments into rand()
%  Outputs:
%    A = random integers in the range [1,imax]

% Stub for randi if it isn't available.

  A = rand(varargin{:});

  if numel(imax) == 1
    A = ceil(imax .* A);
  end
  if numel(imax) == 2
    A = ceil((imax(2) - (imax(1)-1)) .* A) + (imax(1)-1);
  end

end
