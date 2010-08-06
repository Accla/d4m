function A = conv(A,window)
%CONV convoles assoc vector with window vector (using 'same' syntax).
% Assumes Val is double values and Row/Col are keys that can be
% mapped onto integers in sorted order.

  % Get size of A.
  sizeA = size(A);  N = sizeA(1);  M = sizeA(2);

  W = length(window);    % Get windows size.

  if (N > M)      % Get assoc vector index and values.
    [indexStr ~ val] = find(A);
  else
    [~ indexStr val] = find(A);
  end

  index = str2num(indexStr);    % Convert index to integers.

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
  dNotIsoIndex = diff(double(not([1 isoIndex 1])))

  groupStart = find(dNotIsoIndex(1:end-1) == 1);
  groupStop = find(dNotIsoIndex(2:end) == -1);

  Ngroup = numel(groupStart);
  for iGroup =1:NGroup;
    rG = groupStart(iGroup):groupStop(iGroup);
    iG = index(r);
    vG = val(r);
    convG = conv(sparse((iG - iG(1) + 1),1,vG),window,'same');

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

