function [Aout] = splitCombWords(A)
%splitCombWords Splits words that have been combined
%   The MIMIC text data contains some cases where words have been
%	put together with no whitespace. If there is obvious punctuation,
%	this can be easily fixed. This function takes in an Associative
%	Array, splits words combined in this way, and outputs an Associative
%	Array with words appropriately split.
%	Example: Aout=splitCombWords(A);
nl=char(10);

% Makes sure delimiter is the new line character
A=NewSep(A,nl);
Awords=A(:,StartsWith(['word|' nl]));% Separate words
A=A-Awords;

[r,c,v]=find(Awords);

% Find words separated by a period
pattern='word\|([a-zA-Z]{2,})\.([a-zA-Z]+)\n';
[match]=regexp(c,pattern,'match');
match=strjoin(match,'');

% Extract from Associative Array of Words
toSplit=Awords(:,match);
[r,c,v]=find(toSplit);

% Split Words in Columns
pattern='(word\|[a-zA-Z]{2,})\.([a-zA-Z]+\n)';
rep='$1\nword\|$2';
cNew=regexprep(c,pattern,rep);

% Duplicate Row Labels
pattern='(\S+\n)';
rep='$1$1';
rNew=regexprep(r,pattern,rep);

% Duplicate Values
vNew=zeros(2*length(v),1);
vNew(1:2:end)=v;
vNew(2:2:end)=v;

% Put split words back into Awords
newSplit=Assoc(rNew,cNew,vNew);
Awords=Awords-toSplit+newSplit;

% Add Awords back into A
Aout=A+Awords;

end

