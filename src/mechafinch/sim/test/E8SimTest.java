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
				0b01101000_00000110, // JSR 0x006
				0b00100000_00000110, // LDA 0x006
				0b01101100_00000010, // JSR [A + 0x02]
				0b11100000_00000000, // exit
				0b0,
				0b0,
				0b00100001_00000101, // LD B, 0x05
				0b01110000_00000000, // RET
				0b00100010_00000111, // LD C, 0x07
				0b01110000_00000000, // RET
		};
		
		TestUtil.insert(romContents, rom);
		
		E8Simulator testSim = new E8Simulator(rom);
		
		//Execute until test inst.
		for(int i = 0; i < romContents.length - 1; i++) {
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
