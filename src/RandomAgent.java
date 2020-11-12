import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class RandomAgent extends Agent {

//	private State state;
	private AID mainAgent;
	private int myId;
	private ACLMessage msg;

	protected void setup() {
//		state = State.s0NoConfig;

		// Register in the yellow pages as a player
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
//		addBehaviour(new Play());
		System.out.println("RandomAgent " + getAID().getLocalName() + " is ready.");
	}
}
