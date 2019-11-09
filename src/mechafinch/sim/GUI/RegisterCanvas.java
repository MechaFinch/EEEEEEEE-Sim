package mechafinch.sim.GUI;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * A canvas which displays the value of a register
 * 
 * @author Alex Pickering
 */
@SuppressWarnings("serial")
public class RegisterCanvas extends Canvas {
	
	private String registerName;	//Name of the register (A, B, IP, etc)
	private int numBits,			//Number of bits
				value = 0;			//Current value of the register
	
	/**
	 * Default constructor
	 * 
	 * @param registerName The name of the register
	 * @param numBits The number of bits of the value of the reigster
	 */
	public RegisterCanvas(String registerName, int numBits) {
		this.registerName = registerName;
		this.numBits = numBits;
	}
	
	/*
	 * Setters
	 */
	public void setValue(int v) {
		if(v >= Math.pow(2, numBits)) throw new IllegalArgumentException("Value out of bounds");
		
		value = v;
	}
	
	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		
		//TODO: show the value lmao
	}
}
