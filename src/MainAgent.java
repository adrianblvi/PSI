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
	private boolean endAdvise = false;

	protected void setup() {
		gui = new MyGui(this);
		gui.setVisible(true);
		updatePlayers();

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
		gui.initTable(playerNames);
//		gui.setPlayersUI(playerNames); Pendiente de saber que hace
	}

	public void resetStats() {

	}

	public int newGame() {
		round = 0;
		addBehaviour(new GameManager());
		return 0;
	}

	public void setStop() {
		gui.log("[MAIN AGENT]: Stop");
		stop = true;
	}

	public void setResume() {
		gui.log("[MAIN AGENT]: Resume");
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
						gui.log("Agent: " + players.get(i).id + " vs Agent: " + players.get(j).id + " Game: "
								+ (r + 1));
						playGame(players.get(i), players.get(j));
						round++;
						gui.setNumberGames(round);
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
			switch (result) {
			case "CC":
				System.out.println("Payoff:");
				System.out.println("Player 1: 3");
				player1.payoff += 3;
				player1.gamesPlayed++;
				System.out.println("Player 2: 3");
				player2.payoff += 3;
				player2.gamesPlayed++;
				return "3,3";

			case "CD":
				System.out.println("Payoff:");
				System.out.println("Player 1: 0");
				System.out.println("Player 2: 5");
				player2.payoff += 5;
				player2.gamesPlayed++;
				player1.gamesPlayed++;
				return "0,5";
			case "DC":
				System.out.println("Payoff:");
				System.out.println("Player 1: 5");
				player1.payoff += 5;
				player1.gamesPlayed++;
				System.out.println("Player 2: 0");
				player2.gamesPlayed++;
				return "5,0";
			case "DD":
				System.out.println("Payoff:");
				System.out.println("Player 1: 1");
				player1.payoff += 1;
				player1.gamesPlayed++;
				System.out.println("Player 2: 1");
				player2.payoff += 1;
				player2.gamesPlayed++;
				return "1,1";
			}
			return null;
		}

		@Override
		public boolean done() {
			return true;
		}

	}

	public class GameParametersStruct {

		int N;
		int R;
//		int S;
//		int I;
//		int P;

		public GameParametersStruct() {
			N = 2;
			R = 10;
			// S = 4;
			// I = 0;
			// P = 10;
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
		}
	}
}
