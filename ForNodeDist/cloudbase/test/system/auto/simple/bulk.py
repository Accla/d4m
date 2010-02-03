import os
import logging
import unittest
import time
from subprocess import PIPE

from TestUtils import TestUtilsMixin, ROOT, ROOT_PASSWORD

log = logging.getLogger('test.auto')

N = 100000
COUNT = 5

class SimpleBulkTest(unittest.TestCase, TestUtilsMixin):
    "Start a clean cloudbase, make some bulk data and import it"

    order = 25

    def setUp(self):
        TestUtilsMixin.setUp(self);

    def testIngest(self, host, args, **kwargs):
        return self.runJarOn(host,
                             'cloudbase.core.test.TestIngest',
                             args,
                             stdout=PIPE,
                             **kwargs)

    def bulkLoad(self, host):
        handle = self.runJarOn(
            self.masterHost(),
            'cloudbase.core.client.mapreduce.bulk.BulkOperations',
            ['import', self.masterHost(), ROOT, ROOT_PASSWORD,
             'test_ingest', '/testmf', '/testmfFail'],
	    stdout=PIPE)
        self.wait(handle)
        self.assert_(handle.returncode == 0)
        

    def createMapFiles(self):
        args = '-mapFile /testmf/mf%02d -timestamp 1 -size 50 -random 56 1000000 %ld 1'
        log.info('creating map files')
        handles = []
        for i in range(COUNT):
            handles.append(self.testIngest(
                self.hosts[i%len(self.hosts)],
                (args % (i, (N * i))).split()))
        log.info('waiting to finish')
        for h in handles:
            h.communicate()
            self.assert_(h.returncode == 0)
        log.info('done')

    def execute(self, host, cmd, **opts):
        handle = self.runOn(host, cmd, stdin=PIPE, stdout=PIPE, **opts)
        out, err = handle.communicate()
        return handle.returncode, out, err

    def runTest(self):

        # initialize the database
        self.createTable('test_ingest')
        self.execute(self.masterHost(), 'hadoop fs -rmr /testmf'.split())
        self.execute(self.masterHost(), 'hadoop fs -rmr /testmfFail'.split())

        # insert some data
        self.createMapFiles()
        self.bulkLoad(self.masterHost())

        log.info("Verifying Ingestion")
        handles = []
        for i in range(COUNT):
            handles.append(self.verify(self.hosts[i%len(self.hosts)], N, i * N))
        for h in handles:
            out, err = h.communicate()
            self.assert_(h.returncode == 0)

        self.shutdown_cloudbase()

def suite():
    result = unittest.TestSuite()
    result.addTest(SimpleBulkTest())
    return result
