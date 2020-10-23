import jade.core.Agent;

public class MainAgent extends Agent {
	private MyGui gui;

	protected void setup() {
		gui = new MyGui();
		gui.setVisible(true);
	}
}
