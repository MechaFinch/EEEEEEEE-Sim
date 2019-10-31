package mechafinch.sim.e8;

public enum Instructions {
	MOV_IMM, MOV_REG, MOV_INDEX, MOV_INDIR,
	ADD, SUB, AND, OR, XOR, NOT, BSL, BSR,
	JMP_DIR, JMP_IND, JSR_DIR, JSR_IND, RET,
	BEQ, BLT, BGT, BZ, BNZ,
	INT, NOP;
	
	public static Instructions getEnumeratedInstruction(String bin) {
		if(bin.startsWith("00100")) { return MOV_IMM; }
		else if(bin.startsWith("00101")) { return MOV_REG; }
		else if(bin.startsWith("00110")) { return MOV_INDEX; }
		else if(bin.startsWith("00111")) { return MOV_INDIR; }
		
		else if(bin.startsWith("010000")) { return ADD; }
		else if(bin.startsWith("010001")) { return SUB; }
		else if(bin.startsWith("010010")) { return AND; }
		else if(bin.startsWith("010011")) { return OR; }
		else if(bin.startsWith("010100")) { return XOR; }
		else if(bin.startsWith("0101010")) { return NOT; }
		else if(bin.startsWith("0101011")) { return BSL; }
		else if(bin.startsWith("010110")) { return BSR; }
		
		else if(bin.startsWith("011000")) { return JMP_DIR; }
		else if(bin.startsWith("011001")) { return JMP_IND; }
		else if(bin.startsWith("011010")) { return JSR_DIR; }
		else if(bin.startsWith("011011")) { return JSR_IND; }
		else if(bin.startsWith("0111")) { return RET; }
		
		else if(bin.startsWith("10000")) { return BEQ; }
		else if(bin.startsWith("10001")) { return BLT; }
		else if(bin.startsWith("10010")) { return BGT; }
		else if(bin.startsWith("10011")) { return bin.charAt(7) == '0' ? BZ : BNZ; }
		
		else if(bin.startsWith("111")) { return INT; }
		
		return NOP;
	}
}
