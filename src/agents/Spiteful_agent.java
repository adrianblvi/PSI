package agents;

import java.io.FileWriter;
import java.io.PrintWriter;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Spiteful_agent extends Agent {

	private State state;
	private AID mainAgent;
	private int myId, opponentId;
	private int N, R;
	private boolean defeated = false;
	private ACLMessage msg;

	protected void setup() {
		state = State.s0NoConfig;

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Player");
		sd.setName("Game");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new Play());
		writeLog("SpitefulAgent " + getAID().getName() + " is ready.");

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

	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		System.out.println("SpitefulAgent " + getAID().getName() + " terminating.");
	}

	private enum State {
		s0NoConfig, s1AwaitingGame, s2Round, s3AwaitingResult
	}

	private class Play extends CyclicBehaviour {

		@Override
		public void action() {
			msg = blockingReceive();
			if (msg != null) {

				switch (state) {
				case s0NoConfig:
					if (msg.getContent().startsWith("Id#") && msg.getPerformative() == ACLMessage.INFORM) {
						boolean parametersUpdated = false;
						try {
							parametersUpdated = validateSetupMessage(msg);
						} catch (NumberFormatException e) {
							System.out.println(getAID().getName() + ":" + state.name() + " - Bad message");
						}
						if (parametersUpdated)
							state = State.s1AwaitingGame;

					} else if (msg.getPerformative() == ACLMessage.INFORM && msg.getContent().startsWith("Removed")) {
						doDelete();
					} else {
						System.out.println(getAID().getName() + ":" + state.name() + " - Unexpected message");
					}
					break;
				case s1AwaitingGame:
					if (msg.getPerformative() == ACLMessage.INFORM) {
						if (msg.getContent().startsWith("Id#")) { // Game settings updated
							try {
								validateSetupMessage(msg);
							} catch (NumberFormatException e) {
								System.out.println(getAID().getName() + ":" + state.name() + " - Bad message");
							}
						} else if (msg.getContent().startsWith("NewGame#")) {
							boolean gameStarted = false;
							try {
								gameStarted = validateNewGame(msg.getContent());
							} catch (NumberFormatException e) {
								System.out.println(getAID().getName() + ":" + state.name() + " - Bad message");
							}
							if (gameStarted)
								state = State.s2Round;
						} else if (msg.getContent().startsWith("GameOver#")) {
							// Creo que no es necesario pero puede venir bien para el inteligente
						} else if (msg.getContent().equals("Removed")) {
							doDelete();
						}
					} else {
						System.out.println(getAID().getName() + ":" + state.name() + " - Unexpected message");
					}
					break;
				case s2Round:
					if (msg.getPerformative() == ACLMessage.REQUEST /* && msg.getContent().startsWith("Position") */) {
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.addReceiver(mainAgent);
						msg.setContent("Action#" + Action());// In other agents is here where he have to codify
																		// the decission
						writeLog(getAID().getName() + " sent " + msg.getContent());
						send(msg);
						state = State.s3AwaitingResult;
					} else if (msg.getPerformative() == ACLMessage.INFORM && msg.getContent().startsWith("Changed#")) {
						// Process changed message, in this case nothing
					} else if (msg.getPerformative() == ACLMessage.INFORM && msg.getContent().startsWith("GameOver#")) {
						// state = State.s1AwaitingGame;
						System.out.println("Fin de la partida");
						state = State.s1AwaitingGame;
					} else {
						System.out.println(
								getAID().getName() + ":" + state.name() + " - Unexpected message:" + msg.getContent());
					}
					break;
				case s3AwaitingResult:
					if (msg.getPerformative() == ACLMessage.INFORM && msg.getContent().startsWith("Results#")) {
						validateResultMessage(msg);
						state = State.s2Round;
					} else {
						System.out.println(getAID().getName() + ":" + state.name() + " - Unexpected message");
					}
					break;

				}

			}
		}

		private void validateResultMessage(ACLMessage msg) {
			int pos;
			String msgContent = msg.getContent();
			String[] contentSplit = msgContent.split("#");
			String[] idSplit = contentSplit[1].trim().split(",");
			if (Integer.parseInt(idSplit[0]) == myId)
				pos = 1;
			else
				pos = 0;

			String[] actions = contentSplit[2].trim().split(",");
			if (!defeated) {
				if (actions[pos].equals("D")) {
					defeated = true;
				}
			}
		}

		private String Action() {
			String answer = "";
			if (defeated) {
				answer = "D";
			} else {
				answer = "C";
			}

			return answer;
		}

		private boolean validateSetupMessage(ACLMessage msg) throws NumberFormatException {
			int tN, tR, tMyId;
			String msgContent = msg.getContent();

			String[] contentSplit = msgContent.split("#");
			if (contentSplit.length != 3)
				return false;
			if (!contentSplit[0].equals("Id"))
				return false;
			tMyId = Integer.parseInt(contentSplit[1]);

			String[] parametersSplit = contentSplit[2].split(",");
			if (parametersSplit.length != 2)
				return false;
			tN = Integer.parseInt(parametersSplit[0]);
			tR = Integer.parseInt(parametersSplit[1]);

			mainAgent = msg.getSender();
			N = tN;
			R = tR;
			myId = tMyId;
			return true;
		}

		public boolean validateNewGame(String msgContent) {
			int msgId0, msgId1;
			String[] contentSplit = msgContent.split("#");
			if (contentSplit.length != 3)
				return false;
			if (!contentSplit[0].equals("NewGame"))
				return false;
			
			msgId0 = Integer.parseInt(contentSplit[1]);
			msgId1 = Integer.parseInt(contentSplit[2]);
			if (myId == msgId0) {
				opponentId = msgId1;
				return true;
			} else if (myId == msgId1) {
				opponentId = msgId0;
				return true;
			}
			return false;
		}

	}
}
