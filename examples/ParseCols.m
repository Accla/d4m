function varargout = ParseCols(AentStr);
% PARSECOLS: parse long string into a sequence of cols.

Nrow = nargout;

% Load data.

tic;
  % Make all , sep.
  AentStr(AentStr == AentStr(end)) = ',';

  % Find seperators.
  isep = find(AentStr == AentStr(end));


  for i=1:Nrow

    disp(['Parsing col: ' num2str(i)]);

    x = zeros(1,numel(AentStr),'int8');
    i1 = i-1;    i2 = i;

    if (i == 1)
      x(1) = 1;  i1 = Nrow;
    end

    x(isep(i1:Nrow:end)+1) = 1;
    x(isep(i2:Nrow:end)+1) = -1;
    x = logical(cumsum(single(x)));
    x(end) = 0;
    varargout(i) = {AentStr(find(x))};

  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

