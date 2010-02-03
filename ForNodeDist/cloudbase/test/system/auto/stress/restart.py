from simple.readwrite import SunnyDayTest
import unittest
import logging
import os
from TestUtils import TestUtilsMixin, CLOUDBASE_HOME

log = logging.getLogger('test.auto')

class RestartTest(SunnyDayTest):
    order = 80

    settings = TestUtilsMixin.settings

class RestartMasterTest(RestartTest):


    def runTest(self):

        self.sleep(3)
        log.info("Stopping master server")
        self.stop_master(self.masterHost())
        self.sleep(1)
        log.info("Starting master server")
        self.start_master(self.masterHost())

        self.waitForStop(self.ingester, 30)
        self.waitForStop(self.verify(self.masterHost(), self.options.rows), 60)
        

class KilledTabletServerTest(RestartTest):

    def flushAll(self):
        self.flush('test_ingest')
        self.flush('!METADATA')
        self.flush('!METADATA')

    def startRead(self):
        return self.verify(self.masterHost(), self.options.rows)

    def stopRead(self, handle, timeout):
        self.waitForStop(handle, timeout)

    def readRows(self):
        self.stopRead(self.startRead(), 120)

    def runTest(self):

        self.waitForStop(self.ingester, 60)
        log.info("Ingester stopped")
        log.info("starting scan")
        self.readRows()
        for host in self.hosts:
            log.info("Restarting Tablet server on %s", host)
            self.flushAll()
            self.stop_tserver(host)
            self.start_tserver(host)
            log.info("Tablet server on %s started", host)
            log.info("starting scan")
            self.readRows()

class KilledTabletDuringScan(KilledTabletServerTest):
    "Kill a tablet server while we are scanning a table"

    def runTest(self):

        self.waitForStop(self.ingester, 30)
        log.info("Ingester stopped")
        # force the data out to disk
        self.shutdown_cloudbase()
        self.start_cloudbase()
        log.info("starting scan")
        handle = self.startRead()

        for host in self.hosts:
            log.info("Restarting Tablet server on %s", host)
            self.stop_tserver(host)
            self.start_tserver(host)
            log.info("Tablet server on %s started", host)
            self.sleep(5)
            log.info("starting scan")
            self.stopRead(handle, 60)
            if host != self.hosts[-1]:
                handle = self.startRead()

class KilledTabletDuringShutdown(KilledTabletServerTest):

    def runTest(self):
        self.flushAll()
        self.waitForStop(self.ingester, 30)
        log.info("Ingester stopped")
        self.stop_tserver(self.hosts[0])
        log.info("This can take a couple minutes")
        self.shutdown_cloudbase()


from simple.split import TabletShouldSplit

class ShutdownSplitter(TabletShouldSplit):
    "Shutdown while compacting, splitting, and migrating"

    tableSettings = TabletShouldSplit.tableSettings.copy()
    tableSettings['!METADATA'] = { 
            'cloudbase.tablet.split.threshold': 10000,
        }
    tableSettings['test_ingest'] = { 
            'cloudbase.tablet.split.threshold': 50000,
        }

    def runTest(self):
        self.sleep(1)
        self.shutdown_cloudbase()

        # look for any exceptions
        self.wait(
            self.runOn(self.masterHost(),
                       ['grep', '-r', '-q', '" at cloudbase.core"',
                        os.path.join(CLOUDBASE_HOME,'logs') ])
            )

def suite():
    result = unittest.TestSuite()
    result.addTest(ShutdownSplitter())
    result.addTest(KilledTabletDuringShutdown())
    result.addTest(KilledTabletDuringScan())
    result.addTest(RestartMasterTest())
    result.addTest(KilledTabletServerTest())
    return result
