function Asub = randRow(A,varargin)
%randRow: Randomly selects rows from an associative array or database table.
%Associative array and database utility function.
%  Usage:
%     Asub = randRow(A,Nsub)
%     Asub = randRow(T,Ti,Nsub)
%  Inputs:
%    A = associative array
%    T = database table
%    Ti = database index table
%    Nsub = number of random rows to choose
%  Outputs:
%    Asub = associative array holding randomly selected rows

%RANDROW returns up to Nsub random subrows of an associative array.
   if nargin == 2
     Nsub = varargin{1};
   elseif nargin == 3
     Nsub = varargin{2};
   end

   [N M] = size(A.A);

   Asub = A;  % Copy input.
   if (Nsub < N)
%     isub = unique(randi(N,Nsub,1));   % Generate sub-indices.
     isub = unique(randiTmp(N,Nsub,1));   % Generate sub-indices.
     Asub.A = Asub.A(isub,:);    % Copy Adj matrix.
     if not(isempty(A.row))
       rowMat = Str2mat(A.row);
       Asub.row = Mat2str(rowMat(isub,:));    % Copy row keys.
     end
  end

  Asub = reAssoc(Asub);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

