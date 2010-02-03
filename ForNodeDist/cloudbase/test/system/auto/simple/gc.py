import os
import glob
import logging
import unittest
import sleep
from subprocess import PIPE
import signal

from TestUtils import TestUtilsMixin, CLOUDBASE_HOME, CLOUDBASE_DIR
from simple.readwrite import SunnyDayTest

log = logging.getLogger('test.auto')

class GCTest(SunnyDayTest):

    order = SunnyDayTest.order + 1

    settings = SunnyDayTest.settings.copy()
    settings.update({
        'cloudbase.gc.mapfile.time.delay': 0,
        'cloudbase.gc.mapfile.rounds.delay': 0,
        'cloudbase.tabletserver.maxMapMemory':5000,
        'cloudbase.tabletserver.compactionDelay': 1,
        'cloudbase.tabletserver.maxOpen': 500,
        })
    tableSettings = SunnyDayTest.tableSettings.copy()
    tableSettings['test_ingest'] = { 
    	'cloudbase.tablet.split.threshold': 5000,
        }

    def fileCount(self):
        handle = self.runOn(self.masterHost(),
                            ['hadoop', 'fs', '-lsr', CLOUDBASE_DIR+"/tables"],
                            stdout=PIPE)
        out, err = handle.communicate()
        return len(out.split('\n'))

    def waitForFileCountToStabilize(self):
        count = self.fileCount()
        while True:
            self.sleep(5)
            update = self.fileCount()
            if update == count:
                return count
            count = update

    def runTest(self):
        self.waitForStop(self.ingester, 60)

        count = self.waitForFileCountToStabilize()
        gc = self.runOn(self.masterHost(),
                        [self.cloudbase_sh(), 'gc'],
                        stdout=None,
                        stderr=PIPE)
        self.sleep(5)
        collected = self.fileCount()
        self.assert_(count > collected)

        handle = self.runOn(self.masterHost(),
                            ['grep', '-q', 'root_tablet'] +
                            glob.glob(os.path.join(CLOUDBASE_HOME,'logs/gc_*')))
        out, err = handle.communicate()
        self.assert_(handle.returncode != 0)
        self.pkill(self.masterHost(), 'java.*gc$', signal.SIGHUP)
        self.wait(gc)
        log.info("Verifying Ingestion")
        self.waitForStop(self.verify(self.masterHost(), self.options.rows),
                         10)
        self.shutdown_cloudbase()
        

def suite():
    result = unittest.TestSuite()
    result.addTest(GCTest())
    return result
