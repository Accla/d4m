function util_Require( varargin )
%util_Require: Require all the variable names to be present in the calling script
str = 'all([';
for i = 1:nargin
    ss = strsplit(varargin{i},{' ', ','});
    for j = 1:numel(ss)
        str = [str 'exist(''' ss{j} ''',''var'') '];
    end
end
str = [str ']);'];
if ~evalin('caller', str)
    s = '';
    for i = 1:nargin
        s = [s varargin{i} ' '];
    end
    error(['caller does not have all the variables: ' s]);
end
end

