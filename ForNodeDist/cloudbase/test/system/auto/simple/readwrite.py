import os
import logging
import unittest
import time
from subprocess import PIPE

from TestUtils import TestUtilsMixin

log = logging.getLogger('test.auto')

class SunnyDayTest(unittest.TestCase, TestUtilsMixin):
    "Start a clean cloudbase, ingest some data, verify it was stored properly"

    order = 20

    settings = {}

    def add_options(self, parser):
        if not parser.has_option('-c'):
            parser.add_option('-c', '--rows', dest='rows',
                              default=20000, type=int,
                              help="The number of rows to write "
                              "when testing (%default)")
        if not parser.has_option('-n'):
            parser.add_option('-n', '--size', dest='size',
                              default=50, type=int,
                              help="The size of values to write "
                              "when testing (%default)")

    def setUp(self):
        TestUtilsMixin.setUp(self);
        
        # initialize the database
        self.createTable('test_ingest')
        
        # start test ingestion
        log.info("Starting Test Ingester")
        self.ingester = self.ingest(self.masterHost(),
                                    self.options.rows,
                                    size=self.options.size)

    def tearDown(self):
        TestUtilsMixin.tearDown(self)
        self.pkill(self.masterHost(), 'TestIngest')

    def waitTime(self):
        return 1000*120 * self.options.rows / 1e6 + 30

    def runTest(self):
        waitTime = self.waitTime()

        self.waitForStop(self.ingester, waitTime)

        log.info("Verifying Ingestion")
        self.waitForStop(self.verify(self.masterHost(),
                                     self.options.rows,
                                     size=self.options.size),
                         waitTime)

        log.info("Hitting the web pages")
        import urllib2
        handle = urllib2.urlopen('http://%s:50099/monitor' % self.masterHost())
        self.assert_(len(handle.read()) > 100)
        
        self.shutdown_cloudbase()

class LargeTest(SunnyDayTest):

    def ingest(self, host, count, start=0, timestamp=None, size=50, **kwargs):
        return SunnyDayTest.ingest(self, host, count / 1000, start, timestamp, size * 100000, **kwargs)
    
    def verify(self, host, count, start=0, size=50, timestamp=None):
        return SunnyDayTest.verify(self, host, count / 1000, start, size * 100000, timestamp)


class Interleaved(SunnyDayTest):

    def setUp(self):
        TestUtilsMixin.setUp(self);
        
        # initialize the database
        self.createTable('test_ingest')

    def tearDown(self):
        TestUtilsMixin.tearDown(self)
        self.pkill(self.masterHost(), 'TestIngest')


    def runTest(self):
        waitTime = self.waitTime()

        N = self.options.rows
        ingester = self.ingest(self.masterHost(), N, 0)
        for i in range(0, 10*N, N):
            self.waitForStop(ingester, waitTime)
            verifier = self.verify(self.hosts[-1], N, i)
            ingester = self.ingest(self.masterHost(), N, i + N)
            self.waitForStop(verifier, waitTime)
        verifier = self.verify(self.hosts[-1], N, i)
        self.waitForStop(verifier, waitTime)

        self.shutdown_cloudbase()


def suite():
    result = unittest.TestSuite()
    result.addTest(SunnyDayTest())
    result.addTest(Interleaved())
    result.addTest(LargeTest())
    return result
