package net.jeebiz.boot.api;

import net.jeebiz.boot.api.utils.RandomString;

public class RandomString_Test {

	static RandomString random = new RandomString(8);
	
	public static void main(String[] args) throws Exception {
		System.out.println(random.nextString());
		for (int i = 0; i < 50; i++) {
			System.out.println(random.nextNumberString());
		}
	}
	
	
}
