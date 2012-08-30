function A = conv(A,window)
%conv: Convolves associative array vector with window vector (using 'same' syntax).
%Associative array utility function.
%  Usage:
%    A = conv(A,window)
%  Inputs:
%    A = Nx1 or 1xN associative array with numeric values and integer keys
%    window = numeric vector
%  Outputs:
%    A = convolution of A with window

  % Get size of A.
  sizeA = size(A);  N = sizeA(1);  M = sizeA(2);

  W = length(window);    % Get windows size.

  if (N > M)      % Get assoc vector index and values.
    [indexStr tmp val] = find(A);
  else
    [tmp indexStr val] = find(A);
  end

  index = str2num(indexStr);    % Convert index to integers.
  sep = indexStr(end);
  if ((sep == char(10)) | (sep == char(13)))
    index = index.';   % Transpose.
  end

  convVal = val; convVal(:) = -1;    % Init convolved values.

  dIndex = diff(index);   % Get difference.

  % Find isolated indices.
  isoIndex  = [(dIndex > floor(W/2)) 1];
  isoIndex  = [1 isoIndex(1:end-1)] & isoIndex;
  iIsoIndex = find(isoIndex);
  iIndex = find(not(isoIndex));

  % Compute values of isolated indices.
  convVal(isoIndex) = val(isoIndex) .* window(floor(W/2)+1);


  % Find groups of non-isolated indexes.
  dNotIsoIndex = diff(double(not([1 (dIndex > floor(W/2)) 1])));

  groupStart = find(dNotIsoIndex == 1);
  groupStop = find(dNotIsoIndex == -1);

  % Loop through each group and convolve.
  Ngroup = numel(groupStart);
  for iGroup =1:Ngroup;
    rG = groupStart(iGroup):groupStop(iGroup);
    iG = index(rG);
    vG = val(rG);
    iG0 = iG - iG(1) + 1;
%    convG = conv(full(sparse(iG0,1,vG)),window,'same');
    convG = conv(full(sparse(iG0,1,vG)),window);
    convVal(rG) = convG(floor(W/2) + iG0);
  end

  if (N > M)      % Put assoc vector index and values.
    A = Assoc(indexStr,Col(A),convVal);
  else
    A = Assoc(Row(A),indexStr,convVal);
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

