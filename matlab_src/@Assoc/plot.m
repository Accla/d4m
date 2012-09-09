function plot(A,varargin)
%plot: Creates a plot of an associative array vector or matrix of values.
%Associative array user function.
%  Usage:
%    plot(A)
%  Inputs:
%    A = 1xN, Nx1, or NxM associative array with numeric values
%  Outputs:
%

  sizeA = size(A);

  [r c v] = find(A);


  if (sizeA(1) == 1)

    plot(1:nnz(v),v,'.');
    set(gca,'XTick',1:nnz(v));
    c(c == c(end)) = '|';
    set(gca,'XTick',1:nnz(v),'XTickLabel',c(1:end-1))

  elseif (sizeA(2) == 1)

    plot(v,1:nnz(v),'.');
    set(gca,'YTick',1:nnz(v));
    r(r == r(end)) = '|';
    set(gca,'YTick',1:nnz(v),'YTickLabel',r(1:end-1))

  else
    plot(Adj(A),'.-');
    set(gca,'XTick',1:size(A,1));
    set(gca,'XTick',1:size(A,1),'XTickLabel',Str2mat(Row(A)))
    legend(Str2mat(Col(A)))
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

