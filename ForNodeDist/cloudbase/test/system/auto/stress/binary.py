import unittest
import os

from TestUtils import CLOUDBASE_DIR
from simple.binary import BinaryTest
from subprocess import PIPE

class BinaryStressTest(BinaryTest) :
    order = 80

    tableSettings = BinaryTest.tableSettings.copy()
    tableSettings['bt'] = { 
    	'cloudbase.tablet.split.threshold': 1000,
        }
    settings = BinaryTest.settings.copy()
    settings.update({
        'cloudbase.tabletserver.maxMapMemory':5000,
        'cloudbase.tabletserver.compactionDelay': 1,
        'cloudbase.tabletserver.maxOpen': 500,
        })

    def runTest(self):
        BinaryTest.runTest(self)
        handle = self.runOn(self.masterHost(), [
            'hadoop', 'fs', '-ls', os.path.join(CLOUDBASE_DIR,'tables','bt')
            ], stdout=PIPE)
        out, err = handle.communicate()
        self.assert_(len(out.split('\n')) > 7)

def suite():
    result = unittest.TestSuite()
    result.addTest(BinaryStressTest())
    return result
