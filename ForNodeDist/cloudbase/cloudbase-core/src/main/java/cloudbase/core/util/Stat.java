package cloudbase.core.util;

public class Stat {
	
	long max = Long.MIN_VALUE;
	long min = Long.MAX_VALUE;
	long sum = 0;
	int count = 0;
	
	public void addStat(long stat){
		if(stat > max) max = stat;
		if(stat < min) min = stat;
		
		sum += stat;
		count++;
	}
	
	public long getMin(){
		return min;
	}
	
	public long getMax(){
		return max;
	}
	
	public double getAverage(){
		return ((double)sum)/count;
	}
	
	public String toString(){
		return String.format("min = %,d max = %,d avg = %,.2f cnt = %,d", getMin(), getMax(), getAverage(), count);
	}

	public void clear() {
		sum = 0;
		count = 0;
	}
}
