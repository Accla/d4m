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

    plot(1:nnz(v),v,'.-');   axis('tight');
%    set(gca,'XTick',1:nnz(v));
%    ccell = cell(NumStr(c),1);
%    cmat = Str2mat(c);
%    for i = 1:NumStr(c)
%       ci = Mat2str(cmat(i,:));
%       ci = ci(1:end-1);
%       ccell{i} = ci;
%    end
%    set(gca,'XTick',1:nnz(v),'XTickLabel',ccell)
    [XTick XTickLabel] = AssocPlotTicks(Col(A),[1 size(A,2)]);
    set(gca,'XTick',XTick,'XTickLabel',XTickLabel)

  elseif (sizeA(2) == 1)

    plot(v,1:nnz(v),'.');  axis('tight');
    set(gca,'YTick',1:nnz(v));
    rcell = cell(NumStr(r),1);
    rmat = Str2mat(r);
    for i = 1:NumStr(r)
       ri = Mat2str(rmat(i,:));
       ri = ri(1:end-1);
       rcell{i} = ri;
    end
    set(gca,'YTick',1:nnz(v),'YTickLabel',rcell)

  else
    plot(Adj(A),'.-');  axis('tight');
    if (not(isempty(A.row)))
      [XTick XTickLabel] = AssocPlotTicks(Row(A),[1 size(A,1)]);
      set(gca,'XTick',XTick,'XTickLabel',XTickLabel)
    end
    legend(Str2mat(Col(A)),'Location','NorthWest')
  end

end

function [Tick TickLabel] = AssocPlotTicks(str,lim)

  maxTicks = 10;
  maxLabelLength = 20;

  Tick = ceil(lim(1)):floor(lim(2));
  if (numel(Tick) > maxTicks)
    Tick = round(Tick(1):((Tick(end) - Tick(1))/(maxTicks-1)):Tick(end));
  end

  sep = str(end);
  strMat = Str2mat(str);
  TickLabel = strMat(Tick,:);
  if (numel(TickLabel(1,:)) > maxLabelLength)
    TickLabel = TickLabel(:,1:maxLabelLength);
    TickLabel((TickLabel(:,end) > 0),end) = sep;
  end

  TickLabelMat = TickLabel;
  TickLabel = cell(size(TickLabelMat,1),1);
  for i =1:size(TickLabelMat,1)
     iTickLabelStr = Mat2str(TickLabelMat(i,:));
     TickLabel{i} = iTickLabelStr(1:end-1);
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

