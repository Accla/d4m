function Atest = runTESTdir(testDir)
% Atest: associative array containing test results.
% testDir:  path to directory to test.

  pwd0 = pwd;  % Save current directory.

  cd(testDir);   % cd to testDir.

  % Get all files in testDir.
  fnames = dir('./*TEST.m');

  % Initialize result triples.
  AtestRows = '';  AtestCols = '';  AtestVals = '';
%  sep = char(10);
  sep = '|';

  % Loop over all files:
  for i = 1:numel(fnames)

    % Get the file parts.
    [pathstr,testscript,ext] = fileparts(fnames(i).name);


    % Run the script.
    [result time message] =  runTESTfile(testscript);

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

end

function [result time message] = runTESTfile(testscript)

   % Set default results.
   result = 'PASS';
   time = -1.0;
   message = '';

   % Run the script.
   if exist('OCTAVE_VERSION','builtin')
     try
       tic;
         eval(testscript)
       time = toc;
     catch
       result = 'FAIL';
       message = lasterr;
     end
   else
     try
       tic;
         eval(testscript)
       time = toc;
     catch ME
       result = 'FAIL';
       message = ME.message;
     end
  end

end
