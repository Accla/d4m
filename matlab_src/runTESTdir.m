function Atest = runTESTdir(testDir)
%runTESTdir: Runs every *TEST.m script in a directory.
%Associative array and database utility function.
%  Usage:
%    Atest = runTESTdir(testDir)
%  Inputs:
%    testDir = directory containing TEST.m script files
%  Outputs:
%    Atest = associative array containing results of run the scripts

% Atest: associative array containing test results.
% testDir:  path to directory to test.

  pwd0 = pwd;  % Save current directory.

  cd(testDir);   % cd to testDir.

  % Get all files in testDir.
  fnames = dir('./*TEST.m');
  if ispc       % On Windows OS: TEST is matched case-insensitive. 
      truematch = true(numel(fnames),1);    % We only want TEST, not Test.
      for i = 1:numel(fnames)
          if ~strcmp(fnames(i).name(end-5:end),'TEST.m')
              truematch(i) = false;
          end
      end
      fnames = fnames(truematch);
  end

  % Initialize result triples.
  AtestRows = '';  AtestCols = '';  AtestVals = '';
%  sep = char(10);
  sep = '|';

  % Loop over all files:
  for i = 1:numel(fnames)

    % Get the file parts.
    [pathstr,testscript,ext] = fileparts(fnames(i).name);
%    display([' ***************************************** ']);
%    display([' **** RUN TEST ' testscript '  **** ']);
    % Run the script.
    [result time message] =  runTESTfile(testscript);

    % Check for output differences.
    if ( strcmp(result,'PASS') )
      matfile = [testscript '.mat'];
      matfilefid = fopen(matfile,'r');  % Check for existence.
      if ( matfilefid > 0)
        goldfile = [testscript '-GOLD.mat'];
        if exist('OCTAVE_VERSION','builtin')
          goldfile = [testscript '-GOLDoct.mat'];
        end
        % Check for existence.
        goldfilefid = fopen(goldfile,'r');
        if ( goldfilefid > 0)   % Read in both files and compare.
          matdata = fread(matfilefid,inf,'uint8');
          fclose(matfilefid);
          golddata = fread(goldfilefid,inf,'uint8');
          fclose(goldfilefid);
          if ( numel(golddata) == numel(matdata) )
            if ( max(abs(golddata(100:end) - matdata(100:end))) )
              result = 'DIFF';
            end
          else
            result = 'DIFF';
          end
        else
          % Move data to gold file.
          fclose(matfilefid);
          movefile(matfile,goldfile);
        end
      end
    end

    % Append to list.
    AtestRows = [AtestRows testscript sep];
    AtestCols = [AtestCols 'result' sep];
    AtestVals = [AtestVals result sep];

    AtestRows = [AtestRows testscript sep];
    AtestCols = [AtestCols 'time' sep];
    AtestVals = [AtestVals num2str(time) sep];

    if not(isempty(message))
      AtestRows = [AtestRows testscript sep];
      AtestCols = [AtestCols 'message' sep];
      AtestVals = [AtestVals message sep];
    end

  end

  % Put into assoc array.
  Atest = Assoc(AtestRows,AtestCols,AtestVals);

  cd(pwd0);   % cd to original directory.
  pause(0.5);
end

function [result time message] = runTESTfile(testscript)

   % Set default results.
   result = 'PASS';
   time = -1.0;
   message = '';

   % Run the script.
   try
     tic;
       evalin('base', testscript)
     time = toc;
   catch
     result = 'FAIL';
     message = lasterr;
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

