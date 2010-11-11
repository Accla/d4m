function A = randi(imax,varargin);
% Stub for randi if it isn't available.

  A = rand(varargin{:});

  if numel(imax) == 1
    A = floor((imax + 1) .* A);
  end
  if numel(imax) == 2
    A = floor((imax(2) + 1) .* A) - imax(1);
  end

end
