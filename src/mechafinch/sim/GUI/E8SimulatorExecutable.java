package mechafinch.sim.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Insets;
import java.awt.CardLayout;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.JTextPane;

public class E8SimulatorExecutable {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					E8SimulatorExecutable window = new E8SimulatorExecutable();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public E8SimulatorExecutable() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JPanel registersPanel = new JPanel();
		GridBagConstraints gbc_registersPanel = new GridBagConstraints();
		gbc_registersPanel.insets = new Insets(0, 0, 5, 0);
		gbc_registersPanel.fill = GridBagConstraints.BOTH;
		gbc_registersPanel.gridx = 0;
		gbc_registersPanel.gridy = 0;
		frame.getContentPane().add(registersPanel, gbc_registersPanel);
		
		JTabbedPane ramRomTabs = new JTabbedPane(JTabbedPane.TOP);
		ramRomTabs.setBorder(null);
		GridBagConstraints gbc_ramRomTabs = new GridBagConstraints();
		gbc_ramRomTabs.insets = new Insets(0, 0, 5, 0);
		gbc_ramRomTabs.fill = GridBagConstraints.BOTH;
		gbc_ramRomTabs.gridx = 0;
		gbc_ramRomTabs.gridy = 1;
		frame.getContentPane().add(ramRomTabs, gbc_ramRomTabs);
		
		JScrollPane ramPane = new JScrollPane();
		ramPane.setViewportBorder(null);
		ramRomTabs.addTab("RAM", null, ramPane, null);
		
		JTextPane ramText = new JTextPane();
		ramText.setFont(new Font("Courier New", Font.PLAIN, 14));
		ramText.setText("     00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\r\n\r\n00   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n10   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n20   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n30   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n40   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n50   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n60   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n70   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n80   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n90   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\nA0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\nB0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\nC0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\nD0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\nE0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\nF0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
		ramPane.setViewportView(ramText);
		
		JScrollPane romPane = new JScrollPane();
		romPane.setViewportBorder(null);
		ramRomTabs.addTab("ROM", null, romPane, null);
		
		JTextPane romText = new JTextPane();
		romText.setFont(new Font("Courier New", Font.PLAIN, 14));
		romText.setText("      00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\r\n\r\n000   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n010   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n020   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n030   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n040   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n050   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n060   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n070   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n080   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n090   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n0A0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n0B0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n0C0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n0D0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n0E0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n0F0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n100   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n110   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n120   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n130   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n140   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n150   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n160   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n170   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n180   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n190   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n1A0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n1B0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n1C0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n1D0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n1E0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n1F0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n200   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n210   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n220   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n230   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n240   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n250   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n260   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n270   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n280   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n290   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n2A0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n2B0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n2C0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n2D0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n2E0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n2F0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n300   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n310   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n320   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n330   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n340   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n350   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n360   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n370   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n380   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n390   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n3A0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n3B0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n3C0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n3D0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n3E0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\r\n3F0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
		romPane.setViewportView(romText);
	}

}
