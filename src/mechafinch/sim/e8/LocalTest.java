package mechafinch.sim.e8;

/**
 * A testing class with access to protected methods
 * 
 * @author Alex Pickering
 */
public class LocalTest {
	
	public static void main(String[] args) {
		E8Simulator e8 = new E8Simulator();
		e8.setRegisters(new byte[] {5, 6, 7, 8});
		
		//                "                "
		e8.setInstruction("1110100000000000");
		
		System.out.println(e8.getLoadedLocations());
	}
}
