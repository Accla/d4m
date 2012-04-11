function TD = close(T)
% Close any connection to cloud and drop iterator

    T.d4mQuery.reset();
    TD= T;
end
