package mechafinch.sim.e8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayDeque;

/**
 * The class containing the simulator for E8
 * 
 * @author Alex Pickering
 */
public class E8Simulator {
	
	/*
	 * VM Objects
	 */
	private int[] RAM,		//RAM
				  registers;//Registers
	private int[] ROM;		//ROM
	
	private ArrayDeque<Integer> callStack;
	private ArrayDeque<Integer> dataStack;
	
	private String instruction;		//Current instruction (binary string)
	private Instructions iType;		//The type of the current instruction
	private int instructionPointer;	//The instruction pointer
	private boolean cFlag = false;	//The carry flag
	
	private BufferedReader inputStream;		//Inputstream used by interrupts
	private BufferedWriter outputStream;	//Outputstream used by interrupts
	
	private int MAX_VALUE,		//Maximum value based on number of bits
				ZERO_MASK,		//Bitmask for not complementing during XOR
				ADDRESS_MASK;	//Bitmask for the maximum value of a ROM address
	
	/*
	 * Public Constructors
	 */
	
	/**
	 * Full/STATE Constructor <br>
	 * Constructs an instance of the simulator to match a desired state
	 * 
	 * @param nRAM Initial RAM state
	 * @param nROM ROM
	 * @param nRegisters Initial register states
	 * @param dataLength Data length in bits
	 * @param nDataStack Initial data stack
	 * @param nCallStack Initial call stack
	 * @param nInstructionPointer Initial IP
	 * @param nInstruction Initial loaded instruction
	 * @param nInputStream The input stream used by interrupts
	 * @param nOutputStream The output stream used by interrupts
	 */
	public E8Simulator(int[] nRAM, int[] nROM, int[] nRegisters, int dataLength, ArrayDeque<Integer> nDataStack, ArrayDeque<Integer> nCallStack, int nInstructionPointer, String nInstruction, boolean nCFlag, InputStream nInputStream, PrintStream nOutputStream) {
		//Data size stuff
		MAX_VALUE = (int)(Math.pow(2, dataLength)) - 1;	//dataLength bits, all 1s
		ZERO_MASK = 0;
		ADDRESS_MASK = 0x3FF;
		
		//System.out.println(Integer.toHexString(MAX_VALUE));
		//System.out.println(Integer.toHexString(CARRY_MASK));
		
		//Sanitize inputs
		if(nRAM.length != (MAX_VALUE + 1)) throw new IllegalArgumentException("RAM size must be " + (MAX_VALUE + 1) + " data words");
		if(nROM.length != 1024) throw new IllegalArgumentException("ROM size must be 1024 short instructions");
		if(nRegisters.length != 4) throw new IllegalArgumentException("Register size must be 4 bytes");
		if(nInstructionPointer > 0x3FF || nInstructionPointer < 0) throw new IllegalArgumentException("Instruction pointer must be a 10-bit unsigned value");
		if(nInstruction.length() != 16 || !nInstruction.matches("[10]+")) throw new IllegalArgumentException("Instruction must be a 16-bit binary string");
		
		//Apply inputs
		RAM = nRAM;
		ROM = nROM;
		registers = nRegisters;
		callStack = nCallStack;
		dataStack = nDataStack;
		instructionPointer = nInstructionPointer;
		cFlag = nCFlag;
		inputStream = new BufferedReader(new InputStreamReader(nInputStream));
		outputStream = new BufferedWriter(new OutputStreamWriter(nOutputStream));
		
		updateInstruction();
	}
	
	/**
	 * RAM & ROM Constructor with streams and data length <br>
	 * Constructs a new simulator instance with the given RAM, ROM, streams, and data length
	 * 
	 * @param nRAM Initial RAM state
	 * @param nROM ROM
	 * @param dataLength Length of data words in bits
	 * @param nInputStream Input stream for interrupts
	 * @param nOutputStream Output stream for interrupts
	 */
	public E8Simulator(int[] nRAM, int[] nROM, int dataLength, InputStream nInputStream, PrintStream nOutputStream) {
		this(nRAM, nROM, new int[4], dataLength, new ArrayDeque<Integer>(), new ArrayDeque<Integer>(), 0, "0000000000000000", false, nInputStream, nOutputStream);
	}
	
	/**
	 * RAM & ROM Constructor with streams <br>
	 * Constructs a new simulator instance with the given RAM, ROM, and streams
	 * 
	 * @param nRam Initial RAM state
	 * @param nRom ROM
	 * @param nInputStream Input stream for interrupts
	 * @param nOutputStream Output stream for interrupts
	 */
	public E8Simulator(int[] nRAM, int[] nROM, InputStream nInputStream, PrintStream nOutputStream) {
		this(nRAM, nROM, new int[4], 8, new ArrayDeque<Integer>(), new ArrayDeque<Integer>(), 0, "0000000000000000", false, nInputStream, nOutputStream);
	}
	
	/**
	 * RAM & ROM Constructor with data length <br>
	 * Constructs a new simulator instance with the given RAM, ROM, and data length
	 * 
	 * @param nRAM Initial RAM state
	 * @param nROM ROM
	 * @param dataLength Data length in bits
	 */
	public E8Simulator(int[] nRAM, int[] nROM, int dataLength) {
		this(nRAM, nROM, new int[4], dataLength, new ArrayDeque<Integer>(), new ArrayDeque<Integer>(), 0, "0000000000000000", false, System.in, System.out);
	}
	
	/**
	 * RAM & ROM Constructor <br>
	 * Constructs a new simulator instance with the given RAM and ROM
	 * 
	 * @param nRAM Initial RAM state
	 * @param nROM ROM
	 */
	public E8Simulator(int[] nRAM, int[] nROM) {
		this(nRAM, nROM, new int[4], 8, new ArrayDeque<Integer>(), new ArrayDeque<Integer>(), 0, "0000000000000000", false, System.in, System.out);
	}
	
	/**
	 * ROM, streams, and data length constructor <br>
	 * Constructs a new simulator instance with the given ROM, empty RAM, and the given data length and streams
	 * 
	 * @param nROM ROM
	 * @param dataLength Data length in bits
	 * @param nInputStream Input stream for interrupts
	 * @param nOutputStream Output stream for interrupts
	 */
	public E8Simulator(int[] nROM, int dataLength, InputStream nInputStream, PrintStream nOutputStream) {
		this(new int[256], nROM, dataLength, nInputStream, nOutputStream);
	}
	
	/**
	 * ROM & Streams Constructor <br>
	 * Constructs a new simulator instance with the given ROM, empty RAM, and the given streams
	 * 
	 * @param nROM ROM
	 * @param nInputStream Input stream for interrupts
	 * @param nOutputStream Output stream for interrupts
	 */
	public E8Simulator(int[] nROM, InputStream nInputStream, PrintStream nOutputStream) {
		this(new int[256], nROM, nInputStream, nOutputStream);
	}
	
	/**
	 * ROM and data length constructor <br>
	 * Constructs a new simulator instance with the given ROM, empty RAM, and the given data length
	 * 
	 * @param nROM ROM
	 * @param dataLength Data length in bits
	 */
	public E8Simulator(int[] nROM, int dataLength) {
		this(new int[256], nROM, dataLength);
	}
	
	/**
	 * ROM Constructor <br>
	 * Constructs a new simulator instance with the given ROM and empty RAM
	 * 
	 * @param nROM ROM
	 */
	public E8Simulator(int[] nROM) {
		this(new int[256], nROM);
	}
	
	/*
	 * UI Getters
	 */
	public int[] getRAMState() { return RAM.clone(); }
	public int[] getRegisterState() { return registers.clone(); }
	public int[] getROM() { return ROM.clone(); }
	public ArrayDeque<Integer> getCallStack() { return callStack.clone(); }
	public ArrayDeque<Integer> getDataStack() { return dataStack.clone(); } 
	public int getIP() { return instructionPointer; }
	public String getInstruction() { return instruction; }
	public boolean getCarryFlag() { return cFlag; }
	
	/**
	 * Steps through the simulation
	 * This is the big boi method that does the work of each instruction
	 * 
	 * @return true if no exceptions occurred
	 * @throws IOException 
	 */
	public boolean step() throws IOException {
		//Flags for later
		boolean incIP = true;	//Do we need to increment the instruction pointer
		
		//Execute instruction
		switch(iType) {
			/*
			 * M Type Instructions
			 */
			case MOV_IMM:
				int reg = E8Util.getRegister(instruction, 6);					//Register @ 8-9
				registers[reg] = Integer.parseInt(instruction.substring(8), 2);	//Immediate @ 0-7
				break;
				
			case MOV_REG:
				int sReg = E8Util.getRegister(instruction, 8),	//Source register @ 6-7
					dReg = E8Util.getRegister(instruction, 6);	//Destination register @ 8-9
				registers[dReg] = registers[sReg];
				break;
				
			case MOV_INDEX:
				reg = E8Util.getRegister(instruction, 6);					//S/D register @ 8-9
				int addr = Integer.parseInt(instruction.substring(8), 2);	//Address @ 0-7
				
				if(instruction.charAt(5) == '0') {	//0 if loading
					registers[reg] = RAM[addr];
				} else {							//1 if storing
					RAM[addr] = registers[reg];
				}
				break;
				
			case MOV_INDIR:
				reg = E8Util.getRegister(instruction, 6);									//S/D register @ 8-9
				sReg = E8Util.getRegister(instruction, 8);									//Address register @ 6-7
				addr = registers[sReg] + Integer.parseInt(instruction.substring(10), 2);	//Offset @ 0-5
				
				if(instruction.charAt(5) == '0') {	//0 if loading
					registers[reg] = RAM[addr];
				} else {							//1 if storing
					RAM[addr] = registers[reg];
				}
				break;
			
			/*
			 * A Type Instructions
			 */
			case ADD:
				sReg = E8Util.getRegister(instruction, 10);										//Register A @ 4-5
				dReg = E8Util.getRegister(instruction, 8);										//Destination @ 6-7
				int res = registers[sReg] + (cFlag && instruction.charAt(6) == '1' ? 1 : 0);	//Init result to A + carry (if applicable)
				
				if(instruction.charAt(7) == '0') {	//0 if register
					res += registers[Integer.parseInt(instruction.substring(14), 2)];
				} else {							//1 if immediate
					res += Integer.parseInt(instruction.substring(12), 2);
				}
				 
				cFlag = res > MAX_VALUE;		//Set carry flag
				registers[dReg] = res & MAX_VALUE;	//Set destination to lower 8 bits
				break;
				
			case SUB:
				sReg = E8Util.getRegister(instruction, 10);	//Reg A @ 4-5
				dReg = E8Util.getRegister(instruction, 8);	//Dest @ 6-7
				int bVal = 0;
				
				//Get B
				if(instruction.charAt(7) == '0') {	//Value is reg
					bVal = registers[E8Util.getRegister(instruction, 14)];
				} else {							//Value is immediate
					bVal = Integer.parseInt(instruction.substring(12), 2);
				}
				
				//Carry
				bVal += (cFlag && instruction.charAt(6) == '1') ? 1 : 0;
				cFlag = bVal > registers[sReg];
				
				//Apply & fix
				res = registers[sReg] - bVal;
				registers[dReg] = res & MAX_VALUE;
				break;
				
			case AND:
				sReg = E8Util.getRegister(instruction, 10);	//Reg A @ 4-5
				dReg = E8Util.getRegister(instruction, 8);	//Dest @ 6-7
				bVal = 0;
				
				if(instruction.charAt(7) == '0') {	//B is a register
					bVal = registers[E8Util.getRegister(instruction, 14)];
				} else {							//B is an immediate
					bVal = Integer.parseInt(instruction.substring(12), 2);
				}
				
				//Apply operation, complement, and limit to however many bits in a nice little bitwise mess
				registers[dReg] = ((registers[sReg] & bVal) ^ (instruction.charAt(6) == '0' ? ZERO_MASK : MAX_VALUE)) & MAX_VALUE;
				break;
				
			case OR:
				sReg = E8Util.getRegister(instruction, 10);	//Reg A @ 4-5 (I could just have this later, but, ya know, conventions)
				dReg = E8Util.getRegister(instruction, 8);	//Dest @ 6-7  (also because of how variables work i can't just pipe this to a method)
				bVal = 0;
				
				if(instruction.charAt(7) == '0') {	//B is a register
					bVal = registers[E8Util.getRegister(instruction, 14)];
				} else {							//B is an immediate
					bVal = Integer.parseInt(instruction.substring(12), 2);
				}
				
				//Do basically everything lmao   op v       v complement                               keep to N bits v
				registers[dReg] = ((registers[sReg] | bVal) ^ (instruction.charAt(6) == '0' ? ZERO_MASK : MAX_VALUE)) & MAX_VALUE;
				break;
			
			/*
			 * E Type Instructions
			 */
			case INT:
				String iCode = "";	//interrupt code
				
				if(instruction.charAt(7) == '0') {	//immediate
					iCode = instruction.substring(8);
				} else {
					iCode = E8Util.paddedBinaryString(registers[E8Util.getRegister(instruction, 14)]);
				}
				
				interrupt(iCode);
				break;
				
			default: //NOP
		}
		
		//Load next instruction
		if(incIP) instructionPointer = (instructionPointer + 1) & ADDRESS_MASK;
		updateInstruction();
		
		return true;
	}
	
	/**
	 * Interrupt execution
	 * This will interact with whatever is running the simulator
	 * 
	 * @param code The interrupt code
	 * @throws IOException 
	 */
	private void interrupt(String code) throws IOException {
		int register = E8Util.getRegister(code, 6);	//we'll probably need this
		
		//Code is 6 most significant bits, register is 2 least significant bits
		switch(code.substring(0, 6)) {
			case "000000":	//HALT
				//TODO: Figure out how we tell it to stop
				break;
				
			case "000001":	//Input character to register
				registers[register] = (char) inputStream.read() & MAX_VALUE;
				break;
				
			case "000010":	//Input integer to register
				registers[register] = Integer.parseInt(inputStream.readLine()) & MAX_VALUE;
				break;
				
			case "000011":	//Output character from register
				outputStream.write((char) registers[register]);
				outputStream.flush();
				break;
				
			case "000100":	//Output integer from register
				outputStream.write(Integer.toString(registers[register]));
				outputStream.flush();
				break;
				
			default:	//Unknown interrupt = NOP
		}
	}
	
	/**
	 * Reset the simulator
	 */
	public void reset() {
		RAM = new int[MAX_VALUE + 1];
		registers = new int[4];
		instructionPointer = 0;
		cFlag = false;
		
		updateInstruction();
	}
	
	/**
	 * Assembles a string representing the locations that the current instruction will load from for calculations, including
	 * any immediate values
	 * 
	 * @return A CSV list of locations
	 */
	public String getLoadedLocations() {
		switch(iType) {
			//Immediate 8-bit value
			case MOV_IMM:
				return "IMM8";
				
			//Immediate 10-bit value
			case JMP_DIR:
			case JSR_DIR:
				return "IMM10";
				
			//Register at bits 4-5
			case NOT:
				return E8Util.toRegister(instruction.substring(10, 12));
			
			//Register at bits 6-7
			case MOV_REG:
				return E8Util.toRegister(instruction.substring(8, 10));
			
			//Indexed memory location 
			case MOV_INDEX:
				String s = "IMM8";
				if(instruction.charAt(5) == '0')
					s += "," + instruction.substring(8);
				return s;
			
			//Indirect reg+6 memory location
			case MOV_INDIR:
				String s2 = "IMM6";
				if(instruction.charAt(5) == '0') {
					String reg = instruction.substring(8, 10);
					s2 += "," + E8Util.toRegister(reg) + Integer.toString(registers[Integer.parseInt(reg, 2)] + Integer.parseInt(instruction.substring(10), 2), 2);
				}
				return s2;
				
			//Indirect reg+8 jump
			case JMP_IND:
			case JSR_IND:
				return E8Util.toRegister(instruction.substring(6, 8)) + ",IMM8";
				
			//Register or immediate
			case PUSH:
				s = "";
				
				if(instruction.charAt(7) == '0') {
					s = E8Util.toRegister(instruction.substring(14));
				} else {
					s = "IMM8";
				}
				
				return s;
			
			//Register and register or immediate
			case ADD:
			case SUB:
			case AND:
			case OR:
			case XOR:
			case BSL:
			case BSR:
				String r = E8Util.toRegister(instruction.substring(10, 12)) + ",";
				
				if(instruction.charAt(7) == '0') {
					r += E8Util.toRegister(instruction.substring(14));
				} else {
					r += "IMM4";
				}
				return r;
			
			//Register and register and immediate
			case BEQ:
			case BLT:
			case BGT:
				return E8Util.toRegister(instruction.substring(6, 8)) + "," + E8Util.toRegister(instruction.substring(8, 10)) + ",IMM6";
				
			//Register and immediate
			case BZ:
			case BNZ:
				return E8Util.toRegister(instruction.substring(8, 10)) + ",IMM6";
			
			//Interrupt
			case INT:	//TODO: interrupts that i eventually implement might load from registers as well
				if(instruction.charAt(7) == '0') {
					return "IMM8";
				}
				return E8Util.toRegister(instruction.substring(14));
				
			//Stack
			case POP:
			case PEEK:
				return "STACK";
				
			//Call Stack
			case RET:
				return "CALLSTACK";
			
			default:
				return "";
		}
	}
	
	/**
	 * Assembles a string representing the location being written to by the current instruction
	 * 
	 * @return The location to be written to
	 */
	public String getStoredLocation() {
		switch(iType) {
			//Register at bits 6-7
			case ADD:
			case SUB:
			case AND:
			case OR:
			case XOR:
			case NOT:
			case BSL:
			case BSR:
			case POP:
			case PEEK:
				return E8Util.toRegister(instruction.substring(8, 10));
				
			//Immediate address
			//because of how this and indirect are positioned, the 'register at bits 8-9' section is called
			//if it stores to a register 
			case MOV_INDEX:
				if(instruction.charAt(5) == '1') {
					return instruction.substring(8);
				}
				
			//Indirect address
			case MOV_INDIR:
				if(instruction.charAt(5) == '1') {
					return Integer.toString(registers[Integer.parseInt(instruction.substring(8, 10), 2)] + Integer.parseInt(instruction.substring(10), 2), 2);
				}
				
			//Register at bits 8-9
			case MOV_IMM:
			case MOV_REG:
				return E8Util.toRegister(instruction.substring(6, 8));
				
			//TODO: the interrupts i eventually implement may store to registers
				
			//Stack
			case PUSH:
				return "STACK";
				
			//Call Stack
			case JSR_DIR:
			case JSR_IND:
				return "CALLSTACK";
				
			default:
				return "";
		}
	}
	
	/*
	 * Local Test Methods
	 * Methods and constructors for protected use only
	 */
	
	/**
	 * Local Blank Constructor
	 */
	protected E8Simulator() {
		this(new int[256], new int[1024], new int[4], 8, new ArrayDeque<Integer>(), new ArrayDeque<Integer>(), 0, "0000000000000000", false, System.in, System.out);
	}
	
	protected void setRam(int[] newRam) { RAM = newRam; }
	protected void setRegisters(int[] newRegisters) { registers = newRegisters; }
	protected void setRom(int[] newRom) { ROM = newRom; }
	protected void setIP(int newIP) { instructionPointer = newIP; }
	protected void setInstruction(String newInst) { instruction = newInst; iType = Instructions.getEnumeratedInstruction(instruction); }
	protected void setCarryFlag(boolean nCarry) { cFlag = nCarry; }
	
	/*
	 * Private Methods
	 * Internal stuff
	 */
	
	/**
	 * Update the current instruction, fetching it from ROM at the IP
	 */
	private void updateInstruction() {
		instruction = String.format("%16s", Integer.toBinaryString(ROM[instructionPointer])).replace(' ', '0');
		iType = Instructions.getEnumeratedInstruction(instruction);
	}
}
