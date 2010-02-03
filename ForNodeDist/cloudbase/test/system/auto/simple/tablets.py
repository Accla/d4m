import os
import logging
import unittest
import time
from subprocess import PIPE

from TestUtils import TestUtilsMixin

log = logging.getLogger('test.auto')

N = 10000    # rows to insert, 100 rows per tablet
WAIT = (N / 1000. + 1) * 60

class LotsOfTablets(TestUtilsMixin, unittest.TestCase):

    order = 80

    settings = TestUtilsMixin.settings.copy()
    settings.update({
    	'cloudbase.tablet.split.threshold':200,
        'cloudbase.tabletserver.maxMapMemory':123456789
        })

    def runTest(self):

        # initialize the database
        handle = self.runJarOn(self.masterHost(), 
		               'cloudbase.core.test.CreateTestTable', 
		               [str(N)])
	self.waitForStop(handle, WAIT)
	self.shutdown_cloudbase()
	self.start_cloudbase()
        handle = self.runJarOn(self.masterHost(), 
		               'cloudbase.core.test.CreateTestTable', 
		               ['-readOnly', str(N)])
        self.waitForStop(handle, WAIT)

def suite():
    result = unittest.TestSuite()
    result.addTest(LotsOfTablets())
    return result
