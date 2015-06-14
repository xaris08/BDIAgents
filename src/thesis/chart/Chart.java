package thesis.chart;

import java.awt.*;
import org.jfree.ui.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;
import org.jfree.chart.axis.NumberAxis;

public class Chart extends ApplicationFrame {

	private int numberOfHosts = 4;
	private XYSeries[] series = new XYSeries[numberOfHosts];

	public Chart(final String title) {
		super(title);
		for (int i=0; i<numberOfHosts; i++){
			series[i] = new XYSeries("PM"+(i+1));
		}
	}

	public void addToDataset(int hostId, float time, int usage) {
		
		for (int i=0; i<numberOfHosts; i++){
			if (hostId==i){
				series[i].add(time, usage);
			}
		}
	}

	private XYDataset createDataset(){
		final XYSeriesCollection dataset = new XYSeriesCollection();
		for (int i=0; i<numberOfHosts; i++){
			dataset.addSeries(series[i]);
		}

		return dataset;
	}
	
	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"PM Resources",
				"Time (sec)", "CPU Usage (MIPS)", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		
		chart.setBackgroundPaint(Color.white);
		
		final XYPlot[] plot = new XYPlot[numberOfHosts];
		
		for (int i=0; i<numberOfHosts; i++){
			plot[i] = chart.getXYPlot();
			plot[i].setBackgroundPaint(Color.lightGray);
			plot[i].setDomainGridlinePaint(Color.white);
			plot[i].setRangeGridlinePaint(Color.white);
		}

		return chart;
	}

	public void initialise() {
		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(620, 370));
		setContentPane(chartPanel);
	}

}