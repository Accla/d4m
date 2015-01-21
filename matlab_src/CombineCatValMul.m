function v = CombineCatValMul(str,azero,afunc,mfunc)
% Perform numeric combination of the results of a matrix multiply

% adapted from Str2mat
  sep = str(end);  % Assume last entry is separator.

  % Find the end and start locations of each string.
  strSep = find(str == sep);
  
  % take pairs of values %%
  assert(mod(numel(strSep),2)==0);
  newStrSep = zeros(1,numel(strSep)/2);
  for i=1:numel(newStrSep)
      newStrSep(i) = strSep(i*2);
  end
  strSep = newStrSep;
  
  strEnd = strSep - 1;
  strStart = [1 (strSep(1:end-1) + 1)];
  strLen = strEnd - strStart + 1;

  % Compute row index for each character.
  x = double(str);   x(:) = 0;
  x(strSep(1:(end-1))+1) = 1;
  i = cumsum(x) + 1;  i(end) = i(end-1);

  % Compute col index for each character.
  x(:) = 1;
  x(strStart(2:end)) = -strLen(1:(end-1));
  j = cumsum(x);

  % Compute matrix dimensions.
  N = i(end);
  M = max(j);
  mat = char(zeros(N,M,'int8'));

  % Compute offset into matrix.
  ind = i + (j-1)*N;
  mat(ind) = str;

% helper:
allButLast = @(x) x(1:end-1);
%result = zeros(size(mat,1),1);
v = azero;
for i=1:size(mat,1)
    pair = Str2mat(deblank(mat(i,:)));
    v1 = str2num(allButLast(deblank(pair(1,:))));
    v2 = str2num(allButLast(deblank(pair(2,:))));
    %result(i) = mfunc(v1,v2)
    v = afunc(v, mfunc(v1,v2));
end

end



