package development;

import sim.engine.SimState;

public class MainTest {
    public static void main(String[] args) {
//    	Model model = new Model(1);
    	SimState.doLoop(Model.class, args);
    }
}
