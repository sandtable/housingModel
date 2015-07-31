package development;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jfree.data.xy.XYSeries;

import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Valuable;
import sim.util.media.chart.ScatterPlotGenerator;
import sim.util.media.chart.ScatterPlotSeriesAttributes;
import sim.util.media.chart.TimeSeriesAttributes;
import sim.util.media.chart.TimeSeriesChartGenerator;
import sim.display.ChartUtilities;
import utilities.DataRecorder;

/********************************************
 * Interface to the Mason Graphic Interface for the housing market simulation.
 * This object contains the "main" function, which crates a ModelGUI instance.
 * This, in turn, creates an instance of the Model class, which actually does
 * the simulation.
 * 
 * @author daniel
 *
 ********************************************/
@SuppressWarnings("serial")
public class ModelGUI extends GUIState implements Steppable {

	public ModelRoot root;

	// Chart generators
  
	public ScatterPlotGenerator
		housingChart,
		housePriceChart,
//		bankBalanceChart,
		mortgageStatsChart,
		mortgagePhaseChart;
    
    public TimeSeriesChartGenerator 
    	marketStats,
    	tenure;
    public TimeSeriesAttributes 	
    	hpi,
     	daysOnMarket,
     	nHouseholds,
     	nPrivateHousing,
     	ownerOccupier;
     	;
    
    
    /** Create an instance of MySecondModelGUI */
    ModelGUI() { 
        super(new Model(1L));
    }

    
        
    /** Called once, to initialise display windows. */
    @Override
    public void init(Controller controller) {
        super.init(controller);
        
        root = ((ModelBase)state).root;
        
        marketStats = ChartUtilities.buildTimeSeriesChartGenerator(this, "Market statistics", "Time");
        marketStats.setYAxisLabel("Index");
        hpi = ChartUtilities.addSeries(marketStats, "House Price Index");
        daysOnMarket = ChartUtilities.addSeries(marketStats, "Days on market");

        tenure = ChartUtilities.buildTimeSeriesChartGenerator(this, "Household tenure", "Time");
        marketStats.setYAxisLabel("Number of households");
        ownerOccupier = ChartUtilities.addSeries(tenure, "Owner Occupiers");
        nPrivateHousing = ChartUtilities.addSeries(tenure, "Private Rental");
        nHouseholds = ChartUtilities.addSeries(tenure, "Social housing");

//        housingChart = ChartUtilities.buildScatterPlotGenerator("Housing Tenure", "Probability", "Household income");
        
//        myAttributes = ChartUtilities.addSeries(myChart, "The Time Series of Interest");
        
        
//        myChartFrame = new JFrame("My Graphs");            		
 //       controller.registerFrame(myChartFrame);
       
        // Create a tab interface
  //      JTabbedPane newTabPane = new JTabbedPane();
        /***
//        housingChart = makeScatterPlot(newTabPane, "Housing stats", "Probability", "Household Income");
        housePriceChart = makeScatterPlot(newTabPane, "House prices", "Modelled Price", "Reference Price");
//        bankBalanceChart = makeScatterPlot(newTabPane, "Bank balances", "Balance", "Income");
        mortgageStatsChart = makeScatterPlot(newTabPane, "Mortgage stats", "Frequency", "Ratio");
        mortgagePhaseChart = makeScatterPlot(newTabPane, "Mortgage phase",  "Down-payment/Income", "Loan to Income ratio");
        mortgagePhaseChart.setXAxisRange(0.0, 8.0);
        mortgagePhaseChart.setYAxisRange(0.0, 8.0);
            **/
        /**
        timeSeriesPlots.add(
        		new TimeSeriesPlot("Market Statistics","Time (years)","Value")
        			.addVariable(Model.housingMarket,"housePriceIndex", "HPI")
        			.addVariable(Model.housingMarket,"averageDaysOnMarket", "Years on market", new DataRecorder.Transform() {
        				public double exec(double x) {return(Math.min(x/360.0, 1.0));}
        			})
        			.addVariable(Collectors.housingMarketStats, "nBuyers", "Buyers (1000s)", new DataRecorder.Transform() {
        				public double exec(double x) {return(x/1000.0);}
        			})
//        			.addVariable(HousingMarketTest.housingMarket.diagnostics,"averageSoldPriceToOLP", "Sold Price/List price")
        			.addVariable(Collectors.creditSupply,"affordability", "Affordability (Mortgage-payment/income)")
    	);

        timeSeriesPlots.add(
        		new TimeSeriesPlot("Bid/Offer quantities","Time (years)","Number")
        			.addVariable(Collectors.housingMarketStats,"nSales", "Transactions")
        			.addVariable(Collectors.housingMarketStats,"nSellers", "Sellers")
        			.addVariable(Collectors.housingMarketStats,"nBuyers", "Buyers")
    	);
        
        timeSeriesPlots.add(
        		new TimeSeriesPlot("Bid/Offer Prices","Time (years)","Price")
        			.addVariable(Collectors.housingMarketStats,"averageBidPrice", "Average Bid Price")
        			.addVariable(Collectors.housingMarketStats,"averageOfferPrice", "Average Offer Price")        			
        );

//        timeSeriesPlots.add(
//        		new TimeSeriesPlot("Affordability","Time (years)","mortgage payment/income")
//        			.addVariable(HousingMarketTest.bank.diagnostics,"affordability", "Affordability")
//    	);

        timeSeriesPlots.add(
        		new TimeSeriesPlot("Tenure quantities","Time (years)","number")
        			.addVariable(Collectors.householdStats,"nHomeless", "Social-Housing")
        			.addVariable(Collectors.householdStats,"nNonOwner", "Non Owners")
        			.addVariable(Collectors.householdStats,"nEmpty", "Empty Houses")
        			.addVariable(Collectors.householdStats,"nHouseholds", "Total")
    	);
        
        for(TimeSeriesPlot plot : timeSeriesPlots) {
        	plot.addToPane(newTabPane);
        }
        **/
   //     myChartFrame.add(newTabPane);
        
    //    myChartFrame.pack();
    }

    /** Called once, when the simulation is to begin. */
    @Override
    public void start() {
        super.start();
        
        marketStats.clearAllSeries(); /// NOTE THIS ISN'T IN LOAD(...)
        tenure.clearAllSeries();
        
        scheduleSeries(this, hpi, new sim.util.Valuable() {
        	public double doubleValue() {
        		//return root.get(HouseSaleMarket.class).housePriceIndex; }});
        		return root.mustGet(HouseSaleMarket.class).housePriceIndex; }});
        
        scheduleSeries(this, daysOnMarket, new sim.util.Valuable() {
        	public double doubleValue() {
        		return root.mustGet(HouseSaleMarket.class).averageDaysOnMarket.value(); }});

        scheduleSeries(this, nHouseholds, new sim.util.Valuable() {
        	public double doubleValue() {
        		return root.mustGet(HouseholdStats.class).nHouseholds; }});
        scheduleSeries(this, ownerOccupier, new sim.util.Valuable() {
        	public double doubleValue() {
        		return root.mustGet(HouseholdStats.class).nOwnerOccupier; }});
        scheduleSeries(this, nPrivateHousing, new sim.util.Valuable() {
        	public double doubleValue() {
        		return root.mustGet(HouseholdStats.class).nPrivateHousing; }});

        /***
        addSeries(housePriceChart, "Modelled prices", Collectors.housingMarketStats.priceData);
        addSeries(mortgageStatsChart, "Owner-occupier LTV distribution", Collectors.creditSupply.oo_ltv_distribution);
        addSeries(mortgageStatsChart, "Owner-occupier LTI distribution (x0.1)", Collectors.creditSupply.oo_lti_distribution);
        addSeries(mortgagePhaseChart, "Approved mortgages", Collectors.creditSupply.approved_mortgages);
        
        housePriceChart.addSeries(Collectors.housingMarketStats.referencePriceData, "Reference price", null);
***/
        // Execute when each time-step is complete
 //       scheduleRepeatingImmediatelyAfter(this);

    }

    public void load(final SimState state) {
    	super.start();

    	ChartUtilities.scheduleSeries(this, hpi, new sim.util.Valuable() {
        	public double doubleValue() {
        		return root.get(HouseSaleMarket.class).housePriceIndex; }});
        ChartUtilities.scheduleSeries(this, daysOnMarket, new sim.util.Valuable() {
        	public double doubleValue() {
        		return root.get(HouseSaleMarket.class).housePriceIndex; }});
    	
    }

    
    /** Called after each simulation step. */

    @Override
    public void step(SimState state) {
        ModelBase myModel = (ModelBase)state;
//        myModel.collectors.step();
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

    /***
    private TimeSeriesChartGenerator makeTimeSeriesChart(JTabbedPane pane, String title, String yAxis) {
    	TimeSeriesChartGenerator chart = ChartUtilities.buildTimeSeriesChartGenerator(title, yAxis);
        pane.addTab(title, chart);
        return(chart);
    }


    private void addSeries(TimeSeriesChartGenerator chart, String title, Valuable val) {
    	TimeSeriesAttributes attributes = ChartUtilities.addSeries(chart, title);
        ChartUtilities.scheduleSeries(this, attributes, val);
    }
     ***/
    
    /** Called once, when the console quits. */
    @Override
   public void quit() {
        super.quit();
//        myChartFrame.dispose();
//        state.kill();
    }
    

    /**
     * Java entry point. This is what's called when we begin an execution
     * @param args
     */
    public static void main(String[] args) {
        // Create a console for the GUI
        Console console = new Console(new ModelGUI());
        console.setVisible(true);
    }

    ////////////////////////////////////////////////////////////////////
    // Console stuff
    ////////////////////////////////////////////////////////////////////
    /**
     * MASON stuff
     */
	@Override
	public Object getSimulationInspectedObject() {
		return state;
	}
	
	  public static Stoppable scheduleSeries(final GUIState state, final TimeSeriesAttributes attributes, 
		        final Valuable valueProvider)
		        {
		        return state.state.schedule.scheduleRepeating(new Steppable()
		            {
		            final XYSeries series = attributes.getSeries();
		            double last = state.state.schedule.BEFORE_SIMULATION;
		            public void step(SimState state)
		                {
		                final double x = state.schedule.getTime();
		                if (x > last && x >= state.schedule.EPOCH && x < state.schedule.AFTER_SIMULATION)
		                    {
		                    last = x;
		                    final double value = (valueProvider == null) ? Double.NaN : valueProvider.doubleValue();
		                                        
		                    // JFreeChart isn't synchronized.  So we have to update it from the Swing Event Thread
		                    SwingUtilities.invokeLater(new Runnable()
		                        {
		                        public void run()
		                            {
		                            attributes.possiblyCull();
		                            series.add(x, value, true);
		                            }
		                        });
		                    // this will get pushed on the swing queue late
		                    attributes.getGenerator().updateChartLater(state.schedule.getSteps());
		                    }
		                }
		            }, 1.0);
		        }

}
