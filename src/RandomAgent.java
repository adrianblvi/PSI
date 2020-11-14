import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class RandomAgent extends Agent {

	private State state;
	private AID mainAgent;
	private int myId, opponentId;
	private int N, R;
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
		System.out.println("RandomAgent " + getAID().getName() + " is ready.");

	}

	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		System.out.println("RandomPlayer " + getAID().getName() + " terminating.");
	}

	private enum State {
		s0NoConfig, s1AwaitingGame, s2Round, s3AwaitingResult
	}

	private class Play extends CyclicBehaviour {

		@Override
		public void action() {
//			System.out.println(getAID().getName() + ":" + state.name());
			msg = blockingReceive();
			if (msg != null) {
				// System.out.println(
				// getAID().getName() + " received " + msg.getContent() + " from " +
				// msg.getSender().getName());
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
						}
					} else if (msg.getContent().startsWith("GameOver#")) {
						System.out.println("Game seems to be finsihed");
						state = State.s0NoConfig;
					} else {
						System.out.println(getAID().getName() + ":" + state.name() + " - Unexpected message");
					}
					break;
				case s2Round:
					if (msg.getPerformative() == ACLMessage.REQUEST /* && msg.getContent().startsWith("Position") */) {
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.addReceiver(mainAgent);
						msg.setContent("Action#" + randomOption());// In other agents is here where he have to codify
																	// the decission
						System.out.println(getAID().getName() + " sent " + msg.getContent());
						send(msg);
						state = State.s3AwaitingResult;
					} else if (msg.getPerformative() == ACLMessage.INFORM && msg.getContent().startsWith("Changed#")) {
						// Process changed message, in this case nothing
					} else if (msg.getPerformative() == ACLMessage.INFORM && msg.getContent().startsWith("EndGame")) {
						state = State.s1AwaitingGame;
					} else {
						System.out.println(
								getAID().getName() + ":" + state.name() + " - Unexpected message:" + msg.getContent());
					}
					break;
				case s3AwaitingResult:
					if (msg.getPerformative() == ACLMessage.INFORM && msg.getContent().startsWith("Results#")) {
						validateResultMessage(msg);
						state = State.s1AwaitingGame;
					} else {
						System.out.println(getAID().getName() + ":" + state.name() + " - Unexpected message");
					}
					break;

				}

			}
		}

		private void validateResultMessage(ACLMessage msg) {
			System.out.println("Processing the results... ");

		}

		private String randomOption() {
			int valorDado = (int) Math.floor(Math.random() * 2 + 1);
			String answer = "";
			switch (valorDado) {
			case 1:
				answer = "D";
				break;
			case 2:
				answer = "C";
				break;
			default:
				answer = "Error";
				break;
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
//			tS = Integer.parseInt(parametersSplit[1]);
//			tI = Integer.parseInt(parametersSplit[3]);
//			tP = Integer.parseInt(parametersSplit[4]);

			// At this point everything should be fine, updating class variables
			mainAgent = msg.getSender();
			N = tN;
			R = tR;
//			S = tS;
//			I = tI;
//			P = tP;
			myId = tMyId;
			return true;
		}

		public boolean validateNewGame(String msgContent) {
			int msgId0, msgId1;
			String[] contentSplit = msgContent.split("#");
			if (contentSplit.length != 2)
				return false;
			if (!contentSplit[0].equals("NewGame"))
				return false;
			String[] idSplit = contentSplit[1].split(",");
			if (idSplit.length != 2)
				return false;
			msgId0 = Integer.parseInt(idSplit[0]);
			msgId1 = Integer.parseInt(idSplit[1]);
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