import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class MainAgent extends Agent {
	private MyGui gui;
	private AID[] playerAgents;
	private ArrayList<PlayerInformation> players = new ArrayList<>();
	private ArrayList<String> avgList;
	private GameParametersStruct parameters = new GameParametersStruct();
	private boolean stop = false;
	private int round = 0;
	private int confesions = 0;
	private int betrayals = 0;

	protected void setup() {
		gui = new MyGui(this);
		gui.setVisible(true);
		updatePlayers(true);
		gui.setNumberGames(0);
		gui.setNumConfess(0);
		gui.setNumBetray(0);
		gui.setNumRounds(parameters.R);
	}

	public void updatePlayers(boolean first) {
		gui.log("Updating players list");
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Player");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			if (result.length > 0) {
				gui.log("Found " + result.length + " players");
			}
			playerAgents = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				playerAgents[i] = result[i].getName();
			}
		} catch (FIPAException fe) {
			gui.log(fe.getMessage());
		}

		ArrayList<String> playerNames = new ArrayList<>();
		for (int i = 0; i < playerAgents.length; i++) {
			playerNames.add(playerAgents[i].getLocalName());
		}
		parameters.N = playerNames.size();
		gui.setNumberPlayers(parameters.N);
		if (first) {
			gui.initTable(playerNames);
		}

	}

	public void resetStats() {
		gui.setNumberGames(0);
		gui.setNumConfess(0);
		gui.setNumBetray(0);
		gui.clearTable(false);
	}

	public int newGame() {
		avgList = new ArrayList<>();
		addBehaviour(new GameManager());
		return 0;
	}

	public void setStop() {
		stop = true;
	}

	public void setResume() {
		stop = false;
		doWake();
	}

	public void newNumRounds(int newNumRounds) {
		parameters.R = newNumRounds;
		parameters.modifiedR();

	}

	public void deletePlayer(String playerName) {
		AID removedPlayer = null;
		for (int i = 0; i < playerAgents.length; i++) {
			String aux = playerAgents[i].getLocalName().trim();
			if (aux.trim().equals(playerName.trim())) {
				removedPlayer = playerAgents[i];
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(removedPlayer);
				msg.setContent("Removed");
				send(msg);
				break;
			}
		}

		if (removedPlayer == null) {
			gui.log("This player doesnt exists");
		} else {
			gui.log("Player " + removedPlayer.getLocalName() + " removed");
			writeLog("Player " + removedPlayer.getLocalName() + " removed");
			updatePlayers(false);
		}

	}

	private void writeLog(String log) {
		FileWriter fichero = null;
		PrintWriter pw = null;
		try {
			fichero = new FileWriter("log.txt", true);
			pw = new PrintWriter(fichero);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pw.write(log + "\n");
				if (null != fichero)
					fichero.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public class GameManager extends SimpleBehaviour {

		@Override
		public void action() {
			// Assign the IDs
			ArrayList<PlayerInformation> players = new ArrayList<>();
			int lastId = 0;
			for (AID a : playerAgents) {
				players.add(new PlayerInformation(a, lastId++));
			}
			for (PlayerInformation playerInformation : players) {
				writeLog(playerInformation.aid.getLocalName() + " " + playerInformation.id);
			}
			for (PlayerInformation player : players) {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("Id#" + player.id + "#" + parameters.N + "," + parameters.R);
				msg.addReceiver(player.aid);
				send(msg);
				writeLog("Id#" + player.id + "#" + parameters.N + "," + parameters.R);
			}
			int actualRound = parameters.R;
			int newRound = parameters.R;
			for (int i = 0; i < players.size() - 1; i++) {
				for (int j = i + 1; j < players.size(); j++) {
					for (int r = 0; r < newRound; r++) {
						if (!stop) {
							playGame(players.get(i), players.get(j));
							round++;
							gui.setNumberGames(round);
							if ((r + 1) == parameters.R) {
								endGame(players.get(i), players.get(j));
							}
						} else {
							doWait();
						}
					}
					int modification = (int) Math.floor(Math.random() * (parameters.nR) + 1);
					System.out.println("Modification number: " + modification);
					newRound = (actualRound + 1) - modification;
					System.out.println("Actual round updated: " + newRound);
				}
			}
			finalEnd(players);
		}

		private void finalEnd(ArrayList<PlayerInformation> players) {
			gui.log("Fin del juego");
			gui.log("Sorting table");
			ArrayList<String> names = new ArrayList<>();
			Collections.sort(players);
			for (PlayerInformation player : players) {
				gui.log(player.aid.getLocalName());
				names.add(player.aid.getLocalName());
			}
			gui.clearTable(true);
			gui.initTable(names);
			for (PlayerInformation player : players) {
				updateTable(player);
			}
			gui.showWinner(players.get(0).aid.getLocalName(), players.get(0).payoff, players.get(0).gamesPlayed);
		}

		private void playGame(PlayerInformation player1, PlayerInformation player2) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(player1.aid);
			msg.addReceiver(player2.aid);
			msg.setContent("NewGame#" + player1.id + "," + player2.id);
			send(msg);
			writeLog("NewGame#" + player1.id + "," + player2.id);

			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setContent("Action");
			msg.addReceiver(player1.aid);
			send(msg);

			ACLMessage move1 = blockingReceive();
			String action1 = move1.getContent().split("#")[1];

			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setContent("Action");
			msg.addReceiver(player2.aid);
			send(msg);

			ACLMessage move2 = blockingReceive();
			String action2 = move2.getContent().split("#")[1];
			avgList.add(calculatePayoff(action1, action2, player1, player2));
			msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(player1.aid);
			msg.addReceiver(player2.aid);
			msg.setContent("Results#" + player1.id + "," + player2.id + "#" + action1 + "," + action2);
			send(msg);
			writeLog("Results#" + player1.id + "," + player2.id + "#" + action1 + "," + action2);
		}

		private String calculatePayoff(String action1, String action2, MainAgent.PlayerInformation player1,
				MainAgent.PlayerInformation player2) {
			String result = action1 + action2;
			String retResult = "";
			switch (result) {
			case "CC":
				player1.payoff += 3;
				player2.payoff += 3;
				confesions += 2;
				retResult = "3,3";
				break;
			case "CD":
				player2.payoff += 5;
				confesions++;
				betrayals++;
				retResult = "0,5";
				break;
			case "DC":
				player1.payoff += 5;
				confesions++;
				betrayals++;
				retResult = "5,0";
				break;
			case "DD":
				player1.payoff += 1;
				player2.payoff += 1;
				betrayals += 2;
				retResult = "1,1";
				break;
			}
			player1.gamesPlayed++;
			player2.gamesPlayed++;
			gui.updateConfBetray(confesions, betrayals);
			updateTable(player1);
			updateTable(player2);
			return retResult;
		}

		private void endGame(PlayerInformation player1, PlayerInformation player2) {

			String avgTotal = obtainAvgGame();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(player1.aid);
			msg.addReceiver(player2.aid);
			msg.setContent("GameOver#" + player1.id + "," + player2.id + "#" + avgTotal);
			writeLog("GameOver#" + player1.id + "," + player2.id + "#" + avgTotal);
			send(msg);
			gui.btnStop.setEnabled(false);
			gui.newGame.setEnabled(true);
			gui.removePlayer.setEnabled(true);
			gui.resetPlayers.setEnabled(true);
		}

		private String obtainAvgGame() {
			float avgPlayer1 = 0, avgPlayer2 = 0;
			for (String string : avgList) {
				String[] avgAux = string.split(",");
				avgPlayer1 += Integer.parseInt(avgAux[0]);
				avgPlayer2 += Integer.parseInt(avgAux[1]);
			}
			DecimalFormat format = new DecimalFormat("#.##");
			String toRet = format.format((avgPlayer1 / avgList.size())) + ","
					+ format.format((avgPlayer2 / avgList.size()));
			return toRet;
		}

		private void updateTable(PlayerInformation player) {
			float avg = (float) player.payoff / player.gamesPlayed;
			gui.updateTable(String.valueOf(player.aid.getLocalName()), String.valueOf(player.gamesPlayed),
					String.valueOf(player.payoff), String.valueOf(avg), String.valueOf(player.gamesWin));
		}

		@Override
		public boolean done() {
			return true;
		}

	}

	public class GameParametersStruct {

		int N;
		int R;
		int nR;

		public GameParametersStruct() {
			N = 2;
			R = 20;
			nR = (int) (R - (0.9 * R));

		}

		private void modifiedR() {
			nR = (int) (R - (0.9 * R));
		}
	}

	public class PlayerInformation implements Comparable<PlayerInformation> {

		AID aid;
		int id;
		int payoff;
		int gamesPlayed;
		int gamesWin;

		public PlayerInformation(AID a, int i) {
			aid = a;
			id = i;
			payoff = 0;
		}

		@Override
		public int compareTo(MainAgent.PlayerInformation player) {
			DecimalFormat format = new DecimalFormat("#.##");
			int toRet = 0;
			Double avg = Double.valueOf(this.payoff) / Double.valueOf(this.gamesPlayed);
			Double plyrAvg = Double.valueOf(player.payoff) / Double.valueOf(player.gamesPlayed);
			if (avg < plyrAvg) {
				toRet = 1;
			} else {
				toRet = -1;
			}
			return toRet;
		}

	}
}
