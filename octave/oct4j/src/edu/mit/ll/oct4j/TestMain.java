package edu.mit.ll.oct4j;

import java.lang.reflect.Method;
import java.util.Random;

public class TestMain {
	public static String FILLER="-=+X+=- octave4j filler -=+X+=- "+Long.toString(System.currentTimeMillis())+" -=+X+=-";
	
	public TestMain () {
		
	}
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Random random = new Random();
		int r1 =random.nextInt(1 << 23);
		int r2 = random.nextInt(1 << 23);
		String tag = String.format("%06x%06x", r1, r2);
		System.out.println("tag="+tag);
		System.out.println("r1="+String.format("%06x",r1));
		System.out.println("r2="+r2);
		String tmp =String.format("javaoctave_%1$s_eval", tag);
		System.out.println("tmp = "+tmp);
		System.out.println("===================================");
		System.out.println(TestMain.FILLER);
		Thread.sleep(2000);
		System.out.println(TestMain.FILLER);
		Thread.sleep(2000);
		System.out.println(TestMain.FILLER);
		
		Thread.sleep(2000);
		System.out.println(TestMain.filler());
		Thread.sleep(2000);
		System.out.println(TestMain.filler());
		TestMain tm  = new TestMain();
		Method [] method = TestMain.class.getDeclaredMethods();
		for(int i = 0; i < method.length; i++) {
			System.out.println(i+" -  "+method[i].getName());
			
		}
	}
	
	public static void boomethod() {
		System.out.println("BOO");
	}
	public static void foomethod() {
		System.out.println("FOO");
	}

	public static String filler() {
		return "-=+X+=- octave4j filler -=+X+=- "+Long.toString(System.currentTimeMillis())+" -=+X+=-";
	}
}
