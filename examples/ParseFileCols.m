function varargout = ParseFileCols(fname,sep,Nrow,subcols)
% PARSEFILE: parse file into a sequence of cols.

  fid = fopen(fname, 'r');
    AentStr = fread(fid, inf, 'uint8=>char').';
  fclose(fid);

  % Make all sep seperated and delete first row.
  AentStr(AentStr == AentStr(end)) = sep;
  isep = find(AentStr == AentStr(end));
  % Eliminate first row.
%  AentStr = AentStr(isep(Nrow)+1:end);

  % Find seperators.
  isep = find(AentStr == AentStr(end));


  j = 1;
  for i=subcols

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
    varargout(j) = {AentStr(find(x))};
    j = j + 1;

  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

