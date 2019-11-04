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
			  romContents = new int[] {	//Test 0x033d - 0x02e6 = 0x0057
				0b00100000_00000011,	//LD A, $03
				0b00100001_00111101,	//LD B, $3D
				0b00100010_00000010,	//LD C, $02
				0b00100011_11100110,	//LD D, $E6
				0b01000100_11010011,	//SUB D, B, D
				0b01000110_10000010,	//SUBC C, A, C	Regs: $03 $3D $00 $57
		};
		
		TestUtil.insert(romContents, rom);
		
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
