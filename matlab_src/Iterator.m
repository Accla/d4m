function Ti = Iterator(T,flag,nlimit);
%Iterator: Creates a new iterator inside of a table and sets its limits.
%Database user function.
%  Usage:
%    Ti = Iterator(T,flag,nlimit)
%  Inputs:
%    T = database table binding
%    flag = only option is 'elements'
%    nlimit = maximum number items to return in a single query
%  Outputs:
%    Ti = new database table binding with new limit
%  Example:
%    Ti = Iterator(T,'elements',1000);   % Construct iterator.
%    A = Ti(:,:);                        % Execute first query.
%    while nnz(A)                        % Check if A has results.
%      size(A)                           % Do something with A.
%      A = T();                          % Execute next query.
%    end

% Creates a new iterator inside of a table and sets its limits.
   if strcmp(flag,'elements')
     Ti = putNumLimit(T,nlimit);
   end
   if strcmp(flag,'rows')
     Ti = putNumRow(T,nlimit);
   end
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


