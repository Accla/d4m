function trackNames = D4MqueryGetTrackNames()
% Returns list of all names in track table (i.e. column labels).
  global D4MqueryGlobal

  trackNames = Col(D4MqueryGlobal.DbTr(:,:));

end

