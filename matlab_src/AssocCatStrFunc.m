function j = AssocCatStrFunc(i)
%AssocCatStrFunc: Concatenates strings inside a matrix multiply.
%Associative array internal function for CatKeyMul and CatValMul.
%  Usage:
%    j = AssocCatStrFunc(i)
%  Inputs:
%    i = set of indices into AssocOldValStrMatGlobal
% Outputs:
%    j = an index into AssocOldValStrGlobal
  global AssocOldValStrMatGlobal AssocNewValStrGlobal AssocValStrIndexGlobal AssocValCharIndexGlobal

  % Get all the strings of i.
  ValStr = Mat2str(AssocOldValStrMatGlobal(i,:));

  % Get length.
  nChar = numel(ValStr);

  % Get current locations.
  j = AssocValStrIndexGlobal;
  k = AssocValCharIndexGlobal;

  % Take care of collisions.
%  if (numel(i) > 1)
    % Define new seperator.
    sep = ValStr(end);
    subSep = ',';
    if (subSep == sep)
      subSep = ';';
    end

    % Replace interior seperators.
    ValStr(ValStr == sep) = subSep;
    ValStr(nChar+1) = sep;
    nChar = nChar+1;
%  end

  % Append to new value str.
  AssocNewValStrGlobal(k:(k+nChar-1)) = ValStr;

  % Increment index.
  AssocValStrIndexGlobal = AssocValStrIndexGlobal + 1;

  % Increment char count,
  AssocValCharIndexGlobal = AssocValCharIndexGlobal + nChar;

end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
