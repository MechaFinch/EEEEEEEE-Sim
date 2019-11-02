package mechafinch.sim.test;

import mechafinch.sim.e8.E8Simulator;

/**
 * A utility class for the two testing classes so methods dont have to be copy-pasted
 * 
 * @author Alex Pickering
 */
public class TestUtil {
	/**
	 * Dump the state of the VM
	 * 
	 * @param sim The isntance to dump
	 */
	public static void dumpState(E8Simulator sim) {
		System.out.println("\nInstruction: " + sim.getInstruction() +
						   "\nInstruction Pointer: " + sim.getIP() +
						   "\nRegisters: " + TestUtil.hexString(sim.getRegisterState()) +
						   "\nRAM: " + TestUtil.hexString(sim.getRAMState()) +
						   "\nCarry Flag: " + sim.getCarryFlag() +
						   "\nLoaded Locations: " + sim.getLoadedLocations() +
						   "\nStored Location: " + sim.getStoredLocation() +
						   "\n");
	}
	
	/**
	 * Insert an array of integers at the start of a larger one
	 * 
	 * @param source The array to insert
	 * @param destination The array to insert into
	 * @param length The number of values from the source to copy (omit to insert all of source)
	 */
	public static void insert(int[] source, int[] destination, int length) {
		System.arraycopy(source, 0, destination, 0, length);
	}
	
	/**
	 * Insert an array of integers at the start of a larger one
	 * 
	 * @param source The array to insert
	 * @param destination The array to insert into
	 */
	public static void insert(int[] source, int[] destination) {
		insert(source, destination, source.length);
	}
	
	/**
	 * Construct a comma & space separated hex string of an array
	 * 
	 * @param arr The array to convert
	 * @return A comma & space separated string of the array
	 */
	public static String hexString(int[] arr) {
		String s = Integer.toHexString(arr[0]);
		
		for(int i = 1; i < arr.length; i++) s += ", " + Integer.toHexString(arr[i]);
		
		return s;
	}
	
	/**
	 * Construct a comma & space separated hex string of an array
	 * 
	 * @param arr The array to convert
	 * @return A comma & space separated string of the array
	 */
	public static String hexString(short[] arr) {
		String s = Integer.toHexString(arr[0]);
		
		for(int i = 1; i < arr.length; i++) s += ", " + Integer.toHexString(arr[i]);
		
		return s;
	}
}
