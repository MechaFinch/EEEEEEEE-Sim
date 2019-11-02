package mechafinch.sim.test;

import java.util.ArrayDeque;

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
						   "\nRegisters: " + hexString(sim.getRegisterState()) +
						   "\nRAM: " + hexString(sim.getRAMState()) +
						   "\nData Stack: " + hexString(sim.getDataStack()) +
						   "\nCall Stack: " + hexString(sim.getCallStack()) +
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
	
	/**
	 * Construct a comma & space separated hex string of an ArrayDeque
	 * 
	 * @param arr The ArrayDeque to convert
	 * @return A comma and space separated string of the ArrayDeque
	 */
	public static String hexString(ArrayDeque<Integer> arr) {
		if(arr.size() == 0) return "";
		
		String s = Integer.toHexString(arr.pop());
		
		while(arr.size() > 0) s += ", " + Integer.toHexString(arr.pop());
		
		return s;
	}
}
