package experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import utils.ExperimentUtils;
import utils.IOUtils;
import exec.Constants;

public class ExperimentSeriesStatistics {

    /**
     * Statistics for each experiment performed
     */
    private ArrayList<ExperimentStatistics> experimentStatistics = new ArrayList<ExperimentStatistics>();


    /**
     * Adds experiment data.
     * 
     * @param exStats
     *            {@link ExperimentStatistics} that will be added
     */
    public void addExperimentStatistics(ExperimentStatistics exStats) {
        this.experimentStatistics.add(exStats);
    }


    /**
     * Writes average DTM difference from experiments to CSV file
     * 
     * @param rootDir
     *            directory in which experiments are contained
     */
    public void writeDTMDiffToCsv(String rootDir) {

        // generate column names
        Vector<String> columnNames = new Vector<String>();
        for (int x = 1; x <= this.experimentStatistics.size(); x++) {
            columnNames.add("experiment" + x);
        }

        // get data from experiments
        Vector<Object> dtmDiff = new Vector<Object>();
        for (ExperimentStatistics stats : this.experimentStatistics) {
            dtmDiff.add(stats.getWhitesAverageDTMDiff());
        }

        // prepare correct data object
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        data.add(dtmDiff);

        // write to csv
        IOUtils.writeCSV(rootDir + "/" + IOUtils.WHITE_DTM_DIFFERENCE_FILE_NAME
                + ".csv", columnNames, data);
    }


    /**
     * Writes CSV with all output parameters.
     * 
     * @param filePath
     *            where file will be saved
     */
    public void writeUltimateCSV(String filePath) {
        IOUtils.writeCSV(filePath, this.buildAllColumnNames(), this
                .buildDataForCSV());
    }


    /**
     * Creates line chart of whites DTM difference per experiments parameter
     * under test value.
     * 
     * @param filepath
     *            file to which chart will be saved as jpg picture
     * @param parameter
     *            parameter that was tested
     */
    public void saveDTMDiffGraph(String filepath, MCTestParameter parameter) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String yAxisDescription = "Average DTM difference";
        for (int x = 0; x < this.experimentStatistics.size(); x++) {
            dataset.setValue(this.experimentStatistics.get(x)
                    .getWhitesAverageDTMDiff(), yAxisDescription,
                    Constants.testParameterValues.get(x));
        }

        String title = "Whites average DTM differnce";
        String testParam = ExperimentUtils.testParameterToString(parameter);
        JFreeChart chart = ChartFactory.createLineChart(title, testParam,
                yAxisDescription, dataset, PlotOrientation.VERTICAL, false,
                false, false);

        try {
            ChartUtilities.saveChartAsJPEG(new File(filepath), chart,
                    IOUtils.DEFAULT_GRAPH_WIDTH, IOUtils.DEFAULT_GRAPH_HEIGHT);
        }
        catch (IOException e) {
            System.err.println("Could not save " + filepath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Builds column names for "ultimate" csv file.
     * 
     * @return column names
     */
    private Vector<String> buildAllColumnNames() {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("Experiment");
        columnNames.add("\"average DTM difference\"");
        columnNames.add("\"average chess game length\"");
        columnNames.add("\"number of MCTS tree collapses\"");

        return columnNames;
    }


    /**
     * Build data for CSV file that contains all output parameters.
     * 
     * @return data for CSV file
     */
    private Vector<Vector<Object>> buildDataForCSV() {
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();

        for (int x = 0; x < this.experimentStatistics.size(); x++) {

            // create vector that represents on row in CSV file
            Vector<Object> row = new Vector<Object>();
            row.add(x);
            row.add(this.experimentStatistics.get(x).getWhitesAverageDTMDiff());
            row.add(this.experimentStatistics.get(x).getAverageGameLength());
            row.add(this.experimentStatistics.get(x).getNumberOfTreeCollapses());

            // add current row to data
            data.add(row);
        }

        return data;
    }

}
