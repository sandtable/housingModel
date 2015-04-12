package housing;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.media.chart.ScatterPlotGenerator;
import sim.util.media.chart.ScatterPlotSeriesAttributes;
import sim.display.ChartUtilities;

/********************************************
 * Mason Graphic Interface for the housing market simulation.
 * 
 * @author daniel
 *
 ********************************************/
@SuppressWarnings("serial")
public class ModelGUI extends GUIState implements Steppable {

    private javax.swing.JFrame myChartFrame;
    
    // Chart generators
    
	public ScatterPlotGenerator
		bankBalanceChart;

            
    protected ArrayList<TimeSeriesPlot> timeSeriesPlots;
    
    /** Create an instance of MySecondModelGUI */
    ModelGUI() { 
        super(new Model(1L));
        timeSeriesPlots = new ArrayList<TimeSeriesPlot>();
    }

    
    public void load(final SimState state) {
    	super.start();
    }
        
    /** Called once, to initialise display windows. */
    @Override
    public void init(Controller controller) {
        super.init(controller);
        myChartFrame = new JFrame("My Graphs");            		
        controller.registerFrame(myChartFrame);
       
        // Create a tab interface
        JTabbedPane newTabPane = new JTabbedPane();
        bankBalanceChart = makeScatterPlot(newTabPane, "Bank balances", "Balance", "Income");                
        
        timeSeriesPlots.add(
        		new TimeSeriesPlot("Some numbers of interest","Time (years)","Number")
        			.addVariable(state,"t", "the t variable")
    	);

        
        for(TimeSeriesPlot plot : timeSeriesPlots) {
        	plot.addToPane(newTabPane);
        }
        
        myChartFrame.add(newTabPane);
        
        myChartFrame.pack();
    }

    /** Called once, when the simulation is to begin. */
    @Override
    public void start() {
        super.start();

        // Add scatterplot data here
 //       addSeries(bankBalanceChart, "Bank balances", Household.diagnostics.bankBalData);

        // Execute when each time-step is complete
        scheduleRepeatingImmediatelyAfter(this);
    }
    
    /** Called after each simulation step. */
    @Override
    public void step(SimState state) {
        Model myModel = (Model)state;
        double t = myModel.schedule.getTime()/12.0;
        
        for(TimeSeriesPlot plot : timeSeriesPlots) {
        	plot.recordValues(t);
        }
    }
    
    
    
    /** Add titles and labels to charts. */

    private ScatterPlotGenerator makeScatterPlot(JTabbedPane pane, String title, String xAxis, String yAxis) {
    	ScatterPlotGenerator chart = ChartUtilities.buildScatterPlotGenerator(title, xAxis, yAxis);
        pane.addTab(title, chart);
        return(chart);
    }

    private void addSeries(ScatterPlotGenerator chart, String title, final double[][] data) {
    	ScatterPlotSeriesAttributes attributes = ChartUtilities.addSeries(chart, title);
        ChartUtilities.scheduleSeries(this, attributes, new sim.display.ChartUtilities.ProvidesDoubleDoubles() {
        	public double[][] provide() {
        		return(data);
        	}
        });
    }
    
    /** Called once, when the console quits. */
    @Override
   public void quit() {
        super.quit();
        myChartFrame.dispose();
    }

    // Java entry point
    public static void main(String[] args) {
        // Create a console for the GUI
        Console console = new Console(new ModelGUI());
        console.setVisible(true);
    }

    ////////////////////////////////////////////////////////////////////
    // Console stuff
    ////////////////////////////////////////////////////////////////////
	@Override
	public Object getSimulationInspectedObject() {
		return state;
	}

}
