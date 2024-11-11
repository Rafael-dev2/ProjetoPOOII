package Utils;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import Models.Candidate;


public class BarChart {
    private DefaultCategoryDataset dataset;
    private JFreeChart bChart;

    private DefaultCategoryDataset createDataset(List<Candidate> candidatesList){
        dataset = new DefaultCategoryDataset();

        for (Candidate candidate : candidatesList) {
            dataset.addValue(candidate.getVotesCount(), candidate.getName(), "");
        }

        return dataset;
    };

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        bChart = ChartFactory.createBarChart("Eleições", 
                                            "", 
                                            "Número de votos", 
                                            dataset,
                                            PlotOrientation.VERTICAL,
                                            true,
                                            false,
                                            false);

        return bChart;
    };

    public ChartPanel createPanel (List<Candidate> candidatesList) { 
        dataset = this.createDataset(candidatesList);

        JFreeChart chart = this.createChart(dataset);
        
        ChartPanel panel = new ChartPanel(chart);
        
        panel.setVisible(true);
        panel.setSize(200, 300);

        return panel;
    };

    public void updateDataset (List<Candidate> repository) {
        dataset.clear();

        for (Candidate candidate : repository) {
            dataset.addValue(candidate.getVotesCount(), candidate.getName(), "");
        }
    }
}
