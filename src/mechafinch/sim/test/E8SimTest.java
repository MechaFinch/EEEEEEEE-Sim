package mechafinch.sim.test;

import java.io.IOException;

import mechafinch.sim.e8.E8Simulator;

/**
 * A place to test the simulator in a standard environment
 * 
 * @author Alex Pickering
 */
public class E8SimTest {
	public static void main(String[] args) throws IOException {
		int[] rom = new int[1024],
			  romContents = new int[] {	//Test
				0b00100000_01011100, // LD A, 0x5C
				0b10011001_00000011, // BNZ A, +0x03
				0b00100011_11111111, // LD D, 0xFF
				0b11100000_00000000, // exit
				0b10011100_00000001, // BZ A, -0x02
				0b01100000_00000011, // JMP 0x003
		};
		
		TestUtil.insert(romContents, rom);
		
		E8Simulator testSim = new E8Simulator(rom);
		
		//Execute until test inst.
		for(int i = 0; i < romContents.length; i++) {
			if(!testSim.step()) {
				break;
			}
		}
		
		//Dump state, execute, dump again
		//TestUtil.dumpState(testSim);
		//testSim.step();
		TestUtil.dumpState(testSim);
	}
}
