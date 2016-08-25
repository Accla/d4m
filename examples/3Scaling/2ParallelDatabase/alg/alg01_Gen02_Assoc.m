%function alg01_Gen_Assoc(tname, infoFunc)
% Create Assoc files from row, col, val files. Adjacency and Incidence.
% alg01_Gen_Assoc('DH_pg10_20160331', @util_updateInfo);
util_Require('tname infoFunc')
% tname = 'DH_pg10_20160331'; infoFunc = @disp;

dname = [pwd filesep tname];
[~,~,~] = mkdir(dname);                         % Ensure directory exists
Nfile = size(dir([dname filesep '*r.txt']),1);
Nfilec = size(dir([dname filesep '*c.txt']),1);
if (Nfile == 0 || Nfile ~= Nfilec)
	error('No data files; please run alg01_Gen_File first');
end
clear Nfilec

myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));   % PARALLEL

for i = myFiles
  tic;
    fname = [dname filesep num2str(i)];  disp(fname);  % Create filename.

    % Open files, read data, and close files.
    fidRow=fopen([fname 'r.txt'],'r+'); fidCol=fopen([fname 'c.txt'],'r+'); fidVal =fopen([fname 'v.txt'],'r+');
      rowStr = fread(fidRow,inf,'uint8=>char').';
      colStr = fread(fidCol,inf,'uint8=>char').';
      %valStr = fread(fidVal,inf,'uint8=>char').'; not used
    fclose(fidRow);                     fclose(fidCol);                     fclose(fidVal);

    A = Assoc(rowStr,colStr,1,@sum);             % Construct Adjacency Assoc and sum duplicates.
    save([fname '.A.mat'],'A');                  % Save associative array to file.
    
    % Incidence Assoc construction
    [rowStr, colStr, valStr] = find(num2str(A)); % Get versions with duplicates summed together.
    Nedge = NumStr(rowStr);                      % # of edges.
    edgeBit = ceil(log10(i.*Nedge));             % # of decimal places

    edgeStr = sprintf(['%0.' num2str(edgeBit) 'd,'],((i-1).*Nedge)+(1:Nedge));    % Create identifier for each edge.
    edgeStrMat = Str2mat(edgeStr);
    edgeStrMat(:,1:end-1) = fliplr(edgeStrMat(:,1:end-1));    % Make big endian.
    edgeStr = Mat2str(edgeStrMat);

    outStr = CatStr('Out,','|',rowStr);          % Format out edge string list.
    inStr = CatStr('In,','|',colStr);            % Format in edge string list.

    E = Assoc([edgeStr edgeStr],[outStr inStr],[valStr valStr]); % Create directed incidence Assoc.
    save([fname '.E.mat'],'E');                  % Save incidence Assoc to file.
    
  assocTime = toc;  disp(['Time: ' num2str(assocTime) ', Edges/sec: ' num2str(NumStr(rowStr)./assocTime)]);
end


% cannot measure nnz because only partial view of information
nl = char(10);
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc([tname nl],['tAssoc' nl],[num2str(assocTime) nl]);
infoFunc(Ainfo);




