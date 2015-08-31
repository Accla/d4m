function res = mtimesCustom(A,B,varargin) %afunc,mfunc,azero)
%mtimesCustom: Performs matrix multiply of two associative arrays over an arbitrary ring.
%Associative array user function.
%  Usage:
%    AB = mtimes(A,B)
%    AB = mtimes(A,B,@min)           % define + as @min
%    AB = mtimes(A,B,@min,@plus)     % define * as @plus
%    AB = mtimes(A,B,@min,@plus,Inf) % define additive identity as Inf
%  Inputs:
%    A = associative array with string values (use num2str if numeric values)
%    B = associative array with string values (use num2str if numeric values)
%    afunc = Function to use as + (default @plus)
%    mfunc = Function to use as * (default @times)
%    azero = Scalar to use as the additive identity (default 0)
%  Outputs:
%    AB = associative array that is the matrix multiply of A and B over the given ring
%         and limited to the intersection of Col(A) and Row(B).
%         Please note the resulting associative array is DENSE.

% TODO: Generalize to non-numeric rings.
% TODO: Modify to support sparse output.
% TODO: Optimize.

% revert to regular mtimes in normal usage
if nargin == 2
    res = mtimes(A,B);
    return
end

afunc = varargin{1};
mfunc = @times;
azero = 0;
if nargin >= 4
    mfunc = varargin{2};
    if nargin >= 5
        azero = varargin{3};
    end
end

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
res = Assoc(Rstruct);  % Creates an Associative Array directly from a structure
%res = Assoc(Rstruct.row,Rstruct.col,Rstruct.val); % Would have to use this otherwise.
