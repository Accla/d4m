function [Aout] = cleanWords(A)
%cleanWords Runs pre-processing on words to clean them up
%   The MIMIC text data is messy, this function calls helper
%	functions that will cleat it up, including separating some
%	words missing whitespace between them, removing noisy characters
%	removing punctuation, and removing stopwords.

 nl = char(10);
 Aout=NewSep(A,nl); % Makes sure delimiter is new line character
 Awords=Aout(:,StartsWith(['word|' nl])); % Separate out words for cleaning
 Aout=Aout-Awords;
 Awords=splitCombWords(Awords); % Splits words where whitespace was removed
 
% Remove punctuation and noisy characters
 [r,c,v]=find(Awords);
 c=removeIdentifiers(c);
 c=lower(c);
 c=strrep(c,'*','');
 c=strrep(c,'%','');
 c=strrep(c,'$','');
 c=regexprep(c,'\d','');
 c=removePunctuation(c);
 
 % Re-form Awords and remove stopwords
 Awords=Assoc(r,c,v);
 Awords=Awords-Awords(:,['word|' nl]);
 Awords=removeStopWords(Awords);
 Aout=Aout+Awords;

