function display(A)
%DISPLAY prints out non-zero triples in an associative array.
  [row col val] = find(A);

  if ischar(row)
    row =  Str2mat(row);
  end

  if ischar(col)
    col =  Str2mat(col);
  end

  if ischar(val)
    val =  Str2mat(val);
  end

  [N M] = size(row);

  for i = 1:N
    disp(['(' deblank(num2str(row(i,:))) ',' deblank(num2str(col(i,:))) ')     ' deblank(num2str(val(i,:)))]);
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

