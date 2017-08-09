% Run this file after running alg02_Jaccard04_plot and alg03_kTrussAdj04_Plot
util_Require('KTR JAC');

JAC.linespec = '-';
KTR.linespec = '--';

figure;
legendarr = {};
legendarri = 1;

structs = {JAC, KTR};
lbls = {'Jaccard','3-Truss'};
for idx = 1:length(structs);
d = structs{idx};
for ntidx = 1:numel(d.nt)
    nt = d.nt(ntidx);
    legendarr{legendarri} = [lbls{idx} ' ' num2str(nt) ' Tablet'];
    legendarri = legendarri + 1;
    % sort
    [d.scale{ntidx},sortidx] = sort(d.scale{ntidx});
    d.rate{ntidx} = d.rate{ntidx}(sortidx);
    % plot
    x = d.scale{ntidx};
    y = d.rate{ntidx};
    plot(x, y, d.linespec, 'LineWidth', 0.5+(nt-1)*1);
    hold on;
end
end

hold off;
xlabel('SCALE');
ylabel('Rate (partial products per sec.)');
title('Graphulo Rate Scaling');
axis([-inf,+inf,0,+inf])
legend(legendarr, 'Location', 'southeast');
fileName = ['img/GraphuloRate-' timeSaveStr];
savefig(fileName);
%print('JaccardTime','-depsc')
print(fileName,'-dpng')


