function trackNames = D4MqueryGetTrackNames(minLength)
% Returns list of all names in track table (i.e. column labels).
  global D4MqueryGlobal

%  trackNames = Col(D4MqueryGlobal.DbTr(:,:));
  trackNames = (sum(double(logical(D4MqueryGlobal.DbTr(:,:))),1) >= minLength);

end

