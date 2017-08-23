function s=addmatlatex(s)
if s == 0
    s = '';
elseif s < 1
    s = num2str(s,'%.2f');
elseif ceil(s)-s == 0 && s >= 10 && s <= 20
    s = num2str(s);
else
    s = ['\num{' num2str(s,'%.3f') '}'];
end
end
