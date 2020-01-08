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
				0b00100000_11111101,	//LD A, -3
				0b01100100_00001000,	//JMP [A + $08]
				0b00100000_00001111,	//LD A, 15 (0F)
				0b00100001_11111101,	//LD B, -3 (FD)
				0b01000100_00000001,	//SUB A, A, B
				0b11100000_00010000,	//INT 10 (output A)
				0b11100000_00000000 // exit
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
