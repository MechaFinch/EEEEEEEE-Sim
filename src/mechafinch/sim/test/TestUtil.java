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
		System.out.println("\nInstruction: " + sim.instruction +
						   "\nInstruction Pointer: " + String.format("%3s", Integer.toHexString(sim.instructionPointer)).replace(' ', '0') +
						   "\nRegisters: " + hexString(sim.registers, sim.dataLength).toUpperCase() +
						  // "\nRAM: " + hexString(sim.getRAMState(), 16).toUpperCase() +
						   "\nData Stack: " + hexString(sim.dataStack, sim.dataLength).toUpperCase() +
						   "\nCall Stack: " + hexString(sim.callStack, 10).toUpperCase() +
						   "\nCarry Flag: " + sim.cFlag +
						   "\nLoaded Locations: " + sim.getLoadedLocations() +
						   "\nStored Location: " + sim.getStoredLocation() +
						   "");
	}
	
	/**
	 * Dumps a segment of memory
	 * 
	 * @param sim The instance of the simulator
	 * @param start Start address
	 * @param end End address
	 */
	public static void dumpSegment(E8Simulator sim, int start, int end) {
		
		// Get from memory
		int[] memSeg = new int[end - start]; 
		System.arraycopy(sim.RAM, start, memSeg, 0, end - start);
		
		// Print it
		System.out.print(hexString(memSeg, sim.dataLength).toUpperCase());
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
	 * @param numBits The number of bits per value
	 * @return A comma & space separated string of the array
	 */
	public static String hexString(int[] arr, int numBits) {
		String s = "";
		
		for(int i = 0; i < arr.length; i++) s += ", " + String.format("%" + (numBits / 4) + "s", Integer.toHexString(arr[i])).replace(' ', '0');
		
		return s.substring(2);
	}
	
	/**
	 * Construct a comma & space separated hex string of an ArrayDeque
	 * 
	 * @param arr The ArrayDeque to convert
	 * @param numBits The number of bits for each value
	 * @return A comma and space separated string of the ArrayDeque
	 */
	public static String hexString(ArrayDeque<Integer> arr, int numBits) {
		if(arr.size() == 0) return "";
		
		String s = "";
		
		while(arr.size() > 0) s += ", " + String.format("%" + (numBits / 4) + "s", Integer.toHexString(arr.pop())).replace(' ', '0');
		
		return s.substring(2);
	}
}
