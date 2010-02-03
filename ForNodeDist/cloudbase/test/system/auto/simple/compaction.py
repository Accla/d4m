import os
import logging
import unittest
from simple.bulk import SimpleBulkTest
from subprocess import PIPE

N = 100000
COUNT = 5

log = logging.getLogger('test.auto')

class CompactionTest(SimpleBulkTest):
    "Start a clean cloudbase, bulk import a lot of map files, read while a multi-pass compactions is happening"

    order = 26

    tableSettings = SimpleBulkTest.tableSettings.copy()
    tableSettings['test_ingest'] = { 
    	'cloudbase.tablet.majorCompaction.keep': 0
        }
    settings = SimpleBulkTest.settings.copy()
    settings.update({
        'cloudbase.tabletserver.majorCompaction.maxOpen':4,
        'cloudbase.tabletserver.compactionDelay': 1,
        'cloudbase.tabletserver.maxOpen': 100
        })

    def createMapFiles(self, host):
        handle = self.runJarOn(
            self.masterHost(),
            'cloudbase.core.test.TestIngest$CreateMapFiles',
            "testmf 4 0 500000 59".split(),
	    stdout=PIPE)
        out, err = handle.communicate()
        self.assert_(handle.returncode == 0)

    def runTest(self):

        # initialize the database
        self.createTable('test_ingest')
        self.execute(self.masterHost(), 'hadoop fs -rmr /testmf'.split())
        self.execute(self.masterHost(), 'hadoop fs -rmr /testmfFail'.split())

        # insert some data
        self.createMapFiles(self.masterHost())
        self.bulkLoad(self.masterHost())

        code, out, err = self.shell(self.masterHost(), "table !METADATA\nscan\n")
        self.assert_(code == 0)

        beforeCount = len(out.split('\n'))

        log.info("Verifying Ingestion")
        for c in range(5):
            handles = []
            for i in range(COUNT):
                handles.append(self.verify(self.hosts[i%len(self.hosts)], N, i * N))
            for h in handles:
                h.communicate()
                self.assert_(h.returncode == 0)

        code, out, err = self.shell(self.masterHost(), "table !METADATA\nscan\n")
        self.assert_(code == 0)

        afterCount = len(out.split('\n'))

        self.assert_(afterCount < beforeCount)

        self.shutdown_cloudbase()

def suite():
    result = unittest.TestSuite()
    result.addTest(CompactionTest())
    return result
