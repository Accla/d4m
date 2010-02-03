import os
import logging
import unittest
import time
from subprocess import PIPE

from TestUtils import TestUtilsMixin

log = logging.getLogger('test.auto')

import readwrite

class DeleteTest(readwrite.SunnyDayTest):
    "Injest some data, delete it, re-injest it, look for it"

    order = 25

    def setUp(self):
        TestUtilsMixin.setUp(self);
        
        # initialize the database
        self.createTable('test_ingest')
        
        # start test ingestion
        log.info("Starting Test Ingester")
        self.ingester = self.ingest(self.masterHost(),
                                    self.options.rows,
                                    timestamp=time.time() * 1000)

    def runTest(self):

        self.waitForStop(self.ingester, 200)

        log.info("Deleting data")
        self.waitForStop(self.runJarOn(self.masterHost(), "cloudbase.core.test.TestRandomDeletes", [], stdout=None),
                         200)

        log.info("Inserting data")
        now = time.time() * 1000
        self.waitForStop(self.ingest(self.masterHost(),
                                     self.options.rows,
                                     timestamp=now),
                         60)

        log.info("Verifying Ingestion")
        self.waitForStop(self.verify(self.masterHost(),
                                     self.options.rows,
                                     timestamp=now),
                         60)
        self.shutdown_cloudbase()
        

def suite():
    result = unittest.TestSuite()
    result.addTest(DeleteTest())
    return result
