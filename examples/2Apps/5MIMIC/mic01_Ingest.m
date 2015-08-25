%This script will load the MIMIC note associative arrays,
% do some word cleaning and preprocessing, and ingest them to 
% the Accumulo database specified in DBsetup. 

% If doing parallel ingest, set to 1
PARALLEL=0;

% Data location
inDir = './data';
nl=char(10);

% Run DBsetup to set bindings to database
if(Pid==0 || ~PARALLEL)
    DBsetup;
	Tedge = DB([myName 'Tedge'],[myName 'TedgeT']);
	TedgeDeg = DB([myName 'TedgeDeg']);
	TedgeTxt = DB([myName 'TedgeTxt']);
	
	% Add Summing Combiner to Degree Table to keep track of counts
    TedgeDeg=addColCombiner(TedgeDeg, 'Degree', 'sum');
else
    pause(5);
    DBsetup;
	Tedge = DB([myName 'Tedge'],[myName 'TedgeT']);
	TedgeDeg = DB([myName 'TedgeDeg']);
	TedgeTxt = DB([myName 'TedgeTxt']);
end

% Get list of files to ingest
D=dir([inDir '/*.mat']);
numFiles=numel(D);

% Get indices of files to ingest
if PARALLEL
	myFiles = global_ind(zeros(numFiles,1,map([Np 1],'c',0:Np-1)));
else
	myFiles=1:myFiles;
end

% Iterate through ingest files
for i=myFiles


  fileToLoad=[inDir '/' D(i).name];

  % Ingesting into Tedge, TedgeT, and TedgeDeg
  if(strfind(D(i).name, 'edge'))
     
	 % Load Associative Array
     load(fileToLoad);
     
     % Clean Words
	 Aout=cleanWords(Aout);
     
     put(Tedge, num2str(Aout));
     put(TedgeDeg, putCol(num2str(sum(Aout,1).'), ['Degree' char(10)]));                  
  
  % Ingesting to TedgeTxt                                                                                        
  elseif(strfind(D(i).name, 'txt'))  
	% Load Associative Array
    load(fileToLoad);                                                                     
    put(TedgeTxt,Atxtout);                                                                
                                                                                          
  end                                                                                     
     disp([num2str((i/numFiles)*100) '% done!'])
end