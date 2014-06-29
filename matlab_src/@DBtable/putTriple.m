function T = putTriple(T,r,c,v);
%PUT inserts triples into DB table.


%  chunkBytes = 20e5;  % 10.5
%  chunkBytes = 10e5;  % 8.9
%  chunkBytes = 5e5;  % 8.5

  chunkBytes = T.putBytes;    % Set chunk size in chars.

  % Get number of bytes.
  rByte = numel(r);   cByte = numel(r);   vByte = numel(v);
  ir = [0 find(r == r(end))];
  ic = [0 find(c == c(end))];
  iv = [0 find(v == v(end))];
  Nr = numel(ir)-1; 

  avgBytePerTriple = (rByte + cByte + vByte)/Nr;
  chunkSize = min(max(1,round(chunkBytes/avgBytePerTriple)),Nr);

  DB = struct(T.DB);

  for i=1:chunkSize:Nr
%  insert_t = tic;
    i1 = min(i + chunkSize,Nr+1);
    rr = r((ir(i)+1):ir(i1));
    cc = c((ic(i)+1):ic(i1));
    vv = v((iv(i)+1):iv(i1));
    if strcmp(DB.type,'Accumulo')

      DBinsert(DB.instanceName, DB.host, T.name, DB.user, DB.pass, rr, cc, vv, T.columnfamily, T.security, DB.type);

    elseif strcmp(DB.type,'scidb')

      nl = char(10);  q = '''';
      % Make newline the seperator for everything.
%      rr(rr == rr(end)) = nl;
%      cc(cc == cc(end)) = nl;
      rr(rr == rr(end)) = ',';
      cc(cc == cc(end)) = ',';
      vv(vv == vv(end)) = nl;

      % Convert to SciDB text format
%tic;
      % Get locations of seperators and count length of each entry.
      irr = [0 find(rr == rr(end))];
      irrn = diff(irr);
      icc = [0 find(cc == cc(end))];
      iccn = diff(icc);
      ivv = [0 find(vv == vv(end))];
      ivvn = diff(ivv);

      % Allocate concatenation array.
      rrccvv = char(zeros(1,size(rr,2)+size(cc,2)+size(vv,2),'int8'));

      % Compute position of each triple.
      rrccvvn = [0 cumsum(irrn + iccn + ivvn)];

      % Create a filter to insert each triple in concatenated array.
      iFilt = zeros(1,size(rr,2)+size(cc,2)+size(vv,2));
      iFilt(rrccvvn(1:end-1)+1) = 1;
      iFilt(rrccvvn(1:end-1)+irrn+1) = -1;
      rrccvv(cumsum(iFilt) > 0) = rr;
      iFilt(:) = 0;
      iFilt(rrccvvn(1:end-1)+irrn+1) = 1;
      iFilt(rrccvvn(1:end-1)+irrn+iccn+1) = -1;
      rrccvv(cumsum(iFilt) > 0) = cc;
      iFilt(:) = 0;
      iFilt(rrccvvn(1:end-1)+irrn+iccn+1) = 1;
      iFilt(rrccvvn(1:end-1)+irrn+iccn+ivvn+1) = -1;
      rrccvv(cumsum(iFilt) > 0) = vv;

      % Add final formatting bits. 
      msg = ['{0}[(' strrep(rrccvv,nl,'),(')];
      msg = [msg(1:end-2) ']'];
%toc
      %msg = ['{0}[(' strrep(CatStr(CatStr(rr,',',cc),',',vv),nl,'),(')];

      % Write our 1-d SciDB text format file to SciDB:
      % Put tmp file in our home directory
      home_dir = getenv('HOME');
%      home_dir = '/tmp';    % Use fileattrib to set file permissions to protect?
      [tmp rand_name] = fileparts(tempname);
      tmpfile = [home_dir '/' rand_name];
	
      StrFileWrite(msg,tmpfile);

      [tableName tableSchema] = SplitSciDBstr(T.name);
      urlport = DB.host;
      [sessionID,success]=urlread([urlport 'new_session']);
      sessionID = deblank(sessionID);

      sysCmd = ['curl -F "file=@' tmpfile ';filename=' rand_name '" ' urlport 'upload_file?id=' sessionID];

      [status,output] = system(sysCmd);

      file = strtrim(output);
      delete(tmpfile);
      % 'file' is a reference to the uploaded file on the SciDB server. Now we issue
      % a query to SciDB that loads the data into a table, then redimensions the data

      % into a matrix. This is a typical SciDB load + redimension query:
      ieq = find(tableSchema == '=');
      icom = find(tableSchema == ',');
      tmp1 = ['<' tableSchema(find(tableSchema == '[')+1:(ieq(1)-1)) ':int64,'];
      tmp2 = [tableSchema((icom(3)+1):(ieq(2)-1)) ':int64,'];
      tmp3 = [tableSchema(2:find(tableSchema == '>'))];
      dimStr = [tmp1 tmp2 tmp3];

%      query = ['insert(redimension(input(' dimStr '[i=0:*,' num2str(NumStr(vv)) ',0],' q file q ',0),' tableName '),' tableName ')'];
      query = ['insert(redimension(input(' dimStr '[i=0:' num2str(NumStr(vv)-1) ',' num2str(NumStr(vv)) ',0],' q file q ',0),' tableName '),' tableName ')'];

      % Actually run the query:
      urlreadQuery = [urlport 'execute_query?id=' sessionID '&query=' query]
      [resp, status] = urlread(urlreadQuery);

      % Release the http session:
      [resp, status] = urlread(strcat(urlport,'release_session?id=',sessionID));

    %  insert_t = toc(insert_t);  disp(['Insert time: ' num2str(insert_t)]);
    end
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
