package cloudbase.core.tabletserver;

public class TabletTimer{

	//TODO: use enums instead
	public static final int MAJOR_FINISHED = 0;
	public static final int SPLIT_FINISHED = 1;
	public static final int MIGRATE_FINISHED = 2;
	public static final int MINOR_FINISHED = 3;
	
	int statusMajor = 0;
	int statusSplit = 0;
	int statusMigrate = 0;
	int statusMinor = 0;
	
	double majorElapsed = 0;
	int majorNum = 0;
	long majorCount = 0;
	double majorSumDev = 0;
	double majorQueueTime = 0;
	double majorQueueSumDev = 0;
	double splitElapsed = 0;
	int splitNum = 0;
	double splitSumDev = 0;
	double migrateElapsed = 0;
	int migrateNum = 0;
	double migrateSumDev = 0;
	double migrateQueueTime = 0;
	double migrateQueueSumDev = 0;
	double minorElapsed = 0;
	int minorNum = 0;
	long minorCount = 0;
	double minorSumDev = 0;
	double minorQueueTime = 0;
	double minorQueueSumDev = 0;
	
	int minorFail = 0;
	int majorFail = 0;
	int migrateFail = 0;
	int splitFail = 0;


	public void updateTime(int operation, long queued, long start, long count, boolean failed) {
	try{
		if(failed) {
			if(operation==MAJOR_FINISHED) {
				majorFail++;
				statusMajor--;
			}
			else if(operation==MINOR_FINISHED) {
				minorFail++;
				statusMinor--;
			}
			else if(operation==SPLIT_FINISHED) {
				splitFail++;
				statusSplit--;
			}
			else if(operation==MIGRATE_FINISHED) {
				migrateFail++;
				statusMigrate--;
			}
			else {
				//TODO: log error				
			}
		}
		else {
		double t = (System.currentTimeMillis()-start)/1000.0;
		double q = (start-queued)/1000.0;
				
		if(operation==MAJOR_FINISHED) {
			statusMajor--;
			majorCount += count;
			majorNum++;
			majorElapsed += t;			
			majorQueueTime += q;
			majorSumDev += Math.pow(t, 2);
			majorQueueSumDev += Math.pow(q, 2);	
		}		
		else if(operation==MIGRATE_FINISHED) {
			statusMigrate--;
			migrateNum++;
			migrateElapsed += t;
			migrateQueueTime += q;
			migrateSumDev += Math.pow(t, 2);
			migrateQueueSumDev += Math.pow(q, 2);			
		}
		else if(operation==MINOR_FINISHED) {
			statusMinor--;
			minorCount += count;
			minorNum++;
			minorElapsed += t;
			minorQueueTime += q;
			minorSumDev += Math.pow(t, 2);
			minorQueueSumDev += Math.pow(q, 2);	
		}
		else {
			//TODO: log an error here, unknown message from tablet server or tablet			
		}
		if(majorElapsed < 0 || minorElapsed < 0 || splitElapsed < 0 || migrateElapsed < 0 || majorSumDev < 0 || migrateSumDev < 0 || minorSumDev < 0 || majorQueueSumDev < 0 ||
				migrateQueueSumDev < 0 || minorQueueSumDev < 0 || splitSumDev < 0 || majorQueueTime < 0 || minorQueueTime < 0 || migrateQueueTime < 0)
			resetTimes();
	}}
	catch(Exception E) {
		resetTimes();
	}
		
	}
	
	public void updateTime(int operation, long start, long count, boolean failed) {
		try{
			if(failed) {
				if(operation==MINOR_FINISHED) {
					minorFail++;
					statusMinor--;
				}
				else if(operation==SPLIT_FINISHED) {
					splitFail++;
					statusSplit--;
				}
				else {
					//TODO: log error					
				}
			}
			else {
			double t = (System.currentTimeMillis()-start)/1000.0;
						
			if(operation==SPLIT_FINISHED) {
				statusSplit--;
				splitNum++;
				splitElapsed += t;
				splitSumDev += Math.pow(t, 2);				
			}			
			else if(operation==MINOR_FINISHED) {
				statusMinor--;
				minorNum++;
				minorCount+=count;
				minorElapsed += t;				
				minorSumDev += Math.pow(t, 2);				
			}
			else {
				//TODO: log an error here, unknown message from tablet server or tablet				
			}
			if(majorElapsed < 0 || minorElapsed < 0 || splitElapsed < 0 || migrateElapsed < 0 || majorSumDev < 0 || migrateSumDev < 0 || minorSumDev < 0 || majorQueueSumDev < 0 ||
					migrateQueueSumDev < 0 || minorQueueSumDev < 0 || splitSumDev < 0 || majorQueueTime < 0 || minorQueueTime < 0 || migrateQueueTime < 0)
				resetTimes();
		}}	
		catch(Exception E) {
			resetTimes();
		}
			
	}
	public void saveMinorTimes(int n, double e, double q, double s, double d, long c, int f) {
		minorNum += n;
		minorElapsed += e;
		minorQueueTime += q;
		minorSumDev += s;
		minorQueueSumDev += d;
		minorCount+=c;
		minorFail += f;
	}
	public void saveMajorTimes(int n, double e, double q, double s, double d, long c, int f) {
		majorNum += n;
		majorElapsed += e;
		majorQueueTime += q;
		majorSumDev += s;
		majorQueueSumDev += d;
		majorCount+=c;
		majorFail += f;
	}
	
	public void resetTimes() {
		 majorElapsed = 0;
		 majorNum = 0;
		 majorSumDev = 0;
		 majorQueueTime = 0;
		 majorQueueSumDev = 0;
		 splitElapsed = 0;
		 splitNum = 0;
		 splitSumDev = 0;
		 migrateElapsed = 0;
		 migrateNum = 0;
		 migrateSumDev = 0;
		 migrateQueueTime = 0;
		 migrateQueueSumDev = 0;
		 minorElapsed = 0;
		 minorNum = 0;
		 minorSumDev = 0;
		 minorQueueTime = 0;
		 minorQueueSumDev = 0;
	}
	
	
}