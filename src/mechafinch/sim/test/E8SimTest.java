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
			  romContents = new int[] {0b001000_00_11111111};
		TestUtil.insert(romContents, rom);
		
		System.out.println(TestUtil.hexString(rom));
		
		E8Simulator testSim = new E8Simulator(rom);
		
		//Execute until test inst.
		for(int i = 0; i < romContents.length - 1; i++) {
			testSim.step();
		}
		
		//Dump state, execute, dump again
		dumpState(testSim);
		testSim.step();
		dumpState(testSim);
	}
	
	/**
	 * Dump the state of the VM
	 * 
	 * @param sim The isntance to dump
	 */
	static void dumpState(E8Simulator sim) {
		System.out.println("\nInstruction: " + sim.getInstruction() +
						   "\nInstruction Pointer: " + sim.getIP() +
						   "\nRegisters: " + TestUtil.hexString(sim.getRegisterState()) +
						   "\nRAM: " + TestUtil.hexString(sim.getRAMState()) +
						   "\n");
	}
}
