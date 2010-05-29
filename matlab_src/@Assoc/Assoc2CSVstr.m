function CsvStr = Assoc2CSVstr(A,rowSep,colSep)
%Converts an associative array to a CSV str.


  % Get fields of assoc.
  row = Row(A); col = Col(A);  val = Val(A);
  AA = Adj(A);
  s = size(A);  Nrow = s(1);  Ncol = s(2);

  % Replace seperator in column names and prepend column.
  if ischar(col)
    col(col == col(end)) = colSep;
  else
    col = sprintf(['%d' colSep],col);
  end


  % Create the first row.
  CsvStr = [colSep col(1:end-1) rowSep];


  % Create column of row names.
  if ischar(row)
    row(row == row(end)) = rowSep;
  else
    row = sprintf(['%d' rowSep],row);
  end


  % Make A dense.
  AA  = full(AA);
  zeroVal = max(max(AA)) + 1;
  if (zeroVal == 0)
    zeroVal = 1;
  end
  AA(AA == 0) = zeroVal;


  % Create a dense string list of values.
  if ischar(val)
    val(val == val(end)) = colSep;
    val =  [val '0' colSep];
    Atemp = putVal(A,val);
    Atemp = putAdj(Atemp,AA);
    [temp temp v] = find(Atemp);
  else
    [temp temp v] = find(AA);
    v(v == zeroVal) = 0;
    if isreal(v)
      v = sprintf(['%d' colSep],v);
    else
      v = sprintf(['%d %d' colSep],[real(v).'; imag(v).']);
    end
  end

%  v = strrep(v,[colSep '0' colSep],[colSep colSep]);
%  v = strrep(v,[colSep '0' ],[colSep]);
  v = regexprep(regexprep(v,[colSep '0' ],[colSep]),[colSep '0' ],[colSep]);

  if strcmp(v(1:2),['0' colSep])
    v = v(2:end);
  end

  % Replace every Ncol seperator with rowSep.
  isep = find(v == colSep);
  v(isep(Ncol:Ncol:end)) = rowSep;

  % Concatenate everything together.
  CsvStr = [CsvStr CatStr(row,colSep,v)];

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
