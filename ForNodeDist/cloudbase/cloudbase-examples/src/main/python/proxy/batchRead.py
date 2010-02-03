#! /usr/bin/env python

import sys

from cbshell import client
from data.ttypes import Column
from clientproxy.ttypes import ColumnUpdate, Mutation, Range, Key

def batch(c, *args):
    "Call the client batch API, but make the result an iterator"
    batch = c.lookup(*args)
    while True:
        data = c.fetch(batch)
        for row in data.data:
            yield row
        if not data.more:
            break
    c.closeBatch(batch)

def main():
    table = 'atablename'
    columnFamily = 'af'
    columnQualifier = 'acolumn'
    cr = Column(locals())

    c = client()
    c.createTable(table, [])

    # put in some data to batch read 
    mutations = []
    for i in range(100):
       # set a column value
       cu = ColumnUpdate(dict(column = cr, value = '%d:value' % i))
       mutations.append(Mutation(dict(row = "%02d:row" % i, updates=[cu])))

    errs = c.update(table, mutations)

    arange = Range()
    arange.start = Key(dict(row='\0'))
    arange.stop = Key(dict(row='1'))

    count = 0
    for row in batch(c, table, [arange], [cr], []):
	count += 1
        print repr(row.key.row), row.key.column.columnQualifier, repr(row.value)
        assert row.key.row.startswith('0')
    assert count == 10

main()
