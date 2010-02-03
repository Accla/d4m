import os
import unittest
import time
from subprocess import PIPE

from TestUtils import TestUtilsMixin

testClass = "cloudbase.core.test.ScanRangeTest"

class RangeTest(unittest.TestCase, TestUtilsMixin):
    "Test scanning different ranges in cloudbase"

    order = 21

    def setUp(self):
        TestUtilsMixin.setUp(self);
        
    def test(self, *args):
        handle = self.runJarOn(self.masterHost(), testClass, list(args),
                               stdout=PIPE)
        self.waitForStop(handle, 120)
        
    def tearDown(self):
        TestUtilsMixin.tearDown(self)

    def runTest(self):
        self.test(self.masterHost(),"rt")
        self.shutdown_cloudbase()

def suite():
    result = unittest.TestSuite()
    result.addTest(RangeTest())
    return result
