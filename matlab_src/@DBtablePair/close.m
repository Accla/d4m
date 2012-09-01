function TD = close(T)
%close: Reset iterator in a table object.
%Database table utility function.
%  Usage:
%    TD = close(T)
%  Inputs:
%    T = database table or table pair object
% Outputs:
%    TD = database table or table pair object

    T.d4mQuery.reset();
    TD= T;
end
