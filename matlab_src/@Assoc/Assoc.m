function A = Assoc(row,col,val,func)
%ASSOC constructs associative array from row, col, val triples
  % row/col/val choices are:   str array or scalar, numeric array or scalar
  % Collision function func is optional.

  % Initialize A.
  A.row = '';  A.col = '';  A.val = '';  A.A = [];

  if (isempty(row) | isempty(col) | isempty(val))
    return;   % Short circuit if nothing in A.
  end

  % Set default function.
  if (nargin < 4)
    func = @min;
  end

  % Default case is everything is numeric.
  i = row;   j = col;   v = val;

  if ischar(row)   % row is a string of keys.

   % Uniq and sort strings.
    [A.row  i_out2in i] = StrUnique(row,'first');

  end

  if ischar(col)   % col is a string of keys.

    % Uniq and sort strings.
    [A.col  j_out2in j] = StrUnique(col,'first');

  end

  if ischar(val)   % val is a string of values.

    % Uniq and sort strings.
    [A.val  v_out2in v] = StrUnique(val,'first');

  end

  % Create sparse connection matrix.
%  if not(isempty(func))
%whos
     A.A = accumarray([i j],double(v),[],func,0,logical(1));
%    A.A = sparse(i,j,v);   % Assumes no collisions.
%  else
%    if isnumeric(val)
%      A.A = sparse(i,j,v);
%    end
%    if ischar(val)
%      % Will take smallest v index..
%      A.A = accumarray([i ; j],v,[],'func');
%    end
%  end

  A=class(A,'Assoc');

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%