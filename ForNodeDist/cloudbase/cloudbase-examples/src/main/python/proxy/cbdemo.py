#! /usr/bin/env python
import time

from cbshell import client
from clientproxy.ttypes import *

def createTable(client, tableName):
    tables = client.getTables()
    if tableName not in tables:
        client.createTable(tableName, [])

def insertRows(client, tableName, count):
    column = Column(dict(columnFamily='cf',
                         columnQualifier='cq',
                         columnVisibility=None))
    column2 = Column(dict(columnFamily='cf',
                          columnQualifier='cq2',
                          columnVisibility=None))

    mutations = []
    for i in range(count):
        cu = ColumnUpdate(dict(column=column, value = 'value:%d' % i))
        cu2 = ColumnUpdate(dict(column=column2, value = '%d:value' % i))
        mutations.append( Mutation(dict(row='%d' % i, updates = [cu, cu2])) )
    errs = client.update(tableName, mutations)


def main():
    c = client()

    tableName = 'example'
    
    createTable(c, tableName)
    
    insertRows(c, tableName, 100)

    # get the "90" rows
    start = Key(dict(row='90'))
    stop = Key(dict(row='91'))
    range = Range(dict(start=start, stop=stop))
    for row in c.scan(tableName, range, [], 50):
        print row.key.row, row.key.column, row.value

    c.deleteTable(tableName)

if __name__ == '__main__':
    main()

