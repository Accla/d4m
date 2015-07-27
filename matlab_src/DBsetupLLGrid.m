function [DB,G] = DBsetupLLGrid(dbname,toolspath)
%DBsetupLLGrid: Create database binding on LLGrid.
%Database internal function.
%  Usage:
%    DB = DBsetupLLGrid(group,toolspath)
%  Inputs:
%    group = String containing name group that database lives in.
%    toolspath = Optional input to the LLGrid tools/ directory.
%       Attempts to autodetect if not provided. Ex: ~/tools
%  Outputs:
%    DB = database binding
  narginchk(1, 2)
  fd = filesep;
  if nargin == 1
      %DBdir = [fileparts(mfilename('fullpath')) fd '..' fd '..'];   % Get tools directory.
      DBdir = '~/../';
  else
      if toolspath(end) == '/' || toolspath(end) == '\'
          toolspath = toolspath(1:end-1);
      end
      DBdir = toolspath;
  end
  fid = fopen([DBdir fd 'groups' fd 'databases' fd dbname fd 'accumulo_user_password.txt']);
  %disp([DBdir fd 'groups' fd 'databases' fd dbname fd 'accumulo_user_password.txt'])
    AccumuloUserKey = fgetl(fid);
  fclose(fid);

  fid = fopen([DBdir fd 'groups' fd 'databases' fd dbname fd 'dnsname']);
    dnsName = fgetl(fid);
  fclose(fid);

  % Create a DB.  
  DB = DBserver([dnsName ':2181'],'Accumulo',dbname,'AccumuloUser',AccumuloUserKey);

  % Graphulo object                                                                                             
  if nargout > 1
    G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo',dbname,[dnsName ':2181'],'AccumuloUser',AccumuloUserKey);
  end
  
return
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
