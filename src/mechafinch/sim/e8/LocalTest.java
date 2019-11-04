package mechafinch.sim.e8;

import java.util.ArrayDeque;

import mechafinch.sim.test.TestUtil;

/**
 * A testing class with access to protected methods
 * 
 * @author Alex Pickering
 */
public class LocalTest {
	
	public static void main(String[] args) {
		int[] RAM = new int[256];
		int[] ROM = new int[1024];
		int[] regs = {0x3F, 0xFF, 0x9A, 0x02};
		
		TestUtil.insert(new int[] {5, 12, 2, 15, 37}, RAM);
		TestUtil.insert(new int[] {0xfe30, 0x32f5, 0x66E2, 0xa4d7}, ROM);
		
		System.out.println(Integer.toString(0xA8B2, 2));
		System.out.println(Integer.toString(0xA8B2, 2).matches("[10]+"));
		
		E8Simulator e8 = new E8Simulator(RAM, ROM, regs, new ArrayDeque<Integer>(), new ArrayDeque<Integer>(), 0x3FF, Integer.toString(0xA8B2, 2), false);//new E8Simulator(RAM, ROM, regs, 0x3FF, Integer.toString(0xA8B2, 2));
		
		System.out.println("Instruction: " + e8.getInstruction() +
						 "\nInstruction Pointer: " + e8.getIP() +
						 "\nRAM: " + e8.getRAMState().length + " " + TestUtil.hexString(e8.getRAMState(), 8) + //show length too
						 "\nROM: " + e8.getROM().length + " " + TestUtil.hexString(e8.getROM(), 10) +
						 "\nRegisters: " + e8.getRegisterState().length + " " + TestUtil.hexString(e8.getRegisterState(), 8) +
						 "\nCarry: " + e8.getCarryFlag());
	}
}
