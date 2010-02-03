package cloudbase.core.security;


import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class LabelExpression {
		
	static Logger log = Logger.getLogger(LabelExpression.class.getName());	
	
	private SortedSet<LabelClause> logicExpression;
	private int formatID;	// range: 0-15, describes how to represent this expression as a byte array
							// and determines how the CellLevelAuthenticator interprets the labels
		
	private Iterator<LabelClause> iter;
	
	// create general CNF expression
	public LabelExpression() {	
		formatID = 0;
		logicExpression = new TreeSet<LabelClause>();
		iter = null;
	}
	
	// create CNF with no disjunctions (authorizations must contain EVERY label)
	public LabelExpression(short[] conj) {
		this.formatID = 2;
		logicExpression = new TreeSet<LabelClause>();
		for (int i=0; i<conj.length; i++) {
			this.addClause(new LabelClause(conj[i]));
		}
		iter = null;
	}
	
	private static short[] toArray(List<Short> s) {
	    short[] result = new short[s.size()];
	    for (int i = 0; i < s.size(); i++)
	        result[i] = s.get(i);
	    return result;
	}
	
	public LabelExpression(List<List<Short>> expr) {
	    assert expr.size() == 2;
	    
	    this.formatID = 3;
        logicExpression = new TreeSet<LabelClause>();
        for (int i=0; i<expr.get(0).size(); i++) {
            this.addClause(new LabelClause(expr.get(0).get(i)));
        }
        this.addClause(new LabelClause(toArray(expr.get(1))));
        iter = null;
	}
	

	// create CNF where only last clause has disjunctions
	public LabelExpression(short[] conj, short[] disj) {  
		this.formatID = 3;
		logicExpression = new TreeSet<LabelClause>();
		for (int i=0; i<conj.length; i++) {
			this.addClause(new LabelClause(conj[i]));
		}
		this.addClause(new LabelClause(disj));
		iter = null;

	}
	
	//construct SecurityExpression based on its Key byte representation
	public LabelExpression(byte[] ba) {
		this(ba, 0, ba.length);
	}
	
	public LabelExpression(byte[] ba, int offset, int len) {	
		if (ba==null || len==0) {
			formatID = 0;
			logicExpression = new TreeSet<LabelClause>();
			iter = null;
			return;
		}
		byte firstByte = ba[offset];
		int format = (firstByte & 0xF0) >> 4;
		int headerLen = firstByte & 0x0F;
		
		if (format == 0 || format == 1 || format == 2 | format == 3) {
			this.formatID = format;
		}
		else {
			log.error("formatID " + formatID + " not recognized, setting to general CNF.");
			this.formatID = 0;
		}

		
		this.logicExpression = new TreeSet<LabelClause>();
		boolean endClause; // is this the last label in the current clause?
		LabelClause sc = new LabelClause();
		short s;
		byte msb, lsb;
		for (int i=headerLen + offset; i<len+offset; i+=2) {
			if ((len-headerLen)%2==1 && i+1==len+offset)
				break;
			endClause = (ba[i] < 0);
			msb = ba[i];
			lsb = ba[i+1];
			s = (short) ( ((msb << 8) & 0x7F00) + (lsb & 0x00FF) );
			sc.add(s);
			if (endClause) {
				this.addClause(sc);
				sc = new LabelClause();
			}
		}
		iter = null;		
	}
	
	public void addClause(LabelClause c) {
		//Logic works for both CNF and DNF
		LabelClause replace = this.getSuperSet(c);
		if (replace==null)
			logicExpression.add(c);
		else if (!replace.equals(c)) {
			logicExpression.remove(replace);
			logicExpression.add(c);
		}
	}
	
	public void addClause(short[] sa) {
		this.addClause(new LabelClause (sa));
	}
	
	private int getHeaderLength() {
		switch (formatID) {
		case 0:	// general CNF
			return 1;
		case 1:	// general DNF
			return 1;
		case 2: // special case: CNF with no disjunction operations
			return 1;
		case 3: // special case: CNF with only the last clause containing disjunctions
			return 2;
		default:
			log.error("formatID " + formatID + " not recognized, unable to determine header length.");
			return 1;
		}
	}
	
	// returns CNF or DNF, depending on the value of ormatID
	public String getType() {
		switch (formatID) {
		case 0:	// general CNF
			return "CNF";
		case 1:	// general DNF
			return "DNF";
		case 2: // special case: CNF with no disjunction operations
			return "CNF";
		case 3: // special case: CNF with only the last clause containing disjunctions
			return "CNF";
		default:
			log.error("formatID " + formatID + " not recognized, unable to determine expression type.");
			return "UNKNOWN";
		}
	}
	
	public int getFormatID() {
		return this.formatID;
	}
	
	public void setFormatID(int format) {
		if (format > 1) {
			log.error("Cannot set formatID > 1.  Use special constructors for special formatID's.");
		}
		else {
			this.formatID = format;
		}
	}
	
	public boolean hasNext() {
		if (iter != null && iter.hasNext())
			return true;
		else if (iter == null) {
			resetIterator();
			return iter.hasNext();
		}
		return false;
	}
	
	public short[] next() {
		if (iter == null || !iter.hasNext()) {
			return null;
		}
		else {
			return iter.next().toShortArray();
		}
	}
	
	public void resetIterator() {
		iter = logicExpression.iterator();
	}
	
	public byte[] toByteArray() {
		if (logicExpression.isEmpty()) {
			return new byte[0];
		}
		byte[] ba = new byte[(this.size() << 1) + this.getHeaderLength() ];
		int cur_pos;
		byte[] clause_ba;
		switch (formatID) {
		case 0:	// general CNF
			ba[0] = 0x01;
			break;
		case 1:	// general DNF
			ba[0] = 0x11;
			break;
		case 2: // CNF with no disjunction operations
			ba[0] = 0x21;
			cur_pos = this.getHeaderLength();
			break;
		case 3: // CNF with only the last clause containing disjunctions
			ba[0] = 0x32;
			int numClauses = logicExpression.size() - 1;
			if (numClauses > 255) {
				log.error("formatID 3 can only handle up to 255 non-disjunctive clauses.");
				return ba;
			}
			ba[1] = (byte) numClauses;
			cur_pos = this.getHeaderLength();
			break;
		default:
			log.error("formatID " + formatID + " not recognized, unable to correctly convert to byte array.");
			return ba;
		}
		cur_pos = this.getHeaderLength();
		Iterator<LabelClause> lA = logicExpression.iterator();
		while (lA.hasNext()) {
			clause_ba = lA.next().toByteArray();
			System.arraycopy(clause_ba, 0, ba, cur_pos, clause_ba.length);
			cur_pos += clause_ba.length;
		}
		return ba;
	}
		
	// Returns the number of shorts in the logicExpression
	private int size() {
		int cnt = 0;
		Iterator<LabelClause> lA = logicExpression.iterator();
		while (lA.hasNext()) {
			cnt += lA.next().size();
		}
		return cnt;
	}
	
	public String toString() {
		short[] sa;
		String s = this.getType() + "[";
		Iterator<LabelClause> lA = logicExpression.iterator();

		while (lA.hasNext()) {
			s += "(";
			sa = lA.next().toShortArray();
			if (sa.length > 0) {
				for (int j=0; j<sa.length-1; j++) {
					s += sa[j];
					s += ",";
				}
				s += sa[sa.length-1];
			}
			s += ")";
		}
		s += "]";
		return s;

	}

	public boolean equals(Object obj) {
		LabelExpression otherLe = (LabelExpression)obj;
		return this.toString().equals(otherLe.toString());
	}
	
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	public LabelClause getSuperSet(LabelClause lc) {
		Iterator<LabelClause> iter = logicExpression.iterator();
		LabelClause sub;
		while (iter.hasNext()) {
			sub = iter.next();
			if (lc.subsetOf(sub))
				return sub;
			if (sub.subsetOf(lc))
				return lc;
		}
		return null;
	}
}
