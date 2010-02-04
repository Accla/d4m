#
# Autogenerated by Thrift
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#

from thrift.Thrift import *

from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
try:
  from thrift.protocol import fastbinary
except:
  fastbinary = None


class Key:

  thrift_spec = (
    None, # 0
    (1, TType.I32, 'colFamilyOffset', None, None, ), # 1
    (2, TType.I32, 'colQualifierOffset', None, None, ), # 2
    (3, TType.I32, 'colVisibilityOffset', None, None, ), # 3
    (4, TType.I32, 'totalLen', None, None, ), # 4
    (5, TType.STRING, 'keyData', None, None, ), # 5
    (6, TType.I64, 'timestamp', None, None, ), # 6
  )

  def __init__(self, d=None):
    self.colFamilyOffset = None
    self.colQualifierOffset = None
    self.colVisibilityOffset = None
    self.totalLen = None
    self.keyData = None
    self.timestamp = None
    if isinstance(d, dict):
      if 'colFamilyOffset' in d:
        self.colFamilyOffset = d['colFamilyOffset']
      if 'colQualifierOffset' in d:
        self.colQualifierOffset = d['colQualifierOffset']
      if 'colVisibilityOffset' in d:
        self.colVisibilityOffset = d['colVisibilityOffset']
      if 'totalLen' in d:
        self.totalLen = d['totalLen']
      if 'keyData' in d:
        self.keyData = d['keyData']
      if 'timestamp' in d:
        self.timestamp = d['timestamp']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.I32:
          self.colFamilyOffset = iprot.readI32();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.I32:
          self.colQualifierOffset = iprot.readI32();
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.I32:
          self.colVisibilityOffset = iprot.readI32();
        else:
          iprot.skip(ftype)
      elif fid == 4:
        if ftype == TType.I32:
          self.totalLen = iprot.readI32();
        else:
          iprot.skip(ftype)
      elif fid == 5:
        if ftype == TType.STRING:
          self.keyData = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 6:
        if ftype == TType.I64:
          self.timestamp = iprot.readI64();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('Key')
    if self.colFamilyOffset != None:
      oprot.writeFieldBegin('colFamilyOffset', TType.I32, 1)
      oprot.writeI32(self.colFamilyOffset)
      oprot.writeFieldEnd()
    if self.colQualifierOffset != None:
      oprot.writeFieldBegin('colQualifierOffset', TType.I32, 2)
      oprot.writeI32(self.colQualifierOffset)
      oprot.writeFieldEnd()
    if self.colVisibilityOffset != None:
      oprot.writeFieldBegin('colVisibilityOffset', TType.I32, 3)
      oprot.writeI32(self.colVisibilityOffset)
      oprot.writeFieldEnd()
    if self.totalLen != None:
      oprot.writeFieldBegin('totalLen', TType.I32, 4)
      oprot.writeI32(self.totalLen)
      oprot.writeFieldEnd()
    if self.keyData != None:
      oprot.writeFieldBegin('keyData', TType.STRING, 5)
      oprot.writeString(self.keyData)
      oprot.writeFieldEnd()
    if self.timestamp != None:
      oprot.writeFieldBegin('timestamp', TType.I64, 6)
      oprot.writeI64(self.timestamp)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class Column:

  thrift_spec = (
    None, # 0
    (1, TType.STRING, 'columnFamily', None, None, ), # 1
    (2, TType.STRING, 'columnQualifier', None, None, ), # 2
    (3, TType.STRING, 'columnVisibility', None, None, ), # 3
  )

  def __init__(self, d=None):
    self.columnFamily = None
    self.columnQualifier = None
    self.columnVisibility = None
    if isinstance(d, dict):
      if 'columnFamily' in d:
        self.columnFamily = d['columnFamily']
      if 'columnQualifier' in d:
        self.columnQualifier = d['columnQualifier']
      if 'columnVisibility' in d:
        self.columnVisibility = d['columnVisibility']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRING:
          self.columnFamily = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.STRING:
          self.columnQualifier = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.STRING:
          self.columnVisibility = iprot.readString();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('Column')
    if self.columnFamily != None:
      oprot.writeFieldBegin('columnFamily', TType.STRING, 1)
      oprot.writeString(self.columnFamily)
      oprot.writeFieldEnd()
    if self.columnQualifier != None:
      oprot.writeFieldBegin('columnQualifier', TType.STRING, 2)
      oprot.writeString(self.columnQualifier)
      oprot.writeFieldEnd()
    if self.columnVisibility != None:
      oprot.writeFieldBegin('columnVisibility', TType.STRING, 3)
      oprot.writeString(self.columnVisibility)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class ColumnUpdate:

  thrift_spec = (
    None, # 0
    (1, TType.STRUCT, 'column', (Column, Column.thrift_spec), None, ), # 1
    (2, TType.I64, 'timestamp', None, None, ), # 2
    (3, TType.BOOL, 'hasTimestamp', None, None, ), # 3
    (4, TType.STRING, 'value', None, None, ), # 4
    (5, TType.BOOL, 'deleted', None, None, ), # 5
  )

  def __init__(self, d=None):
    self.column = None
    self.timestamp = None
    self.hasTimestamp = None
    self.value = None
    self.deleted = None
    if isinstance(d, dict):
      if 'column' in d:
        self.column = d['column']
      if 'timestamp' in d:
        self.timestamp = d['timestamp']
      if 'hasTimestamp' in d:
        self.hasTimestamp = d['hasTimestamp']
      if 'value' in d:
        self.value = d['value']
      if 'deleted' in d:
        self.deleted = d['deleted']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRUCT:
          self.column = Column()
          self.column.read(iprot)
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.I64:
          self.timestamp = iprot.readI64();
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.BOOL:
          self.hasTimestamp = iprot.readBool();
        else:
          iprot.skip(ftype)
      elif fid == 4:
        if ftype == TType.STRING:
          self.value = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 5:
        if ftype == TType.BOOL:
          self.deleted = iprot.readBool();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('ColumnUpdate')
    if self.column != None:
      oprot.writeFieldBegin('column', TType.STRUCT, 1)
      self.column.write(oprot)
      oprot.writeFieldEnd()
    if self.timestamp != None:
      oprot.writeFieldBegin('timestamp', TType.I64, 2)
      oprot.writeI64(self.timestamp)
      oprot.writeFieldEnd()
    if self.hasTimestamp != None:
      oprot.writeFieldBegin('hasTimestamp', TType.BOOL, 3)
      oprot.writeBool(self.hasTimestamp)
      oprot.writeFieldEnd()
    if self.value != None:
      oprot.writeFieldBegin('value', TType.STRING, 4)
      oprot.writeString(self.value)
      oprot.writeFieldEnd()
    if self.deleted != None:
      oprot.writeFieldBegin('deleted', TType.BOOL, 5)
      oprot.writeBool(self.deleted)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class Mutation:

  thrift_spec = (
    None, # 0
    (1, TType.STRING, 'row', None, None, ), # 1
    (2, TType.LIST, 'updates', (TType.STRUCT,(ColumnUpdate, ColumnUpdate.thrift_spec)), None, ), # 2
  )

  def __init__(self, d=None):
    self.row = None
    self.updates = None
    if isinstance(d, dict):
      if 'row' in d:
        self.row = d['row']
      if 'updates' in d:
        self.updates = d['updates']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRING:
          self.row = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.LIST:
          self.updates = []
          (_etype3, _size0) = iprot.readListBegin()
          for _i4 in xrange(_size0):
            _elem5 = ColumnUpdate()
            _elem5.read(iprot)
            self.updates.append(_elem5)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('Mutation')
    if self.row != None:
      oprot.writeFieldBegin('row', TType.STRING, 1)
      oprot.writeString(self.row)
      oprot.writeFieldEnd()
    if self.updates != None:
      oprot.writeFieldBegin('updates', TType.LIST, 2)
      oprot.writeListBegin(TType.STRUCT, len(self.updates))
      for iter6 in self.updates:
        iter6.write(oprot)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class KeyExtent:

  thrift_spec = (
    None, # 0
    (1, TType.STRING, 'table', None, None, ), # 1
    (2, TType.STRING, 'endRow', None, None, ), # 2
    (3, TType.STRING, 'prevEndRow', None, None, ), # 3
  )

  def __init__(self, d=None):
    self.table = None
    self.endRow = None
    self.prevEndRow = None
    if isinstance(d, dict):
      if 'table' in d:
        self.table = d['table']
      if 'endRow' in d:
        self.endRow = d['endRow']
      if 'prevEndRow' in d:
        self.prevEndRow = d['prevEndRow']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRING:
          self.table = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.STRING:
          self.endRow = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.STRING:
          self.prevEndRow = iprot.readString();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('KeyExtent')
    if self.table != None:
      oprot.writeFieldBegin('table', TType.STRING, 1)
      oprot.writeString(self.table)
      oprot.writeFieldEnd()
    if self.endRow != None:
      oprot.writeFieldBegin('endRow', TType.STRING, 2)
      oprot.writeString(self.endRow)
      oprot.writeFieldEnd()
    if self.prevEndRow != None:
      oprot.writeFieldBegin('prevEndRow', TType.STRING, 3)
      oprot.writeString(self.prevEndRow)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class KeyValue:

  thrift_spec = (
    None, # 0
    (1, TType.STRUCT, 'key', (Key, Key.thrift_spec), None, ), # 1
    (2, TType.STRING, 'value', None, None, ), # 2
  )

  def __init__(self, d=None):
    self.key = None
    self.value = None
    if isinstance(d, dict):
      if 'key' in d:
        self.key = d['key']
      if 'value' in d:
        self.value = d['value']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRUCT:
          self.key = Key()
          self.key.read(iprot)
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.STRING:
          self.value = iprot.readString();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('KeyValue')
    if self.key != None:
      oprot.writeFieldBegin('key', TType.STRUCT, 1)
      self.key.write(oprot)
      oprot.writeFieldEnd()
    if self.value != None:
      oprot.writeFieldBegin('value', TType.STRING, 2)
      oprot.writeString(self.value)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class ScanResult:

  thrift_spec = (
    None, # 0
    (1, TType.LIST, 'data', (TType.STRUCT,(KeyValue, KeyValue.thrift_spec)), None, ), # 1
    (2, TType.BOOL, 'more', None, None, ), # 2
  )

  def __init__(self, d=None):
    self.data = None
    self.more = None
    if isinstance(d, dict):
      if 'data' in d:
        self.data = d['data']
      if 'more' in d:
        self.more = d['more']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.LIST:
          self.data = []
          (_etype10, _size7) = iprot.readListBegin()
          for _i11 in xrange(_size7):
            _elem12 = KeyValue()
            _elem12.read(iprot)
            self.data.append(_elem12)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.BOOL:
          self.more = iprot.readBool();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('ScanResult')
    if self.data != None:
      oprot.writeFieldBegin('data', TType.LIST, 1)
      oprot.writeListBegin(TType.STRUCT, len(self.data))
      for iter13 in self.data:
        iter13.write(oprot)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.more != None:
      oprot.writeFieldBegin('more', TType.BOOL, 2)
      oprot.writeBool(self.more)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class Range:

  thrift_spec = (
    None, # 0
    (1, TType.STRUCT, 'start', (Key, Key.thrift_spec), None, ), # 1
    (2, TType.STRUCT, 'stop', (Key, Key.thrift_spec), None, ), # 2
    (3, TType.BOOL, 'startKeyInclusive', None, None, ), # 3
    (4, TType.BOOL, 'stopKeyInclusive', None, None, ), # 4
    (5, TType.BOOL, 'infiniteStartKey', None, None, ), # 5
    (6, TType.BOOL, 'infiniteStopKey', None, None, ), # 6
  )

  def __init__(self, d=None):
    self.start = None
    self.stop = None
    self.startKeyInclusive = None
    self.stopKeyInclusive = None
    self.infiniteStartKey = None
    self.infiniteStopKey = None
    if isinstance(d, dict):
      if 'start' in d:
        self.start = d['start']
      if 'stop' in d:
        self.stop = d['stop']
      if 'startKeyInclusive' in d:
        self.startKeyInclusive = d['startKeyInclusive']
      if 'stopKeyInclusive' in d:
        self.stopKeyInclusive = d['stopKeyInclusive']
      if 'infiniteStartKey' in d:
        self.infiniteStartKey = d['infiniteStartKey']
      if 'infiniteStopKey' in d:
        self.infiniteStopKey = d['infiniteStopKey']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRUCT:
          self.start = Key()
          self.start.read(iprot)
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.STRUCT:
          self.stop = Key()
          self.stop.read(iprot)
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.BOOL:
          self.startKeyInclusive = iprot.readBool();
        else:
          iprot.skip(ftype)
      elif fid == 4:
        if ftype == TType.BOOL:
          self.stopKeyInclusive = iprot.readBool();
        else:
          iprot.skip(ftype)
      elif fid == 5:
        if ftype == TType.BOOL:
          self.infiniteStartKey = iprot.readBool();
        else:
          iprot.skip(ftype)
      elif fid == 6:
        if ftype == TType.BOOL:
          self.infiniteStopKey = iprot.readBool();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('Range')
    if self.start != None:
      oprot.writeFieldBegin('start', TType.STRUCT, 1)
      self.start.write(oprot)
      oprot.writeFieldEnd()
    if self.stop != None:
      oprot.writeFieldBegin('stop', TType.STRUCT, 2)
      self.stop.write(oprot)
      oprot.writeFieldEnd()
    if self.startKeyInclusive != None:
      oprot.writeFieldBegin('startKeyInclusive', TType.BOOL, 3)
      oprot.writeBool(self.startKeyInclusive)
      oprot.writeFieldEnd()
    if self.stopKeyInclusive != None:
      oprot.writeFieldBegin('stopKeyInclusive', TType.BOOL, 4)
      oprot.writeBool(self.stopKeyInclusive)
      oprot.writeFieldEnd()
    if self.infiniteStartKey != None:
      oprot.writeFieldBegin('infiniteStartKey', TType.BOOL, 5)
      oprot.writeBool(self.infiniteStartKey)
      oprot.writeFieldEnd()
    if self.infiniteStopKey != None:
      oprot.writeFieldBegin('infiniteStopKey', TType.BOOL, 6)
      oprot.writeBool(self.infiniteStopKey)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class InitialScan:

  thrift_spec = (
    None, # 0
    (1, TType.I64, 'scanID', None, None, ), # 1
    (2, TType.STRUCT, 'result', (ScanResult, ScanResult.thrift_spec), None, ), # 2
  )

  def __init__(self, d=None):
    self.scanID = None
    self.result = None
    if isinstance(d, dict):
      if 'scanID' in d:
        self.scanID = d['scanID']
      if 'result' in d:
        self.result = d['result']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.I64:
          self.scanID = iprot.readI64();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.STRUCT:
          self.result = ScanResult()
          self.result.read(iprot)
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('InitialScan')
    if self.scanID != None:
      oprot.writeFieldBegin('scanID', TType.I64, 1)
      oprot.writeI64(self.scanID)
      oprot.writeFieldEnd()
    if self.result != None:
      oprot.writeFieldBegin('result', TType.STRUCT, 2)
      self.result.write(oprot)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class IterInfo:

  thrift_spec = (
    None, # 0
    (1, TType.I32, 'priority', None, None, ), # 1
    (2, TType.STRING, 'className', None, None, ), # 2
    (3, TType.STRING, 'iterName', None, None, ), # 3
  )

  def __init__(self, d=None):
    self.priority = None
    self.className = None
    self.iterName = None
    if isinstance(d, dict):
      if 'priority' in d:
        self.priority = d['priority']
      if 'className' in d:
        self.className = d['className']
      if 'iterName' in d:
        self.iterName = d['iterName']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.I32:
          self.priority = iprot.readI32();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.STRING:
          self.className = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.STRING:
          self.iterName = iprot.readString();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('IterInfo')
    if self.priority != None:
      oprot.writeFieldBegin('priority', TType.I32, 1)
      oprot.writeI32(self.priority)
      oprot.writeFieldEnd()
    if self.className != None:
      oprot.writeFieldBegin('className', TType.STRING, 2)
      oprot.writeString(self.className)
      oprot.writeFieldEnd()
    if self.iterName != None:
      oprot.writeFieldBegin('iterName', TType.STRING, 3)
      oprot.writeString(self.iterName)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class ConstraintViolationSummary:

  thrift_spec = (
    None, # 0
    (1, TType.STRING, 'constrainClass', None, None, ), # 1
    (2, TType.I16, 'violationCode', None, None, ), # 2
    (3, TType.STRING, 'violationDescription', None, None, ), # 3
    (4, TType.I64, 'numberOfViolatingMutations', None, None, ), # 4
  )

  def __init__(self, d=None):
    self.constrainClass = None
    self.violationCode = None
    self.violationDescription = None
    self.numberOfViolatingMutations = None
    if isinstance(d, dict):
      if 'constrainClass' in d:
        self.constrainClass = d['constrainClass']
      if 'violationCode' in d:
        self.violationCode = d['violationCode']
      if 'violationDescription' in d:
        self.violationDescription = d['violationDescription']
      if 'numberOfViolatingMutations' in d:
        self.numberOfViolatingMutations = d['numberOfViolatingMutations']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRING:
          self.constrainClass = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.I16:
          self.violationCode = iprot.readI16();
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.STRING:
          self.violationDescription = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 4:
        if ftype == TType.I64:
          self.numberOfViolatingMutations = iprot.readI64();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('ConstraintViolationSummary')
    if self.constrainClass != None:
      oprot.writeFieldBegin('constrainClass', TType.STRING, 1)
      oprot.writeString(self.constrainClass)
      oprot.writeFieldEnd()
    if self.violationCode != None:
      oprot.writeFieldBegin('violationCode', TType.I16, 2)
      oprot.writeI16(self.violationCode)
      oprot.writeFieldEnd()
    if self.violationDescription != None:
      oprot.writeFieldBegin('violationDescription', TType.STRING, 3)
      oprot.writeString(self.violationDescription)
      oprot.writeFieldEnd()
    if self.numberOfViolatingMutations != None:
      oprot.writeFieldBegin('numberOfViolatingMutations', TType.I64, 4)
      oprot.writeI64(self.numberOfViolatingMutations)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class UpdateErrors:

  thrift_spec = (
    None, # 0
    (1, TType.MAP, 'failedExtents', (TType.STRUCT,(KeyExtent, KeyExtent.thrift_spec),TType.I64,None), None, ), # 1
    (2, TType.LIST, 'violationSummaries', (TType.STRUCT,(ConstraintViolationSummary, ConstraintViolationSummary.thrift_spec)), None, ), # 2
    (3, TType.LIST, 'authorizationFailures', (TType.STRUCT,(KeyExtent, KeyExtent.thrift_spec)), None, ), # 3
  )

  def __init__(self, d=None):
    self.failedExtents = None
    self.violationSummaries = None
    self.authorizationFailures = None
    if isinstance(d, dict):
      if 'failedExtents' in d:
        self.failedExtents = d['failedExtents']
      if 'violationSummaries' in d:
        self.violationSummaries = d['violationSummaries']
      if 'authorizationFailures' in d:
        self.authorizationFailures = d['authorizationFailures']

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.MAP:
          self.failedExtents = {}
          (_ktype15, _vtype16, _size14 ) = iprot.readMapBegin() 
          for _i18 in xrange(_size14):
            _key19 = KeyExtent()
            _key19.read(iprot)
            _val20 = iprot.readI64();
            self.failedExtents[_key19] = _val20
          iprot.readMapEnd()
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.LIST:
          self.violationSummaries = []
          (_etype24, _size21) = iprot.readListBegin()
          for _i25 in xrange(_size21):
            _elem26 = ConstraintViolationSummary()
            _elem26.read(iprot)
            self.violationSummaries.append(_elem26)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.LIST:
          self.authorizationFailures = []
          (_etype30, _size27) = iprot.readListBegin()
          for _i31 in xrange(_size27):
            _elem32 = KeyExtent()
            _elem32.read(iprot)
            self.authorizationFailures.append(_elem32)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('UpdateErrors')
    if self.failedExtents != None:
      oprot.writeFieldBegin('failedExtents', TType.MAP, 1)
      oprot.writeMapBegin(TType.STRUCT, TType.I64, len(self.failedExtents))
      for kiter33,viter34 in self.failedExtents.items():
        kiter33.write(oprot)
        oprot.writeI64(viter34)
      oprot.writeMapEnd()
      oprot.writeFieldEnd()
    if self.violationSummaries != None:
      oprot.writeFieldBegin('violationSummaries', TType.LIST, 2)
      oprot.writeListBegin(TType.STRUCT, len(self.violationSummaries))
      for iter35 in self.violationSummaries:
        iter35.write(oprot)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.authorizationFailures != None:
      oprot.writeFieldBegin('authorizationFailures', TType.LIST, 3)
      oprot.writeListBegin(TType.STRUCT, len(self.authorizationFailures))
      for iter36 in self.authorizationFailures:
        iter36.write(oprot)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def __str__(self): 
    return str(self.__dict__)

  def __repr__(self): 
    return repr(self.__dict__)

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)
