function res = mtimesCustom(A,B,azero,afunc,mfunc)
% azero = 0; % identity of addition operator
% afunc = @plus;
% mfunc = @times;
% helper:
allButLast = @(x) x(1:end-1);

R = CatValMul(A,B);
Rstruct = struct(R);
v = Rstruct.val;
sep = v(end);
valstrs = Str2mat(struct(R).val);

result = '';
for i=1:size(valstrs,1)
    numRes = CombineCatValMul(allButLast(deblank(valstrs(i,:))),azero,afunc,mfunc);
    result = [result num2str(numRes) sep];
end

Rstruct.val = result;
res = Assoc(Rstruct);  %<-- there is no way to create an Assoc object out of its structure
% Could modify Assoc's constructor to accept a struct.
% Poor man's solution:
%res = Assoc(Rstruct.row,Rstruct.col,Rstruct.val);
