function c = removePunctuation(c)
nl=char(10);
punctuation = '*%$<>,.?!@#&/\:;#{[()]}+=-_`~"'''; % remaining: ^^$%*0123456789

for i=1:length(punctuation)
    c=strrep(c,punctuation(i),'');
end

end