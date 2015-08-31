function [ cNew ] = removeIdentifiers(c)
%removeIdentifiers This function removes meaningless de-identified words
%   Through the de-identification process of the MIMIC data
%	words were replaced by characters surrouneded by [** and **]. Given
%	the character array of the column keys from the r,c,v Associative Array triples,
%	or the Associative Array itself, this function will remove these substrings.
%	Example: [r,c,v]=find(A);
%			 cNew=removeIdentifiers(c);
%			 A=Assoc(r,cNew,v);
%	Example: Anew=removeIdentifiers(A);
nl=char(10);

% extract column keys if passed in Associative Array
if isa(c,'Assoc')
	[r,cNew,v]=find(A);
	else
	cNew=c;
end

% Remove word|[**lkdsjf**]
pattern='word[|]\[\*{2}\S*\*{2}\]\n';
rep='word|\n';
cNew=regexprep(cNew,pattern,rep);

% Remove word|[**klsdkjf
pattern='word[|]\[\*{2}\S*\n';
rep='word|\n';
cNew=regexprep(cNew,pattern,rep);

% Remove word|lkdsf**]
pattern='word[|]\S*\*{2}\]\n';
rep='word|\n';
cNew=regexprep(cNew,pattern,rep);

% Remove [**klsdf from word|lkd[**dkjf
pattern='(word[|]\S+)\[\*{2}\S*\n';
rep='$1\n';
cNew=regexprep(cNew,pattern,rep);

% Remove [**ldksf**] from word|kldsf[**lskdjf**]ldksjf
pattern='(word[|]\S+)\[\*{2}\S\*{2}\](\S+\n)';
rep='$1\nword\|$2';
cNew=regexprep(cNew,pattern,rep);

% Remove klsdf**] from word|lsdkf**]kdlsf
pattern='(word[|])\S*\*{2}\](\S+\n)';
rep='$1$2';
cNew=regexprep(cNew,pattern,rep);

% Output an Associative Array if input is an Associative Array
if isa(c,'Assoc')
	cNew=Assoc(r,cNew,v);
end
		

