import os
import logging
import unittest
import time

from TestUtils import CLOUDBASE_DIR
from subprocess import PIPE

from simple.split import TabletShouldSplit

log = logging.getLogger('test.auto')

class MetaSplitTest(TabletShouldSplit):

    order = TabletShouldSplit.order + 1
    
    tableSettings = TabletShouldSplit.tableSettings.copy()
    tableSettings['!METADATA'] = { 
    	'cloudbase.tablet.split.threshold': 10000,
        }
    tableSettings['test_ingest'] = { 
    	'cloudbase.tablet.split.threshold': 50000,
        }

    def runTest(self):
        TabletShouldSplit.runTest(self)
        handle = self.runOn(self.masterHost(), [
            'hadoop', 'fs', '-ls', os.path.join(CLOUDBASE_DIR,'tables','!METADATA')
            ], stdout=PIPE)
        out, err = handle.communicate()
        self.assert_(len(out.split('\n')) > 3)

        # verify start-up, and assignment of metadata tablets
        self.start_cloudbase()
        self.waitForStop(self.verify(self.masterHost(), self.options.rows), 120)
        self.shutdown_cloudbase()

def suite():
    result = unittest.TestSuite()
    result.addTest(MetaSplitTest())
    return result
