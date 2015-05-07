function md = maxdiff(d4mstr)
% Compute maximum difference of numbers in D4M string format like '3,8,2,2,' (which is 6)
if isempty(d4mstr)
    md = 0;
    return
end
nums = str2num(d4mstr);
md = 0;
for i = 1:numel(nums)-1
for j = i+1:numel(nums)
md = max(md,abs(nums(i)-nums(j)));
end
end

end