#! /usr/bin/env python

from cbshell import client
from data.ttypes import KeyExtent, Range, Column

def printRows(rows):
    for row in rows:
        print repr(row.key.row), row.key.column.columnFamily + ":" + row.key.column.columnQualifier, repr(row.value)

def main():
    c = client()
    cr = Column()
    cr.columnFamily = 'metadata'
    cr.columnQualifier = 'location'
    result = c.startScan('!METADATA', Range(), [cr], 10)
    data = result.result
    while data.more:
        printRows(data.data)
        data = c.continueScan(result.scanID)
    printRows(data.data)
    c.closeScan(result.scanID)

main()
