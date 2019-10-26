package mechafinch.sim.test;

import java.util.regex.Pattern;

import mechafinch.sim.e8.E8Simulator;

/**
 * A place to test the simulator in a standard environment
 * 
 * @author Alex Pickering
 */
public class E8SimTest {
	public static void main(String[] args) {
		String s1 = "001010101010";
		String s2 = "10010101h";
		
		System.out.println(s2.matches("[10]+"));
	}
}
