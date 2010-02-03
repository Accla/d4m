import os
import logging
import unittest
import time
from subprocess import PIPE

from TestUtils import TestUtilsMixin

log = logging.getLogger('test.auto')

class AggregationTest(unittest.TestCase, TestUtilsMixin):
    "Start a clean cloudbase, use an aggregator, verify the data is aggregated"

    order = 25

    settings = {}

    def setUp(self):
        TestUtilsMixin.setUp(self);

    def checkSum(self):
        # check the scan
        code, out, err = self.shell(self.masterHost(),"scan test\n")
        for line in out.split('\n'):
            if line.find('row1') == 0:
                self.assert_(int(row.split()[-1]) == sum(range(10)))
        
    def runTest(self):

        # initialize the database
        aggregator = 'cloudbase.core.aggregation.StringSummation'
        cmd = 'createtable test AGG cf ' + aggregator
        code, out, err = self.rootShell(self.masterHost(),"%s\n" % cmd)

        # insert some rows
        log.info("Starting Test Ingester")
        for i in range(10):
            cmd = 'table test\ninsert row1 cf:col1 %d\n' % i
            code, out, err = self.rootShell(self.masterHost(), cmd)
        self.checkSum()
        self.shutdown_cloudbase()
        self.start_cloudbase()
        self.checkSum()

def suite():
    result = unittest.TestSuite()
    result.addTest(AggregationTest())
    return result
