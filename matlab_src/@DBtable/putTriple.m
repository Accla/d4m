function T = putTriple(T,varargin)
%PUT inserts triples into DB table.

% Usage patterns:
% T = putTriple(T, r, c, v);
% T = putTriple(T, RC, v); => in this case RC: NxM matrix of M-dimensional data

% check the number of inputs
extraDims = [];
switch nargin
    case 3
        % T = putTriple(T, RC, v)
        % rows of the second input are indices for each dimension of data
        % RC = [dim1 dim2 ... dimN]
        r = varargin{1}(:,1);
        c = varargin{1}(:,2);
        extraDims = varargin{1}(:,3:end);
        v = varargin{2};
    case 4
        % T = putTriple(T, r, c, v)
        % r,c,v are row vectors
        %
        r = varargin{1}; 
        c = varargin{2};
        v = varargin{3};
    otherwise
        error('Unsupported inputs');
end

%  chunkBytes = 20e5;  % 10.5
%  chunkBytes = 10e5;  % 8.9
%  chunkBytes = 5e5;  % 8.5
T=struct(T);
chunkBytes = T.putBytes;    % Set chunk size in chars.

DB = struct(T.DB);

if (ischar(r) && ischar(c) && ischar(v))
    % Get number of bytes.
    rByte = numel(r);   cByte = numel(r);   vByte = numel(v);
    ir = [0 find(r == r(end))];
    ic = [0 find(c == c(end))];
    iv = [0 find(v == v(end))];
    Nr = numel(ir)-1;
    avgBytePerTriple = (rByte + cByte + vByte)/Nr;
    chunkSize = min(max(1,round(chunkBytes/avgBytePerTriple)),Nr);
elseif (isa(r,'double') && isa(c,'double') && isa(v,'double'))
    % Get number of bytes.
    Nr = numel(r);
    avgBytePerTriple = 24;
    chunkSize = min(max(1,round(chunkBytes/avgBytePerTriple)),Nr);
    if strcmp(DB.type, 'scidb')
        % custom chunksize only for SciDB
        chunkSize = Nr; % import all the data we've been given
        % alternatively, parse the schema to figure out the chunk size
        
    end
    %disp(chunkSize);
else
    % handle cases for all other data types
    Nr = NumStr(r);
    rb = whos('r'); cb = whos('c'); vb = whos('v');
    avgBytePerTriple = (rb.bytes+cb.bytes+vb.bytes)/3;
    chunkSize = min(max(1,round(chunkBytes/avgBytePerTriple)),Nr);
end

for i=1:chunkSize:Nr
    %  insert_t = tic;
    if (ischar(r) && ischar(c) && ischar(v))
        i1 = min(i + chunkSize,Nr+1);
        rr = r((ir(i)+1):ir(i1));
        cc = c((ic(i)+1):ic(i1));
        vv = v((iv(i)+1):iv(i1));
    elseif (isa(r,'double') && isa(c,'double') && isa(v,'double'))
        i1 = min(i + chunkSize,Nr);
        rr = r(i:i1);
        cc = c(i:i1);
        vv = v(i:i1);
%         %%%% multidim array %%%%
         if ~isempty(extraDims)
            % only extract the indices to be inserted in this iteration
            extraDimsTrimmed = extraDims(i:i1,:);
        else
            extraDimsTrimmed = [];
        end
    end
    
    if strcmp(DB.type,'Accumulo')
        
        DBinsert(DB.instanceName, DB.host, T.name, DB.user, DB.pass, rr, cc, vv, T.columnfamily, T.security, DB.type);
        
    elseif strcmp(DB.type,'scidb')
        % Use fileattrib to set file permissions to protect?
        tmpfile = tempname;
        [tmp, rand_name] = fileparts(tmpfile);
        
        nl = char(10);  q = '''';
        
        if (ischar(r) && ischar(c) && ischar(v))
            % Make comma the seperator for everything.
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
            
            % Write out file.
            StrFileWrite(msg,tmpfile);
        elseif (isa(r,'double') && isa(c,'double') && isa(v,'double'))
            %msg = transpose([rr cc vv]);
            %[msg, ~] = packageMultiDimArray(rr, cc, vv, extraDims);            
            msg = transpose([rr cc extraDimsTrimmed vv]);
            %disp(msg);            
            fid=fopen(tmpfile,'w');
            fwrite(fid,msg,'double');
            fclose(fid);
        elseif ( ischar(r) && ischar(c) && isa(v, 'double') )
            % handle mixed data types
            % convert r, c, v to doubles - is this the right behavior ??
            rr = double(str2num(r)).';  %#ok<*ST2NM> %str2num will skip delimiters and convert to double array
            r=rr;
            cc = double(str2num(c)).';
            c=cc;
            vv = v(:);
            % rr, cc, vv, must be column vectors here. the structure of msg
            % should be :
            % [row of row indices; row of col indices; ... ; row of data]
            msg = transpose([rr cc vv]);
            fid=fopen(tmpfile,'w');
            fwrite(fid,msg,'double');
            fclose(fid);
        else
            error('Unsupported data type');
            
        end
        
        [tableName, tableSchema] = SplitSciDBstr(T.name);
        
        urlport = DB.host;
        %[stat, sessionID] = system(['wget -q -O - --post-file ' tmpfile  ' ' ...
        %urlport 'new_session" --http-user=' DB.user ' --http-password=' DB.pass]);
        cmd = sprintf('wget -q -O - "%snew_session" --http-user=%s --http-password=%s', urlport, DB.user, DB.pass);
        [stat, sessionID] = system(cmd);
        sessionID = deblank(sessionID);
        
        % check return value from wget
        if stat>0
            % throw an error because wegt did not succeed
            error('Unable to create new SciDB session');
        end
        
        sysCmd = sprintf('curl --digest -u %s:%s -F "file=@%s;filename=%s" %supload_file?id=%s', ...
            DB.user, DB.pass, tmpfile, rand_name, urlport, sessionID);
        
        [status,output] = system(sysCmd);
        
        % check return code and throw error if appropriate
        if status>0
            error('Unable to insert data into table. \nError code 2 : %s', output);
        end
        
        file = strtrim(output);        
        delete(tmpfile);
        
        % 'file' is a reference to the uploaded file on the SciDB server. Now we issue
        % a query to SciDB that loads the data into a table, then redimensions the data
        % into a matrix. This is a typical SciDB load + redimension query:
        ieq = find(tableSchema == '=');
        icom = find(tableSchema == ',');
        dim1 = tableSchema(find(tableSchema == '[')+1:(ieq(1)-1));
        tmp1 = ['<' dim1 ':int64,'];
        dim2 = tableSchema((icom(3)+1):(ieq(2)-1));
        tmp2 = [dim2 ':int64,'];
        tmp3 = tableSchema(2:find(tableSchema == '>'));
        
        if (ischar(r) && ischar(c) && ischar(v) && isempty(extraDims) )
            dimStr = [tmp1 tmp2 tmp3];
            query = ['insert(redimension(input(' dimStr '[d4mtmp_i=0:' num2str(NumStr(vv)-1) ',' ...
                num2str(NumStr(vv)) ',0],' q file q ',0),' tableName '),' tableName ')'];
        elseif (isa(r,'double') && isa(c,'double') && isa(v,'double') && isempty(extraDims) )
            dimStr = ['<' dim1 '_d4mtmp_double:double,' dim2 '_d4mtmp_double:double,' tmp3];
            query = ['insert(redimension(apply(input(' dimStr '[d4mtmp_i=0:' num2str(size(vv,1)-1) ',' ...
                num2str(size(vv,1)) ',0],' q file q ',0,' q '(double,double,double)' q '),' dim1 ...
                ',int64(' dim1 '_double),' dim2 ',int64(' dim2 '_double)),' tableName '),' ...
                tableName ')'];
        elseif (isa(r,'double') && isa(c,'double') && isa(v,'double') && ~isempty(extraDims) )
            % build query for multidimensional data 
            query = buildQuery(tableName, tableSchema, file, numel(vv));
        end
        
        % Actually run the query:
        %urlreadQuery = [urlport 'execute_query?id=' sessionID '&query=' query];
        %[resp, status] = urlread(urlreadQuery);
        [stat, resp] = system(['wget -O - "' urlport 'execute_query?id=' sessionID '&query=' query '" --http-user=' DB.user ' --http-password=' DB.pass]);
        
        if stat>0
            error('Unable to insert data into table. \nError code %d\nCause : %s', stat, resp);
        end
        
        % Release the http session:
        %[resp, status] = urlread(strcat(urlport,'release_session?id=',sessionID));
        [stat, sessionID] = system(['wget -q -O - "' urlport 'release_session?id=' sessionID '" --http-user=' DB.user ' --http-password=' DB.pass]);
        if stat>0
            error('Unable to release SciDB session');
        end
        
        %insert_t = toc(insert_t);  disp(['Insert time: ' num2str(insert_t)]);
    end
end

T=class(T,'DBtable');
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), Dr. Vijay
% Gadepally (vijayg@ll.mit.edu), Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
