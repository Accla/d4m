function spy(A,varargin)
%spy: Creates a 2D plot of non-empty entries in an associative array; clicking on a dot shows its value.
%Associative array user function.
%  Usage:
%    spy(A)
%  Inputs:
%    A = associative array
%  Outputs:
%

  if exist('OCTAVE_VERSION','builtin')
    spy(Adj(A));
    return
  end

  MaxTicks = 20;

%  f = figure;
  % Creates labeled spy plot of A.
%  spy(A.A);
  [i j v] = find(A.A);

  %  global AssocSpyAglobal
  AssocSpyAglobal = struct(A);

  % Set the kinds of TickLabels to use.
  AssocSpyAglobal.TickLabelsON = [1 1];

  if (nargin < 2)
    plot(j,i,'.','MarkerSize',6);
  else
    Var1 = varargin{1};
    if IsClass(Var1,'Assoc')
      % Get markers.
      Var1Adj = Adj(Var1);
      markerStr = Val(Var1);
      markerStrMat = Str2mat(markerStr);
      % Loop over each markertype.
      for imark = 1:NumStr(markerStr)
        imarkStr = Mat2str(markerStrMat(imark,:));
        [ii jj vv] = find(Var1Adj == imark);
        plotCmd = ['plot(jj,ii,' imarkStr(1:end-1) ');'];
        eval(plotCmd);
        hold('on');
      end
      hold('off');
    else
      AssocSpyAglobal.TickLabelsON =  varargin{1};
    end
  end

  axis('ij','tight');

%  z = zoom(f);
  z = zoom(gcf);

  set(z,'ActionPostCallback',@AssocSpyPostCallback);

  dcm_obj = datacursormode(gcf);
  set(dcm_obj,'UpdateFcn',@AssocSpyUpdateFcn);
  datacursormode('on');

  [N M] = size(A.A);  % Get sizes of matrix.

%  if isfield(A,'row')   % row is a string of keys.
  if not(isempty(A.row))   % row is a string of keys.

    % Pre-compute string seperation positions.
    AssocSpyAglobal.rowSep = find(A.row == A.row(end));
    [YTick YTickLabel] = AssocSpyTicks(A.row,[1 N]);

    if (AssocSpyAglobal.TickLabelsON(2))
      set(gca,'YTick',YTick,'YTickLabel',YTickLabel)
    else
      set(gca,'YTick',YTick,'YTickLabel',num2str(YTick.'))

      % Print full labels to screen.
      disp('YTickLabels');
      rowTmp = A.row;
      rowTmp(AssocSpyAglobal.rowSep) = char(10);
      rowMat = Str2mat(rowTmp);
      disp(Mat2str([num2str(YTick.','%d:') rowMat(YTick,:)]))
    end

  end

%  if isfield(A,'col')   % col is a string of keys.
  if not(isempty(A.col))   % col is a string of keys.

    % Pre-compute string seperation positions.
    AssocSpyAglobal.colSep = find(A.col == A.col(end));
    [XTick XTickLabel] = AssocSpyTicks(A.col,[1 M]);

    if (AssocSpyAglobal.TickLabelsON(1))
      set(gca,'XTick',XTick,'XTickLabel',XTickLabel)
    else
      set(gca,'XTick',XTick)

      % Print full labels to screen.
      disp('XTickLabels');
      colTmp = A.col;
      colTmp(AssocSpyAglobal.colSep) = char(10);
      colMat = Str2mat(colTmp);
      disp(Mat2str([num2str(XTick.','%d:') colMat(XTick,:)]))
    end

  end

%  if isfield(A,'val')
  if not(isempty(A.val))   % val is a string of keys.
    sep = A.val(end);  % Assume last entry is separator.
    % Find the end and start locations of each string
    % and store for later use.
    AssocSpyAglobal.valSep = find(A.val == sep);
  end

  % Create global unique to this figure.
  globalVarName = ['AssocSpyAglobal' num2str(double(gcf))];
  eval(['global ' globalVarName]);
  eval([globalVarName '= AssocSpyAglobal;']);

end


function [Tick TickLabel] = AssocSpyTicks(str,lim)

  maxTicks = 20;
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


% Need to control plot so that when zoom happens
% a new Associative array is generated:
%   A(i1:i2:,j1:j2)
% This will eliminate that the empty rows and cols
% for the zoom and make it more effective?

function AssocSpyPostCallback(obj,event_obj)
  % Create global unique to this figure.
  globalVarName = ['AssocSpyAglobal' num2str(double(gcf))]; 
  eval(['global ' globalVarName]);
  eval(['AssocSpyAglobal = ' globalVarName ';']);

%  global AssocSpyAglobal


%  currXTickLabel = get(event_obj.Axes,'XTickLabel')
%  currXTick = get(event_obj.Axes,'XTick')

  currYLim = get(event_obj.Axes,'YLim');
%  if isfield(AssocSpyAglobal,'row')
  if not(isempty(AssocSpyAglobal.row))
    [YTick YTickLabel] = AssocSpyTicks(AssocSpyAglobal.row,currYLim);

    if (AssocSpyAglobal.TickLabelsON(2))
      set(gca,'YTick',YTick,'YTickLabel',YTickLabel)
    else
      set(gca,'YTick',YTick,'YTickLabel',num2str(YTick.'))

      % Print full labels to screen.
      disp('YTickLabels');
      rowTmp = AssocSpyAglobal.row;
      rowTmp(AssocSpyAglobal.rowSep) = char(10);
      rowMat = Str2mat(rowTmp);
      disp(Mat2str([num2str(YTick.','%d:') rowMat(YTick,:)]))
    end

  end

  currXLim = get(event_obj.Axes,'XLim');
%  if isfield(AssocSpyAglobal,'col')
  if not(isempty(AssocSpyAglobal.col))
    [XTick XTickLabel] = AssocSpyTicks(AssocSpyAglobal.col,currXLim);

    if (AssocSpyAglobal.TickLabelsON(1))
      set(gca,'XTick',XTick,'XTickLabel',XTickLabel)
    else
      set(gca,'XTick',XTick)

      % Print full labels to screen.
      disp('XTickLabels');
      colTmp = AssocSpyAglobal.col;
      colTmp(AssocSpyAglobal.colSep) = char(10);
      colMat = Str2mat(colTmp);
      disp(Mat2str([num2str(XTick.','%d:') colMat(XTick,:)]))
    end

  end

end

function txt = AssocSpyUpdateFcn(empt,event_obj)
  % Create global unique to this figure.
  globalVarName = ['AssocSpyAglobal' num2str(double(gcf))]; 
  eval(['global ' globalVarName]);
  eval(['AssocSpyAglobal = ' globalVarName ';']);
%  global AssocSpyAglobal

  pos = get(event_obj,'Position');
  row = pos(2);  rowStr = num2str(row);
  col = pos(1);  colStr = num2str(col);
  val = AssocSpyAglobal.A(row,col);  valStr = num2str(val);

%  if isfield(AssocSpyAglobal,'row')
  if not(isempty(AssocSpyAglobal.row))
    rowStart = 1;
    if (row > 1)
      rowStart = AssocSpyAglobal.rowSep(row-1)+1;
    end
    rowEnd = AssocSpyAglobal.rowSep(row)-1;
    rowStr = AssocSpyAglobal.row(rowStart:rowEnd);
  end

%  if isfield(AssocSpyAglobal,'col')
  if not(isempty(AssocSpyAglobal.col))
    colStart = 1;
    if (col > 1)
      colStart = AssocSpyAglobal.colSep(col-1)+1;
    end
    colEnd = AssocSpyAglobal.colSep(col)-1;
    colStr = AssocSpyAglobal.col(colStart:colEnd);
  end
 
%  if isfield(AssocSpyAglobal,'val')
  if not(isempty(AssocSpyAglobal.val))
    valStart = 1;
    if (val > 1)
      valStart = AssocSpyAglobal.valSep(val-1)+1;
    end
    valEnd = AssocSpyAglobal.valSep(val)-1;
    valStr = AssocSpyAglobal.val(valStart:valEnd);
  end


  valStrWrap = valStr;
  valStrWrap(50:50:end) = char(10);
  txt = {['row: ' rowStr],['col: ' colStr],['val: ' valStrWrap]};
  disp(txt{1});
  disp(txt{2});
  disp(valStr);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

