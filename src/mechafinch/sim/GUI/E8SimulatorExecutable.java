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
		GridBagConstraints gbc_ramRomTabs = new GridBagConstraints();
		gbc_ramRomTabs.insets = new Insets(0, 0, 5, 0);
		gbc_ramRomTabs.fill = GridBagConstraints.BOTH;
		gbc_ramRomTabs.gridx = 0;
		gbc_ramRomTabs.gridy = 1;
		frame.getContentPane().add(ramRomTabs, gbc_ramRomTabs);
		
		JPanel ramPanel = new JPanel();
		ramRomTabs.addTab("RAM", null, ramPanel, null);
		ramPanel.setLayout(new BoxLayout(ramPanel, BoxLayout.Y_AXIS));
		
		JLabel lblRam = new JLabel("RAM");
		lblRam.setHorizontalAlignment(SwingConstants.CENTER);
		ramPanel.add(lblRam);
		
		JScrollPane scrollPane = new JScrollPane();
		ramPanel.add(scrollPane);
		
		JTextArea ramTextArea = new JTextArea();
		ramTextArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		ramTextArea.setEditable(false);
		ramTextArea.setText("lmao");
		scrollPane.setViewportView(ramTextArea);
		
		JPanel romPanel = new JPanel();
		ramRomTabs.addTab("ROM", null, romPanel, null);
		romPanel.setLayout(new BoxLayout(romPanel, BoxLayout.Y_AXIS));
		
		JLabel lblRom = new JLabel("ROM");
		lblRom.setHorizontalAlignment(SwingConstants.CENTER);
		romPanel.add(lblRom);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		romPanel.add(scrollPane_1);
		
		JTextArea romTextArea = new JTextArea();
		romTextArea.setEditable(false);
		romTextArea.setText("oaml");
		scrollPane_1.setViewportView(romTextArea);
	}

}
