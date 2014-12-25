function varargout = Text2word(rowText,valText,wordSep)
%Text2word: Converts list of text strings to list of words.
%String array user function.
%  Usage:
%    A = Text2word(rowText,valText,wordSep)
%    [rowWord colWord valPos] = Text2word(rowText,valText,wordSep)
%  Inputs:
%    rowText = list of Ntext unique keys each corresponding to one entry in valText
%    valText = list of Ntext strings made of up word separated by wordSep
%    wordSep = single character word seperator
% Outputs:
%    rowWord = list of Nword keys drawn from rowText
%    colWord = list of Nword words drawn from valText
%    valPos = lit of Nword positions corresponding to the appearance of each word in the text string
%    A = Associative array corresponding to Assoc(rowWord,colWord,1,@sum)

 
    % Split apart words.
    textMat = Str2mat(valText);
    word = valText;
    word(word == wordSep) = valText(end);
    wordEnd = (textMat == wordSep) | (textMat == valText(end));
    wordCount = sum(wordEnd,2);
    Nword = sum(wordCount);
    wordCountSum = cumsum(wordCount);
    ii = zeros(Nword,1);
    ii(wordCountSum(1:end-1)+1) = 1;
    ii(1) = 1;
    iiSum = cumsum(ii);

    x = transpose(cumsum(wordEnd,2));
    valPos = x(find(transpose(wordEnd)));

    rowTextMat = Str2mat(rowText);
    wordMat = Str2mat(word);
    iOK = find(wordMat(:,1) ~= valText(end));
    colWord = Mat2str(wordMat(iOK,:));
    rowWordMat = rowTextMat(iiSum,:);
    rowWord = Mat2str(rowWordMat(iOK,:));

  if (nargout <= 1)
    varargout{1} = Assoc(rowWord,colWord,1,@sum);
  end

  % Return triple.
  if (nargout == 3)
    varargout{1} = rowWord;
    varargout{2} = colWord;
    varargout{3} = valPos;
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

