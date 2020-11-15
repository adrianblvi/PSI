import java.util.ArrayList;

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
	private GameParametersStruct parameters = new GameParametersStruct();
	private boolean stop = false;
	private int round = 0;
	private int confesions = 0;
	private int betrayals = 0;

	protected void setup() {
		gui = new MyGui(this);
		gui.setVisible(true);
		updatePlayers();
		gui.setNumberGames(0);
		gui.setNumConfess(0);
		gui.setNumBetray(0);
		gui.setNumRounds(parameters.R);
	}

	public void updatePlayers() {
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
		gui.initTable(playerNames);
//		gui.setPlayersUI(playerNames); Pendiente de saber que hace
	}

	public void resetStats() {

	}

	public int newGame() {
//		round = 0;
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

	public class GameManager extends SimpleBehaviour {

		@Override
		public void action() {
			// Assign the IDs
			ArrayList<PlayerInformation> players = new ArrayList<>();
			int lastId = 0;
			for (AID a : playerAgents) {
				players.add(new PlayerInformation(a, lastId++));
			}

			for (PlayerInformation player : players) {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("Id#" + player.id + "#" + parameters.N + "," + parameters.R);
				msg.addReceiver(player.aid);
				send(msg);
			}

			for (int i = 0; i < players.size() - 1; i++) {
				for (int j = i + 1; j < players.size(); j++) {
					for (int r = 0; r < parameters.R; r++) {
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

				}
			}
		}

		private void playGame(PlayerInformation player1, PlayerInformation player2) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(player1.aid);
			msg.addReceiver(player2.aid);
			msg.setContent("NewGame#" + player1.id + "," + player2.id);
			send(msg);

			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setContent("Action");
			msg.addReceiver(player1.aid);
			send(msg);

			gui.log("Main Waiting for movement");
			ACLMessage move1 = blockingReceive();
			// gui.log("Main Received " + move1.getContent() + " from " +
			// move1.getSender().getName());
//			pos1 = Integer.parseInt(move1.getContent().split("#")[1]);
			String action1 = move1.getContent().split("#")[1];

			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setContent("Action");
			msg.addReceiver(player2.aid);
			send(msg);

			gui.log("Main Waiting for movement");
			ACLMessage move2 = blockingReceive();
			// gui.log("Main Received " + move1.getContent() + " from " +
			// move1.getSender().getName());
//			pos2 = Integer.parseInt(move1.getContent().split("#")[1]);
			String action2 = move1.getContent().split("#")[1];
			String payoff = calculatePayoff(action1, action2, player1, player2);

			msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(player1.aid);
			msg.addReceiver(player2.aid);
			msg.setContent("Results#" + player1.id + "," + player2.id + "#" + payoff);
			send(msg);

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
			float avgPlayer1, avgPlayer2;
			avgPlayer1 = player1.payoff / player1.gamesPlayed;
			avgPlayer2 = player2.payoff / player2.gamesPlayed;
			String avgTotal = avgPlayer1 + "," + avgPlayer2;

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(player1.aid);
			msg.addReceiver(player2.aid);
			msg.setContent("GameOver#" + player1.id + "," + player2.id + "#" + avgTotal);
			send(msg);
			gui.btnStop.setEnabled(false);
			gui.newGame.setEnabled(true);
		}

		private void updateTable(PlayerInformation player) {
			float avg = (float) player.payoff / player.gamesPlayed;
			System.out.println("AVG: " + avg);
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

		public GameParametersStruct() {
			N = 2;
			R = 20;
		}

	}

	public class PlayerInformation {

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
	}
}
