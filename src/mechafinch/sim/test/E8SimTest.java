package mechafinch.sim.test;

import mechafinch.sim.e8.E8Simulator;

/**
 * A place to test the simulator in a standard environment
 * 
 * @author Alex Pickering
 */
public class E8SimTest {
	public static void main(String[] args) {
		int[] rom = new int[1024],
			  romContents = new int[] {
				 0b00100000_00000011,	//LD A, $03
				 0b01000001_01001110	//ADD B, A, $E
		};
		
		TestUtil.insert(romContents, rom);
		
		System.out.println(TestUtil.hexString(rom));
		
		E8Simulator testSim = new E8Simulator(rom);
		
		//Execute until test inst.
		for(int i = 0; i < romContents.length - 1; i++) {
			testSim.step();
		}
		
		//Dump state, execute, dump again
		TestUtil.dumpState(testSim);
		testSim.step();
		TestUtil.dumpState(testSim);
	}
}
