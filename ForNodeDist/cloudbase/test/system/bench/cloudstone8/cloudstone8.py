import unittest

from lib import cloudshell
from lib.TeraSortBenchmark import TeraSortBenchmark

class CloudStone8(TeraSortBenchmark):
    "Tests variable length input keys and values"
    
    keymin = 10
    keymax = 50
    valmin = 100
    valmax = 500
    rows = 1000000
    tablename = 'VariableLengthIngestTable'
    
    
    def shortDescription(self):
        return 'Ingests %d rows of variable key and value length to be sorted. '\
               'Lower score is better.' % (self.numrows())
    
    def setSpeed(self, speed):
        if speed == "slow":
            self.rows = 1000000
            self.keymin = 60
            self.keymax = 100
            self.valmin = 200
            self.valmax = 300
        elif speed == "medium":
            self.rows = 100000
            self.keymin = 40
            self.keymax = 70
            self.valmin = 130
            self.valmax = 170
        elif speed == "fast":
            self.rows = 10000 
            self.keymin = 30
            self.keymax = 50
            self.valmin = 80
            self.valmax = 100 

def suite():
    result = unittest.TestSuite([
        CloudStone8(),
        ])
    return result
