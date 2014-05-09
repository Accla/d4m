function Assoc2KML(A, varargin)
%writeKMLFile: Writes a KML File with Associative Array input
%User Function
%  Usage:
%    writeKMLFile(A)
%    writeKMLFile(A, 'outfile', filename)
%    writeKMLFile(A, 'outfile', filename, 'showtime', 'true')
%  Inputs:
%    A = Associative Array with latlon and text information
%    filename = desired output file name ending with .kml
%    showtime = true if sorted results are desired, false otherwise
%  Outputs:
%    filename
%  Examples:
%    writeKMLFile(A, 'outfile', 'demotest.kml', 'showtime', 'true');


rowKeyList = deblank(Str2mat(Row(A)));

if(numel(varargin)>0)
    showtime=1;
    if(strcmp(varargin{1}, 'outfile'))
        outfile = varargin{2};
        if(numel(varargin)>2)
            showtime = varargin{4};
        end
        
    elseif strcmp(varargin{1}, 'showtime')
        showtime = varargin{2};
        if(numel(varargin)>2)
            outfile = varargin{4};
        end
    else
        fprintf(2, 'error: invalid flags');
    end
else
    showtime=1;
    outfile = 'kmltestfile.kml';
end

fid = fopen(outfile, 'w+');

if showtime
    rowKeyList = sortrows(fliplr(rowKeyList));
    rowKeyList = fliplr(rowKeyList);
end

fprintf(fid, '<?xml version="1.0" encoding="utf-8"?>\n');
fprintf(fid, '<kml xmlns="http://www.opengis.net/kml/2.2">\n');
fprintf(fid, '   <Document>\n');
fprintf(fid, '      <name>%s</name>\n', outfile);
fprintf(fid, '    <Style id="placemark_circle">\n');
fprintf(fid, '      <IconStyle>\n');
fprintf(fid, '        <Icon>\n');
fprintf(fid, '          <href>https://abs.twimg.com/a/1375391966/images/resources/twitter-bird-light-bgs.png</href>\n');
fprintf(fid, '        </Icon>\n');
fprintf(fid, '        <hotSpot x="0" y=".5" xunits="fraction" yunits="fraction"/>\n');
fprintf(fid, '      </IconStyle>\n');
fprintf(fid, '    </Style>\n');

for i = 1:size(rowKeyList,1)
    rowKey = rowKeyList(i,:);

    [tmp usertmp tmp] = A(strcat(rowKey, ','), StartsWith('user|,'));
    [tmp timetmp tmp] = A(strcat(rowKey, ','), StartsWith('time|,'));
    [tmp latlontmp tmp] = A(strcat(rowKey, ','), StartsWith('latlon|,'));
    [tmp tmp tweettext] = A(strcat(rowKey, ','), 'text,');
        
    if(~isempty(latlontmp) && ~isempty(timetmp) && ~isempty(usertmp) && ~isempty(tweettext))
        
        [tmp, latlon] = SplitStr(latlontmp, '|');
        [tmp, time] = SplitStr(timetmp, '|');
        [tmp, user] = SplitStr(usertmp, '|');
        
        tweettext = deblank(tweettext(double(tweettext)~=26));
        user = deblank(user(double(user)~=26));
        lat = latlon(1:2:end);
        lon = latlon(2:2:end);
        timenum = datenum(datevec(time));
        time = datestr(timenum-.2083, 'yyyy-mm-ddTHH:MM:SSZ');
        
    else
        continue;
    end
    
    fprintf(fid, '      <Placemark>\n');
    
    fprintf(fid, '         <Snippet maxLines="0"> </Snippet>\n');
    
    fprintf(fid, '         <description>%s \n%s</description>\n', tweettext, time);
    
    fprintf(fid, '         <name>@%s</name>\n', user);
    
    fprintf(fid, '         <TimeStamp>\n');
    
    fprintf(fid, '            <when>%s</when>\n', time);
    
    fprintf(fid, '         </TimeStamp>\n');
    
    fprintf(fid, '      <styleUrl>#placemark_circle</styleUrl>\n');
    
    fprintf(fid, '         <Point>\n');
    
    fprintf(fid, '            <coordinates>%f,%f,0</coordinates>\n', str2num(lon), str2num(lat));
    
    fprintf(fid, '         </Point>\n');
    
    fprintf(fid, '      </Placemark>\n');
    
end

fprintf(fid, '   </Document>\n');
fprintf(fid, '</kml>');
fclose(fid);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Software Engineer: Dr. Vijay Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%+-003850..35676710951385000000
