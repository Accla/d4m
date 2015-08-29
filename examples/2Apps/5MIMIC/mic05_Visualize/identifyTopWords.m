function topWords = identifyTopWords(h,nTopWords)
%% This function accepts h (D4M associative array) and
%% nTopWords (double), and returns as many words (struct)
%% as nTopWords. Each word has a name (string) and weight (double).
    topWords = cell(1,nTopWords);
    topWordsAssoc = TopColPerRow(h,nTopWords);
    [r,c,v] = find(topWordsAssoc);
    delimiter = c(end);
    topWordNames = strsplit(c,{delimiter,'word|'}); % strip 'word|' prefix
    topWordNames = topWordNames(2:end-1); % drop empty string on ends
    for iWord = 1:nTopWords
        wordName = topWordNames{iWord};
        wordWeight = v(iWord);
        topWords{iWord} = struct('wordName',wordName,'wordWeight',wordWeight);
    end
end
