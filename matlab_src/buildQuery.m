% BUILDQUERY Builds data insert query for SciDB
function query = buildQuery(tableName, tableSchema, dataFileName, numDataPoints)

% ASSUMPTIONS: 
% (1) All data is double (i.e. importing from binary)
% (2) Only single value being imported

% sample query
% insert(
%   redimension(
%          apply(
%           input(
%                   <row_double:double,col_double:double,slice_double:double,val:double>
%                   [i=0:15,16,0],
%                   '/tmp/slice1-3dims',
%                   0,
%                   '(double,double,double,double)'),
%                 row, int64(row_double),
%                 col, int64(col_double),
%                 slice,int64(slice_double)
%                ),
%             m3),
%       m3);

dbinstances = -1; % load using all instances

% for now, assuming that only value is being inserted per call (i.e. data
% has only attribute). % this may break if schema is <val1:double, val2:double>

% sample schema : <val:double> [row=1:4,1,0,col=1:4,1,0,slice=1:4,1,0]

% first determine the data type of the value being inserted
% this is the part between <>
pattern = '<\S*>';
y = regexp(tableSchema, pattern, 'match'); % regexp will return cell array
y = y{1};
y = strsplit(y(2:end-1), {':',','});
% this is the data to be insterted:
valueNames = y(1:2:end);
valueAttribs = y(2:2:end);

% determine dimension names and types
pattern = '\[\S.*\]';
y = regexp(tableSchema, pattern, 'match'); % regexp will return cell array
y = y{1}(2:end-1); % get rid of '[' and ']'
y = strsplit(y, {'=',','});
dimNames = y(1:4:end);
y = y(2:4:end);
y = cellfun(@(IN) strsplit(IN, ':'), y, 'UniformOutput', false);
dimStart = y{1}{1}; % just to check whether the array starts with 1 or 0

allNames = cat(1, dimNames(:), valueNames(:));
fmtStr = repmat('double,', 1, length(allNames)); fmtStr = fmtStr(1:end-1); % remove extra ',' at the end
dimConversionStr = cellfun(@(IN) sprintf('%s, int64(%s_tmp),', IN, IN), dimNames, 'UniformOutput', false);
dimConversionStr = [dimConversionStr{:}];
dataConversionStr = cellfun(@(IN1,IN2) sprintf('%s, %s(%s_tmp),', IN1, IN2, IN1), valueNames, valueAttribs, 'UniformOutput', false);
dataConversionStr = [dataConversionStr{:}];
dataConversionStr = dataConversionStr(1:end-1); % remove extra ',' at the end

query = sprintf('%s_tmp:double,', allNames{:});

%query = sprintf('insert( redimension( apply( input( <%s> [i=%d:%d,%d,0], ''%s'', -2, ''(%s)''), %s %s), %s), %s)', ...
%    query(1:end-1), dbinstances, numDataPoints-1, numDataPoints, dataFileName, fmtStr, dimConversionStr, dataConversionStr, tableName, tableName);

query = sprintf('insert( redimension( apply( input( <%s> [d4m_tmp_i=%s:*,%d,0], ''%s'', -2, ''(%s)''), %s %s), %s), %s)', ...
    query(1:end-1), dimStart, 1000000, dataFileName, fmtStr, dimConversionStr, dataConversionStr, tableName, tableName);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), 
% Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

