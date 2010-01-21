
% Parse into time stamp and add to A and construct tracks..
%ReutersTracks;


tic;
  AtrackGraphAll = TrackGraph(Atrack);
trackGraphTime = toc; disp(['trackGraphTime = ' num2str(trackGraphTime)]);

% Find tracks of a particular org.
x = 'NE_ORGANIZATION/international monetary fund,';
disp(' ');
disp(['x=' x])
AtrackGraphX = TrackGraph(Atrack(:,Col(A(Row(A(:,x)),'NE_PERSON/*,'))));
AtrackGraphXn = AtrackGraphX ./ AtrackGraphAll;
disp('(AtrackGraphX > 5) & (AtrackGraphXn > 0.5)')
(AtrackGraphX > 5) & (AtrackGraphXn > 0.5)
