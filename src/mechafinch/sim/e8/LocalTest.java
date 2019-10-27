package mechafinch.sim.e8;

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
		
		insert(new int[] {5, 12, 2, 15, 37}, RAM);
		insert(new int[] {0xfe30, 0x32f5, 0x66E2, 0xa4d7}, ROM);
		
		System.out.println(Integer.toString(0xA8B2, 2));
		System.out.println(Integer.toString(0xA8B2, 2).matches("[10]+"));
		
		E8Simulator e8 = new E8Simulator();//new E8Simulator(RAM, ROM, regs, 0x3FF, Integer.toString(0xA8B2, 2));
		
		System.out.println("Instruction: " + e8.getInstruction() +
						 "\nInstruction Pointer: " + e8.getIP() +
						 "\nRAM: " + e8.getRAMState().length + " " + hexString(e8.getRAMState()) + //show length too
						 "\nROM: " + e8.getROM().length + " " + hexString(e8.getROM()) +
						 "\nRegisters: " + e8.getRegisterState().length + " " + hexString(e8.getRegisterState()));
	}
	
	static void insert(int[] source, int[] destination) {
		for(int i = 0; i < source.length && i < destination.length; i++) destination[i] = source[i];
	}
	
	static String hexString(int[] bytes) {
		String s = Integer.toHexString(bytes[0]);
		
		for(int i = 1; i < bytes.length; i++) s += ", " + Integer.toHexString(bytes[i]);
		
		return s;
	}
	
	static String hexString(short[] bytes) {
		String s = Integer.toHexString(bytes[0]);
		
		for(int i = 1; i < bytes.length; i++) s += ", " + Integer.toHexString(bytes[i]);
		
		return s;
	}
}
