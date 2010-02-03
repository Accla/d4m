package cloudbase.core.security;

import java.util.List;

import org.apache.log4j.Logger;


public class LabelConversions {

	static Logger log = Logger.getLogger(LabelConversions.class.getName());	

	
	// Converts the byte array representation (as stored in the Key) of a LabelClause to a short array.
	// WARNING: Do not use this to create a short array of the entire LabelExpression; 
	//			this only creates a short array of a single LabelClause.
	protected static short[] clauseBytesToShorts(byte[] ba) {
		short[] sa = new short[ba.length >> 1];
		byte msb, lsb;
		for (int i=0; i<ba.length; i+=2) {
			msb = ba[i];
			lsb = ba[i+1];
			short s = (short) ( ((msb << 8) & 0x7F00) + (lsb & 0x00FF) );
			sa[i >> 1] = s;
		}
		return sa;
	}
	
	// Converts a short array representing a LabelClause to its byte array representation (as stored in the Key).
	// WARNING: Do not use this to create a byte array of the entire LabelExpression; 
	//			this only creates a byte array of a single LabelClause.
	protected static byte[] clauseShortsToBytes(short[] sa) {
		byte[] ba = new byte[sa.length << 1];
		short tmp;
		int i2;
		if (sa.length > 0) {
			for (int i=0; i<sa.length-1; i++) {
				i2 = i << 1; 
				tmp = sa[i];
				ba[i2] = (byte) ( (tmp & 0xFF00) >> 8); //msb
				ba[i2+1] = (byte) (tmp & 0x00FF); //lsb
			}
			//for last short value, set first bit to 1 (marking end of clause)
			i2 = (sa.length - 1) << 1;
			tmp = sa[sa.length - 1];
			ba[i2] = (byte) ( ((tmp & 0xFF00) >> 8) + 0x80); //msb
			ba[i2+1] = (byte) (tmp & 0x00FF); //lsb
		}
		return ba;
	}

	// Converts a string representation of a LabelExpression (i.e. "CNF[(9)(104)]")
	// to a byte array that can be stored in the Key.
	public static byte[] expressionStringToBytes(String leString) throws LabelExpressionFormatException {
		
		try {
			
			if (leString.equals("CNF[]") || leString.equals("CNF[()]")) { // empty labels
				return new byte[0];
			}
			
			if (leString.length() < 5) {
				throw new LabelExpressionFormatException();
			}
			String type = leString.substring(0, 3);
			int formatID = -1;

			if (leString.charAt(3) != '[' || leString.charAt(leString.length()-1) != ']'
				|| leString.charAt(4) != '(' || leString.charAt(leString.length()-2) != ')') {
				throw new LabelExpressionFormatException();
			}
			
			if ( (!type.equals("CNF") && !type.equals("DNF"))) {
				throw new LabelExpressionFormatException();
			}
			
			if(leString.length() > 7 && type.equals("CNF")) {
				if (!leString.substring(5, leString.length()-3).contains(",")) {
					formatID = 2; //CNF with no disjunctions
				}
				else {
					int lastClauseStartingIndex = -1;
					for (int i=leString.length()-3; i > 3; i--) {
						if (leString.charAt(i) == '(') {
							lastClauseStartingIndex = i;
							break;
						}
					}
					if (lastClauseStartingIndex == -1) {
						throw new LabelExpressionFormatException();
					}
					if (!leString.substring(4, lastClauseStartingIndex).contains(",")) {
						formatID = 3; //CNF where only last clause has disjunctions
					}
					else {
						formatID = 0; //general CNF
					}
				}
			}	
			else if (type.equals("CNF")) {
				formatID = 0; //general CNF
			}
			else if (type.equals("DNF")) {
				formatID = 1; //general DNF
			}
			
			LabelExpression le = null;
			
			// general CNF or DNF
			if ( formatID == 0 || formatID == 1 ) {
				le = new LabelExpression();
				String data = leString.substring(4,leString.length()-1);
				String[] clauses = data.split("\\)");
				for (int i=0; i<clauses.length; i++) {
					if (clauses[i].length() <= 1 || clauses[i].charAt(0) != '(') {
						throw new LabelExpressionFormatException();
					}
					String[] literals = clauses[i].substring(1).split(",");
					short[] sa = new short[literals.length];
					for (int j=0; j<literals.length; j++) {
						sa[j]  = Short.parseShort(literals[j]);
					}
					le.addClause(sa);
				}
				if (type.equals("CNF")) {
					le.setFormatID(0);
				}
				else if (type.equals("DNF")) {
					le.setFormatID(1);
				}
				return le.toByteArray();
			}
			// CNF with no disjunctions
			else if (formatID == 2) {		
				String data = leString.substring(4,leString.length()-1);
				String[] clauses = data.split("\\)");
				short[] sa = new short[clauses.length];
				for (int i=0; i<clauses.length; i++) {
					if (clauses[i].length() <= 1 || clauses[i].charAt(0) != '(') {
						throw new LabelExpressionFormatException();
					}
					String literal = clauses[i].substring(1);
					sa[i]  = Short.parseShort(literal);
				}
				le = new LabelExpression(sa);
				return le.toByteArray();
			}
			// CNF where only last clause has disjunctions
			else if (formatID == 3) {	
				String data = leString.substring(4,leString.length()-1);
				String[] clauses = data.split("\\)");
				short[] sa = new short[clauses.length - 1];
				for (int i=0; i<clauses.length-1; i++) {
					if (clauses[i].length() <= 1 || clauses[i].charAt(0) != '(') {
						throw new LabelExpressionFormatException();
					}
					String literal = clauses[i].substring(1);
					sa[i]  = Short.parseShort(literal);
				}
				String lastClause = clauses[clauses.length-1];
				if (lastClause.length() <= 1 || lastClause.charAt(0) != '(') {
					throw new LabelExpressionFormatException();
				}
				String[] literals = lastClause.substring(1).split(",");
				short[] sa2 = new short[literals.length];
				for (int i=0; i<literals.length; i++) {
					sa2[i]  = Short.parseShort(literals[i]);
				}
				le = new LabelExpression(sa,sa2);
				return le.toByteArray();
			}	
			else {
				throw new LabelExpressionFormatException();
			}
			
		}
		catch(NumberFormatException e) {
			throw new LabelExpressionFormatException();
		}
		catch(StringIndexOutOfBoundsException e) {
			throw new LabelExpressionFormatException();
		}
		catch(Exception e) {
			throw new LabelExpressionFormatException();
		}
	}
	
	
	// Convert a short array representation of a user's authorizations
	// into the appropriate byte array used by CellLevelAuthenticator.
	public static byte[] formatAuthorizations(short[] sa) {
		byte[] ba = new byte[sa.length << 1];
		short tmp;
		int i2;
		for (int i=0; i<sa.length; i++) {
			i2 = i << 1; 
			tmp = sa[i];
			ba[i2] = (byte) ( (tmp & 0xFF00) >> 8); //msb
			ba[i2+1] = (byte) (tmp & 0x00FF); //lsb
		}
		return ba;

	}	
	
	
	public static byte[] formatAuthorizations(List<List<Short>> sa) {
	    if (sa == null || sa.size() < 1) {
	        return new byte[0];
	    }
	    short[] shorts = new short[sa.size()];
	    for (int i = 0; i < sa.get(0).size(); i++) {
	        shorts[i] = sa.get(0).get(i);
	    }
	    return formatAuthorizations(shorts);
	}
}
