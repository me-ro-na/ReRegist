package egovframework.iChat.common.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class SequenceUtil {

	private static String keyValue = "initial";
	public static String getDBKey() {
		Calendar cal = Calendar.getInstance();
				
		String logSeq = Long.toString(System.currentTimeMillis()).substring(6)+ getRandomVlaue();
		
		if(keyValue.equals(logSeq)) {
			while(!keyValue.equals(logSeq)) {
				logSeq = Long.toString(System.currentTimeMillis()).substring(6)+ getRandomVlaue();
			}
		}
		keyValue = logSeq;
		
		return keyValue;
	}
	
	private static int getRandomVlaue() {
		//Random ran = new Random();
		//ran.setSeed(new Date().getTime());
		int randomVal = 0;
		do  {
			Random numGen;
			try {
				numGen = SecureRandom.getInstance("SHA1PRNG");
				randomVal = numGen.nextInt(1000) +1;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("error:NoSuchAlgorithmException");
			}
			
			//randomVal = ran.nextInt(1000);
		} while (randomVal < 100);
		return randomVal;
	}
}
