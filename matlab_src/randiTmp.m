function A = randiTmp(imax,varargin);
% Stub for randi if it isn't available.

  A = rand(varargin{:});

  if numel(imax) == 1
    A = ceil(imax .* A);
  end
  if numel(imax) == 2
    A = ceil((imax(2) - (imax(1)-1)) .* A) + (imax(1)-1);
  end

end
