/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package cloudbase.core.client.proxy.thrift;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import com.facebook.thrift.*;

import com.facebook.thrift.protocol.*;
import com.facebook.thrift.transport.*;

public class Column implements TBase, java.io.Serializable {
  public byte[] columnFamily;
  public byte[] columnQualifier;
  public List<List<Short>> columnVisibility;

  public final Isset __isset = new Isset();
  public static final class Isset implements java.io.Serializable {
    public boolean columnFamily = false;
    public boolean columnQualifier = false;
    public boolean columnVisibility = false;
  }

  public Column() {
  }

  public Column(
    byte[] columnFamily,
    byte[] columnQualifier,
    List<List<Short>> columnVisibility)
  {
    this();
    this.columnFamily = columnFamily;
    this.__isset.columnFamily = true;
    this.columnQualifier = columnQualifier;
    this.__isset.columnQualifier = true;
    this.columnVisibility = columnVisibility;
    this.__isset.columnVisibility = true;
  }

  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Column)
      return this.equals((Column)that);
    return false;
  }

  public boolean equals(Column that) {
    if (that == null)
      return false;

    boolean this_present_columnFamily = true && (this.columnFamily != null);
    boolean that_present_columnFamily = true && (that.columnFamily != null);
    if (this_present_columnFamily || that_present_columnFamily) {
      if (!(this_present_columnFamily && that_present_columnFamily))
        return false;
      if (!java.util.Arrays.equals(this.columnFamily, that.columnFamily))
        return false;
    }

    boolean this_present_columnQualifier = true && (this.columnQualifier != null);
    boolean that_present_columnQualifier = true && (that.columnQualifier != null);
    if (this_present_columnQualifier || that_present_columnQualifier) {
      if (!(this_present_columnQualifier && that_present_columnQualifier))
        return false;
      if (!java.util.Arrays.equals(this.columnQualifier, that.columnQualifier))
        return false;
    }

    boolean this_present_columnVisibility = true && (this.columnVisibility != null);
    boolean that_present_columnVisibility = true && (that.columnVisibility != null);
    if (this_present_columnVisibility || that_present_columnVisibility) {
      if (!(this_present_columnVisibility && that_present_columnVisibility))
        return false;
      if (!this.columnVisibility.equals(that.columnVisibility))
        return false;
    }

    return true;
  }

  public int hashCode() {
    return 0;
  }

  public void read(TProtocol iprot) throws TException {
    TField field;
    iprot.readStructBegin();
    while (true)
    {
      field = iprot.readFieldBegin();
      if (field.type == TType.STOP) { 
        break;
      }
      switch (field.id)
      {
        case 1:
          if (field.type == TType.STRING) {
            this.columnFamily = iprot.readBinary();
            this.__isset.columnFamily = true;
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2:
          if (field.type == TType.STRING) {
            this.columnQualifier = iprot.readBinary();
            this.__isset.columnQualifier = true;
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3:
          if (field.type == TType.LIST) {
            {
              TList _list0 = iprot.readListBegin();
              this.columnVisibility = new ArrayList<List<Short>>(_list0.size);
              for (int _i1 = 0; _i1 < _list0.size; ++_i1)
              {
                List<Short> _elem2 = new ArrayList<Short>();
                {
                  TList _list3 = iprot.readListBegin();
                  _elem2 = new ArrayList<Short>(_list3.size);
                  for (int _i4 = 0; _i4 < _list3.size; ++_i4)
                  {
                    short _elem5 = 0;
                    _elem5 = iprot.readI16();
                    _elem2.add(_elem5);
                  }
                  iprot.readListEnd();
                }
                this.columnVisibility.add(_elem2);
              }
              iprot.readListEnd();
            }
            this.__isset.columnVisibility = true;
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        default:
          TProtocolUtil.skip(iprot, field.type);
          break;
      }
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();
  }

  public void write(TProtocol oprot) throws TException {
    TStruct struct = new TStruct("Column");
    oprot.writeStructBegin(struct);
    TField field = new TField();
    if (this.columnFamily != null) {
      field.name = "columnFamily";
      field.type = TType.STRING;
      field.id = 1;
      oprot.writeFieldBegin(field);
      oprot.writeBinary(this.columnFamily);
      oprot.writeFieldEnd();
    }
    if (this.columnQualifier != null) {
      field.name = "columnQualifier";
      field.type = TType.STRING;
      field.id = 2;
      oprot.writeFieldBegin(field);
      oprot.writeBinary(this.columnQualifier);
      oprot.writeFieldEnd();
    }
    if (this.columnVisibility != null) {
      field.name = "columnVisibility";
      field.type = TType.LIST;
      field.id = 3;
      oprot.writeFieldBegin(field);
      {
        oprot.writeListBegin(new TList(TType.LIST, this.columnVisibility.size()));
        for (List<Short> _iter6 : this.columnVisibility)        {
          {
            oprot.writeListBegin(new TList(TType.I16, _iter6.size()));
            for (short _iter7 : _iter6)            {
              oprot.writeI16(_iter7);
            }
            oprot.writeListEnd();
          }
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("Column(");
    sb.append("columnFamily:");
    sb.append(this.columnFamily);
    sb.append(",columnQualifier:");
    sb.append(this.columnQualifier);
    sb.append(",columnVisibility:");
    sb.append(this.columnVisibility);
    sb.append(")");
    return sb.toString();
  }

}
