function A = Assoc(row,col,val,func)
%Assoc: Constructs an associative array from row, column, and value triples.
%Associative array user function.
%  Usage:
%    A = Assoc(row,col,val)
%    A = Assoc(row,col,val,func)
%  Inputs:
%    row = length 1 or M string list or index column vector with no empty rows
%    col = length 1 or M string list or index column vector with no empty columns
%    val = length 1 or M string list or numeric column vector
%    func = collision function, default is @min, other options include @sum
%  Outputs:
%    A = associative array made from the triples row, col, and val
%  Examples:
%    A = Assoc('r1,r2,','c1,c2,','v1,v2,')
%    A = Assoc('r1,r1,','c1,',[1 2].',@sum)

  if(nargin == 1)
      if ~isstruct(row) || ~isfield(row,'row') ...
         || ~isfield(row,'col') || ~isfield(row,'A')
        error('Bad input structure to Assoc');
      end
      A=class(row,'Assoc');
      return;
  end

  % Initialize A.
  A.row = '';  A.col = '';  A.val = '';  A.A = [];

  if (isempty(row) || isempty(col) || isempty(val))
    A=class(A,'Assoc');
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

    % Set up global incase we want to combine string values.
    global AssocOldValStrMatGlobal AssocNewValStrGlobal AssocValStrIndexGlobal AssocValCharIndexGlobal
    AssocOldValStrMatGlobal = Str2mat(A.val);
    AssocNewValStrGlobal = val;
    AssocNewValStrGlobal(:) = 0;
    AssocValStrIndexGlobal = 1;
    AssocValCharIndexGlobal = 1;
  end

  % Create sparse connection matrix.
  Nmax = max([numel(i) numel(j) numel(v)]);
  if (numel(i) == 1)
    i = repmat(i,Nmax,1);
  end
  if (numel(j) == 1)
    j = repmat(j,Nmax,1);
  end
  if (numel(v) == 1)
    v = repmat(v,Nmax,1);
  end

  A.A = accumarray([i j],double(v),[],func,0,true);

  A=class(A,'Assoc');

  if ischar(val)   % val is a string of values.
    if  (AssocValStrIndexGlobal > 1)
%      A.val = AssocNewValStrGlobal;
      sep = val(end);
      iend = find(AssocNewValStrGlobal == sep, 1, 'last' );
      A = putVal(A,AssocNewValStrGlobal(1:iend));
      AssocOldValStrMatGlobal = '';
      AssocNewValStrGlobal = '';
      AssocNewValStrGlobal = '';
      AssocValStrIndexGlobal = '';
      AssocValCharIndexGlobal = '';
      [r c v] = find(A);
%      A = Assoc(r,c,v);
      A = Assoc(r,c,v,@min);
    end
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

