import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

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
		gui.initTable(playerNames);
//		gui.setPlayersUI(playerNames); Pendiente de saber que hace
	}

	public void resetStats() {

	}

	public void newGame() {
		addBehaviour(new GameManager(this));
		return;
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

		public GameManager(Agent a) {
			super(a);
		}

		@Override
		public void action() {
		}

		@Override
		public boolean done() {
			return false;
		}

	}

	public class GameParametersStruct {

//		int E;
		int N;
		int R;
		int NumGames;

		public GameParametersStruct() {
			// E = 40;
			N = 5;
			R = 10;
			NumGames = 100;
		}

	}

	public class PlayerInformation {

		AID aid;
		int id;
		int gamesPlayed;
		int gamesWin;

		public PlayerInformation(AID a, int i) {
			aid = a;
			id = i;
			gamesPlayed = 0;
			gamesWin = 0;
		}
	}
}
