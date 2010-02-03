package cloudbase.core.security;

import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class LabelClause implements Comparable<LabelClause>{

	static Logger log = Logger.getLogger(LabelClause.class.getName());	
	private TreeSet<Short> labels;

	public LabelClause() {
		this.labels = new TreeSet<Short>();
	}
	
	public LabelClause(short s) {
		this.labels = new TreeSet<Short>();
		this.labels.add(s);
	}
		
	public LabelClause(short[] sa) {
		this.labels = new TreeSet<Short>();
		for (int i=0; i<sa.length; i++) {
			this.labels.add(sa[i]);
		}	
	}
	
	public LabelClause(byte[] ba) {
		this(LabelConversions.clauseBytesToShorts(ba));
	}
	
	public void add(short s) {
		labels.add(new Short(s));
	}
	
	public void remove(short s) {
		labels.remove(new Short(s));
	}
	
	public void add(short[] sa) {
		for (int i=0; i<sa.length; i++) {
			labels.add(sa[i]);
		}
	}
	
	public void remove(short[] sa) {
		for (int i=0; i<sa.length; i++) {
			labels.remove(sa[i]);
		}
	}
	
	public void clear() {
		labels = new TreeSet<Short>();
	}
	
	public boolean contains(short s) {
		return labels.contains(new Short(s));
	}
	
	public int size() {
		return labels.size();
	}
	
	public short[] toShortArray() {
		Iterator<Short> it = labels.iterator();
		short[] sa = new short[labels.size()];
		for (int i=0; i<labels.size(); i++) {
			sa[i] = it.next();
		}	
		return sa;
	}
	
	public byte[] toByteArray() {
		return LabelConversions.clauseShortsToBytes(this.toShortArray());
	}
	
	public String toString() {
		Iterator<Short> it = labels.iterator();
		short lbl;
		String s = "(";
		if (labels.size() > 0) {
			for (int i=0; i<labels.size()-1; i++) {
				lbl = it.next();
				s += lbl;
				s += ",";
			}
			s += it.next();
		}
		s += ")";
		return s;
	}
	
	public int compareTo(LabelClause other) {
		if (this.size()==0)
			return -1;
		if (other==null || ((LabelClause)other).size()==0)
			return 1;
		short[] sThis = this.toShortArray();
		short[] sOther = ((LabelClause)other).toShortArray();
		
		// Compare item by item
		for (int i = 0; i < (sThis.length > sOther.length? sOther.length : sThis.length); i++) {
			if (sThis[i] > sOther[i])
				return 1;
			else if (sThis[i] < sOther[i])
				return -1;
		}
		
		if (sThis.length > sOther.length)
			return 1;
		else if (sThis.length < sOther.length)
			return -1;
		
		return 0;
	}
	
	public boolean subsetOf(LabelClause other) {
		Iterator<Short> iter = labels.iterator();
		while (iter.hasNext()) {
			if (!other.contains(iter.next()))
				return false;
		}
		return true;
	}
}
