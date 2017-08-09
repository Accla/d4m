% SCIDB2MTX Convert output of SciDB query to matrix
% assumes that the data is all numeric
function y = scidb2mtx(rows, cols, slice, vals)

if isempty(rows) || isempty(cols) || isempty(slice)
    y = [];
else
    %{
    % implementation below was for cell array of strings returned
    % by [r,c,z,v] = T()... command. str2double() was EXTREMELY
    % slow : 1 hour for 4096x4096 matrix conversion
    % querySciDB() has been changed so that it returns doubles
    rows = cat(1, str2double(rows(:)));
    rows = rows-min(rows)+1;
    cols = cat(1, str2double(cols(:)));
    cols = cols-min(cols)+1;
    slice = cat(1, str2double(slice(:)));
    slice = slice-min(slice)+1;
    vals = cat(1, str2double(vals(:)));
    %}
    
    nr = range(rows)+1;
    nc = range(cols)+1;
    ns = range(slice)+1;
    
    %idx = sub2ind([nr nc ns], rows, cols, slice);
    idx = sub2ind([nr nc ns], rows-min(rows)+1, cols-min(cols)+1, slice-min(slice)+1);
    y = zeros(nr, nc, ns);
    y(idx) = vals;
end





