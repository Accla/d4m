function i = StrSubsref(Arow,row)
%STRSUBSREF returns locations of one set of string in another.
  % Returns string locations of row in Arow.
  % Arow : sorted str
  % row choices are:   str, regexp

  rowMat = Str2mat(row);
  % Find if there are any regexp.
  [iregexp temp temp] = find((rowMat == '*'));
  iregexp = unique(iregexp);
  ikey = ones(numel(rowMat(:,1)),1);
  ikey(iregexp) = 0;
  [ikey temp temp] = find(ikey);
  i = [];
  wordnumber = cumsum(double(Arow == Arow(end)))+1;
  wordnumber(end) = wordnumber(end)-1;
  if (numel(iregexp) > 0)
    for ireg = iregexp.'
  %    regExpStr = strrep(rowMat(ireg,:),'*','+');  % Fix stupid matlab regexp syntax.
      regExpStr = Mat2str(rowMat(ireg,:));
      regExpStr = regExpStr(1:(end-1));
      i = [i wordnumber(regexp(Arow,regExpStr))];
    end
  end
  if (numel(ikey) > 0)
    % Search Arow for matches.
    rowKey = Mat2str(rowMat(ikey,:));
    imatch = StrSearch(Arow,rowKey);
    % Eliminate matches that are not exact.
    i = [i imatch((imatch > 0))];
  end
  i = sort(i);
  
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

