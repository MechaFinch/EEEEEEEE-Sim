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
				0b00100001_00100011, // LD B, 0x23
				0b00100010_01011100, // LD C, 0x5C
				0b10000000_01000100, // BEQ A, B, +0x04
				0b10000000_10000101, // BEQ A, C, +0x05
				0b01100000_00000111, // JMP 0x007
				0b00100011_11111111, // LD D, 0xFF
				0b11100000_00000000, // exit
				0b0,
				0b10000100_10000010, // BEQ A, C, -0x02
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
