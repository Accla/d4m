function [E V] = genRmatData(SCALE);
%genRmatData: Generates a graph using the R-MAT generator.
%IO user function.
%  Usage:
%    [E V] = genRmatData(SCALE)
%  Inputs:
%    SCALE = Approximate number of vertices in graph is N = 2^SCALE, approximate number of edges is M = 8*N
%  Outputs:
%    E.StartVertex = 1xM int array of start vertices
%    E.EndVertex = 1xM int array of end vertices
%    E.Weight = 1xM int array of integer weights
%    V.N = total number of vertices
%    V.M = total number of edges
%    V.C = maximum value of any of the weights.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% This scalable data generator produces edges containing the start
% vertex, end vertex, and weight for each directed edge, that represent
% data assigned to the edges of a graph. The edge weights are positive
% integers chosen from a uniform random distribution. 
% The graph generator is based on the Recursive MATrix (R-MAT) power-law
% graph generation algorithm (Chakrabati, Zhan & Faloutsos). This model
% recursively sub-divides the graph into four equal-sized partitions and
% distributes edges within these partitions with unequal probabilities.
% Initially, the adjacency matrix is empty, and edges are added one at a
% time. Each edge chooses one of the four partitions with probabilities
% a, b, c, and d respectively. At each stage of the recursion, the
% parameters are varied slightly and renormalized. It is possible that the
% R-MAT algorithm may create a very small number of multiple edges between
% two vertices, and even self loops.
% The algorithm also generates the data tuples with high
% degrees of locality. Thus, as a final step, vertex numbers must be
% randomly permuted, and then edges are randomly shuffled, before being
% presented to subsequent kernels.
% For a detailed description of the SCCA #2 Scalable Data Generator,
% please see the SCCA #2 Graph Analysis Written Specification v2.2.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Calcutate the maximum integer value in any edge weight...
C = 2^SCALE;


%--------------------------------------------------------------------------
%  Generate the tuple list.
%--------------------------------------------------------------------------

%if (ENABLE_DATA_TYPE == RMAT)  % If random recursive MATrix (R-MAT) power-law
    % graph data was opted...

    %----------------------------------------------------------------------
    %  From the selected SCALE, derive the problem's maximum size.
    %----------------------------------------------------------------------

    % Calculate the maximum number of vertices.
    maxN = 2^SCALE;     % maximum number of vertices in the graph.

    % Calculate the maximum number of edges; adjust at low SCALE values to
    % keep the resultant graph's sparsity below 0.25 (since for the
    % intended ranges of M and N, the sparsity of the graph will be much
    % less than 1).
    maxM = 8 * maxN;    % maximum number of edges in the graph.
    lookupM = [0.25, 0.5, 1, 2, 4];
    if (SCALE < 6)
        maxM = lookupM(SCALE) * maxN;
    end

    %----------------------------------------------------------------------
    %  R-MAT power law distributed
    %----------------------------------------------------------------------

    % Set the R-MAT probabilities. Create a single parameter family.
    % Parameters cannot be symmetric in order for it to be a power law
    % distribution.  (These probabilites will add to '1.0').
    p = 0.6;
    a = p;   b = (1 - a)/3;   c = b;   d = b;

    a = 0.55; b = 0.1; c = 0.1; d = 0.25;

    % Create index arrays.
    ii = ones(maxM, 1);    % start vertices
    jj = ones(maxM, 1);    % end vertices

    % Loop over each order of bit.
    ab = a + b;
    c_norm = c/(c + d);
    a_norm = a/(a + b);

    for ib = 1:SCALE

        % Compare with probabilities, and set bits of indices.
        ii_bit = rand(maxM, 1) > ab;
        jj_bit = rand(maxM, 1) > ( c_norm .* ii_bit + a_norm .* not(ii_bit) );

        ii = ii + (2^(ib - 1)) .* ii_bit;
        jj = jj + (2^(ib - 1)) .* jj_bit;

    end

    E.Weight = unidrnd(C, 1, maxM)';  % Compute random integer weights.


%end % of if ENABLE_DATA_TYPE == any torus


%if (ENABLE_RANDOM_VERTICES == 1)

    % Randomly permute the vertex numbers.
    randVertices = randperm(maxN);
    V.invRandVert(randVertices) = 1:maxN; % Save its inverse for verif.
    ii = randVertices(ii);
    jj = randVertices(jj);

%end

% Build the returned edge tuple data structure.
E.StartVertex = ii;
E.EndVertex   = jj;



%--------------------------------------------------------------------------
%  Measure the actual generated data's size.
%--------------------------------------------------------------------------

% Measure the actual sizes of the resulting data.
actualN = max( max(E.StartVertex), max(E.EndVertex) );
actualM = length(E.StartVertex);

% Count how many vertex pairs are diagonals.
% [ignore cnt] = find(E.StartVertex == E.EndVertex);
% numDiagonals = sum(cnt);

% Also build a struct to be returned with other relevant info for verif.
V.N = actualN;  % total number of vertices in the tuple set.
V.M = actualM;  % total number of edges in the tuple set.
V.C = C;        % maximum integer weight value in the tuple set.

% Compute what will be the eventual graph's sparsity.
graphSparsity = actualM/actualN^2;


%--------------------------------------------------------------------------
%  Echo the current problem's parameters.
%--------------------------------------------------------------------------

% Echo the user's selections on the edge tuple set and on what will be
% the eventual graph:

fprintf([ ...
    '\n\n GRAPH PROBLEM SIZE:  \n\tSCALE == %d,', ...
    '\n\tN == %d (actual vertices), M == %d (actual edges),', ...
    '\n\tgraph''s actual sparsity == %.2f, C == %d (max int weight),'], ...
    SCALE, actualN, actualM, graphSparsity, C);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Ms. Theresa Meuse,
%   Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
