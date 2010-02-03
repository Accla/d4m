package cloudbase.core.security;

import org.apache.log4j.Logger;

public class CellLevelAuthenticator {

	static Logger log = Logger.getLogger(CellLevelAuthenticator.class.getName());	

	// returns true iff the user's authorizations satisfies the security logic encoded in labels
	public static boolean authorize(byte[] authorizations, byte[] labels)
	{
		try {

			if (labels.length == 0)
				return true;

			byte firstByte = labels[0];
			int formatID = (firstByte & 0xF0) >> 4;
			int headerLen = firstByte & 0x0F;
			boolean authenticated;

			// CNF where only last clause has disjunctions
			if (formatID == 3) {	
				byte secondByte = labels[1]; 
				// conjuctionBytes equals 2 x (number of clauses - 1)
				// since each label = 2 bytes and the clauses with disjunctions is handled later;
				// the RHS expression formats the byte into an integer between 0-255, inclusive
				int conjunctionBytes = (secondByte & 0xFF) << 1;
				for (int i=headerLen; i<headerLen + conjunctionBytes; i+=2) {
					//search for labels[i] labels[i+1] in authorizations
					authenticated = false;
					for (int j=0; j<authorizations.length; j+=2) {
						if (authorizations[j] == (labels[i] & 0x7F) && authorizations[j+1] == labels[i+1]) {
							authenticated=true;
							break;
						}
					}
					if (authenticated == false) {
						return false;
					}
				}

				// disjunctionBytes equals 2 x (number of labels in clause with disjunctions)
				// int disjunctionBytes = (secondByte & 0x0F) << 1;
				for (int i=headerLen+conjunctionBytes; i<labels.length; i+=2) {
					//search for labels[i] labels[i+1] in authorizations
					for (int j=0; j<authorizations.length; j+=2) {
						if (authorizations[j] == (labels[i] & 0x7F) && authorizations[j+1] == labels[i+1]) {
							return true;						
						}
					}
				}
				// authorizations has none of the labels in the disjunction clause
				return false;

			}

			// CNF with no disjunctions (authorizations must contains EVERY label)
			else if (formatID == 2) {	
				for (int i=headerLen; i<labels.length; i+=2) {
					//search for labels[i] labels[i+1] in authorizations
					authenticated = false;
					for (int j=0; j<authorizations.length; j+=2) {
						if (authorizations[j] == (labels[i] & 0x7F) && authorizations[j+1] == labels[i+1]) {
							authenticated=true;
							break;
						}
					}
					if (authenticated == false) {
						return false;
					}
				}
				return true;
			}


			else if (formatID == 1) {	// general DNF expression

				boolean clauseSat = true;  // is the current clause satisfied so far?
				boolean clauseEnd; // is this the last label in the current clause?

				for (int i=headerLen; i<labels.length; i+=2) {

					clauseEnd = (labels[i] < 0);

					if (clauseSat) {
						clauseSat = false;
						//search for labels[i] labels[i+1] in authorizations
						for (int j=0; j<authorizations.length; j+=2) {
							if (authorizations[j] == (labels[i] & 0x7F) && authorizations[j+1] == labels[i+1]) {
								clauseSat=true;
								break;
							}
						}

					}

					if (clauseEnd ) {
						if (clauseSat) {
							return true;
						}
						else {
							clauseSat = true;
						}
					}

				}
				return false;

			}


			else if (formatID == 0) {  // general CNF expression
				boolean clauseSat = false; // is the current clause satisfied?
				boolean clauseEnd; // is this the last label in the current clause?


				for (int i=headerLen; i<labels.length; i+=2) {

					clauseEnd = (labels[i] < 0);

					if (!clauseSat) {
						//search for labels[i] labels[i+1] in authorizations
						for (int j=0; j<authorizations.length; j+=2) {
							if (authorizations[j] == (labels[i] & 0x7F) && authorizations[j+1] == labels[i+1]) {
								clauseSat=true;
								break;
							}
						}
					}

					if (clauseEnd) {
						if (clauseSat) {
							clauseSat = false;
							continue;
						}
						else {
							return false;
						}
					}
				}
				return true;

			}

			else {	// formatID not implemented
				log.error("Unrecognized format ID.");
				return false; 
			}
		}

		catch (ArrayIndexOutOfBoundsException e) {
			log.error("Incorrectly formatted labels.");
			e.printStackTrace();
			return false;
		}

		catch (NullPointerException e) {	
			log.error("Incorrectly formatted labels.");
			e.printStackTrace();
			return false;	
		}


	}


}
