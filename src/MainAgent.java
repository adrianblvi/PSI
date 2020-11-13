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
			// Initialize (inform ID)
			for (PlayerInformation player : players) {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("Id#" + player.id + "#" + parameters.N + "," + parameters.R);
				msg.addReceiver(player.aid);
				send(msg);
			}
		}

		@Override
		public boolean done() {
			return true;
		}

	}

	public class GameParametersStruct {

		int N;
		int S;
		int R;
		int I;
		int P;

		public GameParametersStruct() {
			N = 2;
			S = 4;
			R = 50;
			I = 0;
			P = 10;
		}

	}

	public class PlayerInformation {

		AID aid;
		int id;
//		int gamesPlayed;
//		int gamesWin;

		public PlayerInformation(AID a, int i) {
			aid = a;
			id = i;
		}
	}
}
