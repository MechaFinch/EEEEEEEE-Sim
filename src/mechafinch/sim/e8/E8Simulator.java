package mechafinch.sim.e8;

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
	
	private String instruction;		//Current instruction (binary string)
	private Instructions iType;		//The type of the current instruction
	private int instructionPointer;	//The instruction pointer
	private boolean cFlag = false;	//The carry flag
	
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
	 * @param nInstructionPointer Initial IP
	 * @param nInstruction Initial loaded instruction
	 */
	public E8Simulator(int[] nRAM, int[] nROM, int[] nRegisters, int nInstructionPointer, String nInstruction, boolean nCFlag) {
		//Sanitize inputs
		if(nRAM.length != 256) throw new IllegalArgumentException("RAM size must be 256 bytes");
		if(nROM.length != 1024) throw new IllegalArgumentException("ROM size must be 1024x2 bytes");
		if(nRegisters.length != 4) throw new IllegalArgumentException("Register size must be 4 bytes");
		if(nInstructionPointer > 0x3FF || nInstructionPointer < 0) throw new IllegalArgumentException("Instruction pointer must be a 10-bit unsigned value");
		if(nInstruction.length() != 16 || !nInstruction.matches("[10]+")) throw new IllegalArgumentException("Instruction must be a 16-bit binary string");
		
		//Apply inputs
		RAM = nRAM;
		ROM = nROM;
		registers = nRegisters;
		instructionPointer = nInstructionPointer;
		cFlag = nCFlag;
		
		updateInstruction();
	}
	
	/**
	 * RAM & ROM Constructor <br>
	 * Constructs a new simulator instance with the given RAM and ROM
	 * 
	 * @param nRAM Initial RAM state
	 * @param nROM ROM
	 */
	public E8Simulator(int[] nRAM, int[] nROM) {
		this(nRAM, nROM, new int[4], 0, "0000000000000000", false);
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
	public int getIP() { return instructionPointer; }
	public String getInstruction() { return instruction; }
	public boolean getCarryFlag() { return cFlag; }
	
	/**
	 * Steps through the simulation
	 * 
	 * @return true if no exceptions occurred
	 */
	public boolean step() {
		//Flags for later
		boolean incIP = true;	//Do we need to increment the instruction pointer
		
		//Execute instruction
		switch(iType) {
			case MOV_IMM:
				int reg = Integer.parseInt(instruction.substring(6, 8), 2);	//Register at bits 8-9
				registers[reg] = Integer.parseInt(instruction.substring(8), 2);
				break;
			
			default: //NOP
		}
		
		//Load next instruction
		if(incIP) instructionPointer++;
		updateInstruction();
		
		return true;
	}
	
	/**
	 * Reset the simulator
	 */
	public void reset() {
		RAM = new int[256];
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
		this(new int[256], new int[1024], new int[4], 0, "0000000000000000", false);
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
