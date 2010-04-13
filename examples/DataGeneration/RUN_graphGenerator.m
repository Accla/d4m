%addpath('../Benchmark/gab2ser');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% SCCA #2 Version 2:  GRAPH ANALYSIS - MAIN PROGRAM 
%
% Main Program for the 2007 Benchmark Challenge Executable Specification
% of DARPA's High Productivity Computing Systems (HPCS) 
% Scalable Synthetic Compact Applications (SCCA) #2 on Graph Analysis.
% Note that this is only one of many possible 'correct' interpretations 
% of its Written Specification.
%
% For its complete list of files and instructions on how to run 
% this program please refer to the accompanying README.txt.
% 
% OBJECTIVE
% An Executable Specification is intended to provide a one-of-many-possible
% proof-of-concept implementation of its Written Specification. Its 
% primary purpose is to demonstrate that its Written Specification can be
% implemented with all of its desired properties.  In addition, the 
% Executable Specification is a tool for Implementers to use in their 
% own verification process. 
%
% This program generates scalable graph data and operates on this data
% through four different kernels, whose operation is timed.  It defines 
% global control and user configurable parameters, invokes the data 
% generator, invokes and times each of the four kernels and their  
% respective verification code (which perform minor code checks and 
% display relevant results).
%
% For a detailed description of the SCCA #2 Graph Analysis, 
% please see the accompanying SCCA #2 Graph Analysis Written Spec. v2.2.
%
% OPTIONS
% The file getUserParameters.m defines the user configurable options.
% 
% REVISION 
% 12-Oct-07   1.0 Release   MIT Lincoln Laboratory.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

declareGlobals;


%--------------------------------------------------------------------------
% Preamble.
%--------------------------------------------------------------------------

% Start overall run time for RUN_graphAnalysis.m (not performance timing).
OverallStartTime = clock;  

format long g % set the terminal dump (float or fixed) to print 15 digits. 
fprintf('\nHPCS SSCA #2 Graph Analysis Executable Specification Release 1.0,');
fprintf('\nbased on Written Spec. v2.2:  Running...\n\n');

% User Interface: Configurable parameters and global program control.
getUserParameters;


%--------------------------------------------------------------------------
% Scalable Data Generator.
%--------------------------------------------------------------------------

fprintf('\nScalable Data Generator - genScalData() beginning execution...\n');

SCALES = [20];
Nfiles = 1;

for SCALE=SCALES
  disp(['SCALE ' num2str(SCALE)]);
  tic;
    % Generate the edge set.
    [E V] = genScalData(SCALE, SUBGR_PATH_LENGTH, K4approx, batchSize);
  toc;

  tic;
    % Save files that don't change.
    endVertexStr = sprintf('%d ',E.EndVertex.');
    weightStr = sprintf('%d ',E.Weight);
    M = numel(E.StartVertex);
    N = 2^SCALE;
%    fid = fopen(['endVertex.' num2str(SCALE) '.txt'],'w');
%    fwrite(fid,endVertexStr);
%    fclose(fid);
%    fid = fopen(['weight.' num2str(SCALE) '.txt'],'w');
%    fwrite(fid, weightStr);
%    fclose(fid);
  toc

  tic
    % Make offset copies of starVertex to increase size of graph.
    for i = 1:Nfiles
      startVertexStr = sprintf('%d ',(E.StartVertex + (i-1)*N).');
%      fid = fopen(['startVertex.' num2str(SCALE) '.' num2str(i) '.txt'],'w');
%      fwrite(fid,startVertexStr);
%      fclose(fid);
    end
  toc
end
