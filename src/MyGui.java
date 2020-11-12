
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
//import jdk.internal.util.xml.impl.Input;
//import sun.font.TrueTypeFont;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class GuiOutputStream extends OutputStream {

	JTextArea textArea;

	public GuiOutputStream(JTextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void write(int data) throws IOException {
		textArea.append(new String(new byte[] { (byte) data }));
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}

/**
 *
 * @author Adrian Blanco
 */
public class MyGui extends javax.swing.JFrame implements Runnable {

	private Thread process;
	private boolean stop;
	private boolean toResume;
	private int round;
	private int defaultRounds = 10;
	private int actualRounds;
	private int numPlayers = 4;
	private int defaultNumPLayers = 4;
	private int gamesPlayed;
	private int betrayals = 0;
	private int confessions = 0;

	/**
	 * Creates new form NewGui
	 */
	public MyGui() {
		initComponents();
		setLocationRelativeTo(null);// Posicion de la ventana en el centro
		this.gamesPlayed = 0;
		btnStop.setEnabled(false);
		btnResume.setEnabled(false);
		taVerbose.setEditable(false);
		txpGamesPlayed.setEditable(false);
		txpGamesPlayed.setText(String.valueOf(gamesPlayed));
		txpNumPlayer.setEditable(false);
		txpNumPlayer.setText(String.valueOf(numPlayers));
		txpRounds.setEditable(false);
		txpRounds.setText(String.valueOf(defaultRounds));
		tpConfessions.setEditable(false);
		tpConfessions.setText(String.valueOf(confessions));
		tpBetray.setEditable(true);
		tpBetray.setText(String.valueOf(betrayals));
		GuiOutputStream rawout = new GuiOutputStream(taVerbose);
		System.setOut(new PrintStream(rawout, true));

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jDialog1 = new javax.swing.JDialog();
		jLabel2 = new javax.swing.JLabel();
		jScrollPane6 = new javax.swing.JScrollPane();
		txNewRounds = new javax.swing.JTextPane();
		btnOkRounds = new javax.swing.JButton();
		jScrollPane8 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();
		consoleLbl = new java.awt.Label();
		lbNumplayers = new javax.swing.JLabel();
		lbGames = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		txpNumPlayer = new javax.swing.JTextPane();
		jScrollPane2 = new javax.swing.JScrollPane();
		txpGamesPlayed = new javax.swing.JTextPane();
		lblStatistics = new javax.swing.JLabel();
		jLabel1 = new javax.swing.JLabel();
		jScrollPane5 = new javax.swing.JScrollPane();
		taVerbose = new javax.swing.JTextArea();
		btnStop = new javax.swing.JButton();
		btnResume = new javax.swing.JButton();
		jScrollPane4 = new javax.swing.JScrollPane();
		txpRounds = new javax.swing.JTextPane();
		lbConfessions = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		tpConfessions = new javax.swing.JTextPane();
		jScrollPane7 = new javax.swing.JScrollPane();
		tpBetray = new javax.swing.JTextPane();
		jScrollPane9 = new javax.swing.JScrollPane();
		tablePlayers = new javax.swing.JTable();
		jMenuBar1 = new javax.swing.JMenuBar();
		startMenu = new javax.swing.JMenu();
		newGame = new javax.swing.JMenuItem();
		numberRounds = new javax.swing.JMenuItem();
		editMenu = new javax.swing.JMenu();
		resetPlayers = new javax.swing.JMenuItem();
		removePlayer = new javax.swing.JMenuItem();
		windowMenu = new javax.swing.JMenu();
		verbose = new javax.swing.JCheckBoxMenuItem();
		helpMenu = new javax.swing.JMenu();
		about = new javax.swing.JMenuItem();

		jDialog1.setTitle("Number of rounds");

		jLabel2.setText("Introduce the new number of rounds:");

		txNewRounds.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		txNewRounds.setAutoscrolls(false);
		txNewRounds.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				txNewRoundsKeyTyped(evt);
			}
		});
		jScrollPane6.setViewportView(txNewRounds);

		btnOkRounds.setText("Ok");
		btnOkRounds.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnOkRoundsActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
		jDialog1.getContentPane().setLayout(jDialog1Layout);
		jDialog1Layout.setHorizontalGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jDialog1Layout.createSequentialGroup().addGroup(jDialog1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jDialog1Layout.createSequentialGroup().addContainerGap()
								.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 270,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(3, 3, 3)
								.addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
						.addGroup(jDialog1Layout.createSequentialGroup().addGap(112, 112, 112).addComponent(btnOkRounds,
								javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGap(35, 35, 35)));
		jDialog1Layout.setVerticalGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jDialog1Layout.createSequentialGroup().addContainerGap()
						.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 25,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18).addComponent(btnOkRounds).addGap(28, 28, 28)));

		jTable1.setModel(
				new javax.swing.table.DefaultTableModel(
						new Object[][] { { null, null, null, null }, { null, null, null, null },
								{ null, null, null, null }, { null, null, null, null } },
						new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
		jScrollPane8.setViewportView(jTable1);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		consoleLbl.setFont(new java.awt.Font("Ubuntu", 0, 15)); // NOI18N
		consoleLbl.setText("Console:");

		lbNumplayers.setText("Number of players:");

		lbGames.setText("Games played:");

		txpNumPlayer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jScrollPane1.setViewportView(txpNumPlayer);

		txpGamesPlayed.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jScrollPane2.setViewportView(txpGamesPlayed);

		lblStatistics.setText("Statistics:");

		jLabel1.setText("Rounds:");

		taVerbose.setColumns(20);
		taVerbose.setRows(5);
		jScrollPane5.setViewportView(taVerbose);

		btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pause.png"))); // NOI18N
		btnStop.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnStopActionPerformed(evt);
			}
		});

		btnResume.setIcon(new javax.swing.ImageIcon(getClass().getResource("/play.png"))); // NOI18N
		btnResume.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnResumeActionPerformed(evt);
			}
		});

		txpRounds.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jScrollPane4.setViewportView(txpRounds);

		lbConfessions.setText("Confessions: ");

		jLabel3.setText("Betrayals: ");

		jScrollPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jScrollPane3.setViewportView(tpConfessions);

		tpBetray.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jScrollPane7.setViewportView(tpBetray);

		tablePlayers.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {

		}, new String[] { "Round", "Player 1", "Player 2", "Player 3", "Player 4" }) {
			Class[] types = new Class[] { java.lang.String.class, java.lang.String.class, java.lang.String.class,
					java.lang.String.class, java.lang.String.class };

			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}
		});
		jScrollPane9.setViewportView(tablePlayers);

		startMenu.setText("Start");

		newGame.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
		newGame.setText("New Game");
		newGame.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newGameActionPerformed(evt);
			}
		});
		startMenu.add(newGame);

		numberRounds.setText("Number of rounds");
		numberRounds.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				numberRoundsActionPerformed(evt);
			}
		});
		startMenu.add(numberRounds);

		jMenuBar1.add(startMenu);

		editMenu.setText("Edit");

		resetPlayers.setText("Reset players");
		resetPlayers.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetPlayersActionPerformed(evt);
			}
		});
		editMenu.add(resetPlayers);

		removePlayer.setText("Remove player");
		editMenu.add(removePlayer);

		jMenuBar1.add(editMenu);

		windowMenu.setText("Window");

		verbose.setSelected(true);
		verbose.setText("Verbose on/off");
		verbose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				verboseActionPerformed(evt);
			}
		});
		windowMenu.add(verbose);

		jMenuBar1.add(windowMenu);

		helpMenu.setText("Help");

		about.setText("About");
		about.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				aboutActionPerformed(evt);
			}
		});
		helpMenu.add(about);

		jMenuBar1.add(helpMenu);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout
												.createSequentialGroup().addContainerGap().addComponent(jScrollPane9))
										.addGroup(layout
												.createSequentialGroup().addGap(12, 12, 12).addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup().addComponent(
																btnResume, javax.swing.GroupLayout.PREFERRED_SIZE, 77,
																javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		11, Short.MAX_VALUE)
																.addComponent(btnStop,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 85,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(layout.createSequentialGroup().addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(lbGames).addComponent(lbNumplayers)
																.addComponent(
																		lblStatistics)
																.addComponent(jLabel3).addComponent(lbConfessions)
																.addComponent(jLabel1))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(layout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(jScrollPane1)
																		.addComponent(jScrollPane4)
																		.addComponent(jScrollPane2)
																		.addComponent(jScrollPane3)
																		.addComponent(jScrollPane7,
																				javax.swing.GroupLayout.Alignment.TRAILING))))
												.addGap(72, 72, 72)
												.addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jScrollPane5,
																javax.swing.GroupLayout.PREFERRED_SIZE, 287,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(consoleLbl,
																javax.swing.GroupLayout.PREFERRED_SIZE, 87,
																javax.swing.GroupLayout.PREFERRED_SIZE))))
								.addGap(67, 67, 67)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
						.addComponent(consoleLbl, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lblStatistics))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(layout
						.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addGroup(layout.createSequentialGroup().addGap(6, 6, 6).addComponent(lbGames))
								.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addGap(12, 12, 12).addComponent(lbNumplayers))
								.addGroup(layout.createSequentialGroup()
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel1))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup()
												.addComponent(lbConfessions, javax.swing.GroupLayout.PREFERRED_SIZE, 24,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 48,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createSequentialGroup()
										.addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(31, 31, 31)
										.addComponent(btnResume, javax.swing.GroupLayout.PREFERRED_SIZE, 48,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(0, 0, Short.MAX_VALUE))))
						.addComponent(jScrollPane5))
				.addGap(18, 18, 18).addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 234,
						javax.swing.GroupLayout.PREFERRED_SIZE)
				.addContainerGap(45, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	@Override
	public void run() {
		int i = 0;
		if (toResume) {
			i = round;
			toResume = false;
		}
		if (actualRounds == 0) {
			actualRounds = defaultRounds;
		}
		while (i < actualRounds) {
			try {
				i++;
				if (!stop) {
					round = i;
					System.out.println("Round: " + i);
					addTableRow();
					Thread.sleep(2000);
				} else {
					return;
				}
			} catch (InterruptedException oIE) {
			}
		}
		newGame.setEnabled(true);
		btnStop.setEnabled(false);
		round = 0;
		gamesPlayed++;
		txpGamesPlayed.setText(String.valueOf(gamesPlayed));
		resetPlayers.setEnabled(true);
		removePlayer.setEnabled(true);
		stopThread();
	}

	public void startThread() {
		if (process == null) {
			process = new Thread(this);
			process.start();
			stop = false;
		}
	}

	public void stopThread() {
		if (process != null) {
			stop = true;
		}
		process = null;
	}

	private void newGameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newGameActionPerformed
		System.out.println("New Game!");
		clearTable();
		startThread();
		btnStop.setEnabled(true);
		btnResume.setEnabled(false);
		newGame.setEnabled(false);
		resetPlayers.setEnabled(false);
		removePlayer.setEnabled(false);
	}// GEN-LAST:event_newGameActionPerformed

	private void numberRoundsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_numberRoundsActionPerformed
		jDialog1.setVisible(true);
		jDialog1.setSize(361, 120);
		jDialog1.setLocationRelativeTo(null);
		btnOkRounds.setEnabled(false);
	}// GEN-LAST:event_numberRoundsActionPerformed

	private void verboseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_verboseActionPerformed
		if (verbose.isSelected()) {
			taVerbose.setVisible(true);
		} else {
			taVerbose.setVisible(false);
		}
	}// GEN-LAST:event_verboseActionPerformed

	private void aboutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_aboutActionPerformed
		About_dialog aboutDialog = new About_dialog(this, false);
		aboutDialog.setVisible(true);
		aboutDialog.setLocationRelativeTo(null);
	}// GEN-LAST:event_aboutActionPerformed

	private void btnResumeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnResumeActionPerformed
		System.out.println("You clicked Resume!");
		stop = false;
		toResume = true;
		btnResume.setEnabled(false);
		btnStop.setEnabled(true);
		newGame.setEnabled(false);
		startThread();
	}// GEN-LAST:event_btnResumeActionPerformed

	private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnStopActionPerformed
		System.out.println("You clicked Stop!");
		stopThread();
		btnResume.setEnabled(true);
		btnStop.setEnabled(false);
		newGame.setEnabled(true);
	}// GEN-LAST:event_btnStopActionPerformed

	private void btnOkRoundsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnOkRoundsActionPerformed
		actualRounds = Integer.valueOf(txNewRounds.getText());
		if (actualRounds > 0) {
			txpRounds.setText(String.valueOf(actualRounds));
		}
		jDialog1.dispose();
	}// GEN-LAST:event_btnOkRoundsActionPerformed

	private void txNewRoundsKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txNewRoundsKeyTyped
		if (txNewRounds.getText().isEmpty()) {
			btnOkRounds.setEnabled(false);
		} else {
			btnOkRounds.setEnabled(true);
			System.out.println(txNewRounds.getText());
		}
	}// GEN-LAST:event_txNewRoundsKeyTyped

	private void resetPlayersActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_resetPlayersActionPerformed
		numPlayers = defaultNumPLayers;
		txpNumPlayer.setText(String.valueOf(numPlayers));
		clearTable();
	}// GEN-LAST:event_resetPlayersActionPerformed

	private String randomOption() {
		int valorDado = (int) Math.floor(Math.random() * 2 + 1);
		String answer = "";
		switch (valorDado) {
		case 1:
			answer = "Betray";
			betrayals++;
			tpBetray.setText(String.valueOf(betrayals));
			break;
		case 2:
			answer = "Confess";
			confessions++;
			tpConfessions.setText(String.valueOf(confessions));
			break;
		default:
			answer = "Error";
			break;
		}
		return answer;
	}

	
	public void addTableRow() {
		String[] decisions = new String[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			decisions[i] = randomOption();
		}
		// cambiar cuando se cambie numero de jugadores
		Object[] row = { round, decisions[0], decisions[1], decisions[2], decisions[3] };
		DefaultTableModel model = (DefaultTableModel) tablePlayers.getModel();
		model.addRow(row);
	}

	public void clearTable() {
		if (gamesPlayed == 0) {
			return;
		} else {
			System.out.println("Clearing table...");
			DefaultTableModel model = (DefaultTableModel) tablePlayers.getModel();
			model.setRowCount(0);
		}
	}

	 public void log(String log) {
		 System.out.println(log);
	 }
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
		// (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;

				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MyGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MyGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MyGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MyGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MyGui().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JMenuItem about;
	private javax.swing.JButton btnOkRounds;
	private javax.swing.JButton btnResume;
	private javax.swing.JButton btnStop;
	private java.awt.Label consoleLbl;
	private javax.swing.JMenu editMenu;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JDialog jDialog1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JScrollPane jScrollPane5;
	private javax.swing.JScrollPane jScrollPane6;
	private javax.swing.JScrollPane jScrollPane7;
	private javax.swing.JScrollPane jScrollPane8;
	private javax.swing.JScrollPane jScrollPane9;
	private javax.swing.JTable jTable1;
	private javax.swing.JLabel lbConfessions;
	private javax.swing.JLabel lbGames;
	private javax.swing.JLabel lbNumplayers;
	private javax.swing.JLabel lblStatistics;
	private javax.swing.JMenuItem newGame;
	private javax.swing.JMenuItem numberRounds;
	private javax.swing.JMenuItem removePlayer;
	private javax.swing.JMenuItem resetPlayers;
	private javax.swing.JMenu startMenu;
	private javax.swing.JTextArea taVerbose;
	private javax.swing.JTable tablePlayers;
	private javax.swing.JTextPane tpBetray;
	private javax.swing.JTextPane tpConfessions;
	private javax.swing.JTextPane txNewRounds;
	private javax.swing.JTextPane txpGamesPlayed;
	private javax.swing.JTextPane txpNumPlayer;
	private javax.swing.JTextPane txpRounds;
	private javax.swing.JCheckBoxMenuItem verbose;
	private javax.swing.JMenu windowMenu;
	// End of variables declaration//GEN-END:variables

}
