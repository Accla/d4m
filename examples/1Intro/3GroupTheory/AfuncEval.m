function Av12 = AfuncEval(Afunc,v1,v2);
% Evaluate v1 and v2 against Afunc.
% If either v1 or v2 is a vector must be the same length as Afunc.

  [tmp tmp v1] = find(v1);
  [tmp tmp v2] = find(v2);

  % Initialize result.
  AfuncSize = size(Afunc);
  Nfunc = AfuncSize(1);
  Av12 = Assoc('','','');
  if (numel(v1) == 1)
    v1 = repmat(v1,Nfunc,1);
  end
  if (numel(v2) == 1)
    v2 = repmat(v2,Nfunc,1);
  end

  % Create associative arrays of inputs.
  Av1 = Assoc(Row(Afunc),'v1,',v1);
  Av2 = Assoc(Row(Afunc),'v2,',v2);

  % Check for NaN and use KeyFunc to resolve.
  i = find( ~isnan(v1) & isnan(v2) );
  if nnz(i)
    Av12 = Av12 + putCol(Av1(Row(Afunc(i,'KeyFunc,') == 'union,'),'v1,'),'v12,');
    Av12 = Av12 + Assoc(Row(Afunc(i,'KeyFunc,') == 'intersect,'),'v12,',NaN);
  end

  i = find( isnan(v1) & ~isnan(v2) );
  if nnz(i)
    Av12 = Av12 + putCol(Av2(Row(Afunc(i,'KeyFunc,') == 'union,'),'v2,'),'v12,');
    Av12 = Av12 + Assoc(Row(Afunc(i,'KeyFunc,') == 'intersect,'),'v12,',NaN);
  end

  i = find( isnan(v1) & isnan(v2) );
  if nnz(i)
    Av12 = Av12 + Assoc(Row(Afunc(i,'KeyFunc,') == 'union,'),'v12,',NaN);
    Av12 = Av12 + Assoc(Row(Afunc(i,'KeyFunc,') == 'intersect,'),'v12,',NaN);
  end

  i = find( ~isnan(v1) & ~isnan(v2) );
  if nnz(i)
    r12 = ''; v12 = '';  i12 = [];
    ii = find(v1 > v2);
    if nnz(ii)
      [r c v] = find(Afunc(ii,'v1>v2,'));
      r12 = [r12 r];  v12 = [v12 v];  i12 = [i12; ii];
    end
    ii = find(v1 == v2);
    if nnz(ii)
      [r c v] = find(Afunc(ii,'v1=v2,'));
      r12 = [r12 r];  v12 = [v12 v];  i12 = [i12; ii];
    end
    ii = find(v1 < v2);
    if nnz(ii)
      [r c v] = find(Afunc(ii,'v1<v2,'));
      r12 = [r12 r];  v12 = [v12 v];  i12 = [i12; ii];
    end
    Aexpr = Assoc(r12,v12,i12);
    v = zeros(Nfunc,1);
    [tmp tmp k] = find(Aexpr(:,'v1,'));    v(k) = v1(k);
    [tmp tmp k] = find(Aexpr(:,'v2,'));    v(k) = v2(k);
    [tmp tmp k] = find(Aexpr(:,'NaN,'));    v(k) = NaN;
    [tmp tmp k] = find(Aexpr(:,'+Inf,'));    v(k) = +Inf;
    [tmp tmp k] = find(Aexpr(:,'-Inf,'));    v(k) = -Inf;

    % Put v into result.
%keyboard
    Av12 = Av12 + Assoc(r12,'v12,',v(i12));

  end

return
end





