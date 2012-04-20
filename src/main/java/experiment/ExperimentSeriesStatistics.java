package experiment;

import java.util.ArrayList;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import utils.ExperimentUtils;
import utils.IOUtils;
import utils.ChartUtils;
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

        IOUtils.saveChart(filepath, chart);
    }


    /**
     * Saves Chart with every every output data
     * 
     * @param filePath
     *            file where chart will be saved as jpg picture
     * @param parameter
     *            parameter that was tested in experiment series
     */
    public void saveUltimateChart(String filePath, MCTestParameter parameter) {
        CategoryPlot plot = new CategoryPlot();

        ChartUtils.addToPlot(plot, this.buildDTMDataset(),
                ChartUtils.DTM_DIFF_CATHEGORY, new LineAndShapeRenderer(),
                0);
        ChartUtils.addToPlot(plot, this.buildNumberOfCollapsesDataset(),
                ChartUtils.NUMBER_OF_MC_TREE_COLLAPSES_CATHEGORY,
                new LineAndShapeRenderer(), 1);
        ChartUtils.addToPlot(plot, this.buildAverageTreeSizeDataset(),
                ChartUtils.TREE_SIZE_CATEGORY, new LineAndShapeRenderer(),
                2);
        ChartUtils.addToPlot(plot, this.buildAGLDataset(),
                ChartUtils.GAME_LENGTH_CATEGORY,
                new LineAndShapeRenderer(), 3);
        ChartUtils.addToPlot(plot, this.buildWSRDataset(),
                ChartUtils.WHITE_SUCCESS_RATE_CATEGORY,
                new LineAndShapeRenderer(), 4);

        CategoryAxis categoryAxis = new CategoryAxis(ExperimentUtils
                .testParameterToString(parameter));

        CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot(
                categoryAxis);
        combinedPlot.add(plot);

        JFreeChart chart = new JFreeChart(combinedPlot);
        IOUtils.saveChart(filePath, chart);

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
        columnNames.add("\"average MCTS tree size\"");
        columnNames.add("\"White success rate\"");

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
            row.add(this.experimentStatistics.get(x).getAverageTreeSize());
            row.add(this.experimentStatistics.get(x)
                    .getAverageWhiteSuccessRate());

            // add current row to data
            data.add(row);
        }

        return data;
    }


    /**
     * Generates dataset from experiments for DTM difference where category axis
     * values are experiments test parameter values.
     * 
     * @return {@link CategoryDataset} that represents DTM difference from
     *         optimal move.
     */
    private CategoryDataset buildDTMDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.experimentStatistics.size(); x++) {
            dataset.setValue(this.experimentStatistics.get(x)
                    .getWhitesAverageDTMDiff(),
                    ChartUtils.DTM_DIFF_CATHEGORY,
                    Constants.testParameterValues.get(x));
        }

        return dataset;
    }


    /**
     * Generates dataset from experiments for white success rate where category
     * axis values are experiments test parameter values.
     * 
     * @return {@link CategoryDataset} that represents white success rate per
     *         experiment test parameter value
     */
    private CategoryDataset buildWSRDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.experimentStatistics.size(); x++) {
            dataset.setValue(this.experimentStatistics.get(x)
                    .getAverageWhiteSuccessRate(),
                    ChartUtils.WHITE_SUCCESS_RATE_CATEGORY,
                    Constants.testParameterValues.get(x));
        }

        return dataset;
    }


    /**
     * Generates {@link DefaultCategoryDataset} from experiments for average
     * game length per experiment where category axis values are experiments
     * test parameter values.
     * 
     * @return {@link CategoryDataset} that represents average game length per
     *         experiment test parameter value
     */
    private CategoryDataset buildAGLDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.experimentStatistics.size(); x++) {
            dataset.setValue(this.experimentStatistics.get(x)
                    .getAverageGameLength(),
                    ChartUtils.GAME_LENGTH_CATEGORY,
                    Constants.testParameterValues.get(x));
        }

        return dataset;
    }


    /**
     * Generates {@link DefaultCategoryDataset} from experiments for average
     * tree size per experiment where category axis values are experiments test
     * parameter values.
     * 
     * @return {@link DefaultCategoryDataset} that represents average tree size
     *         per experiment test parameter value
     */
    private CategoryDataset buildAverageTreeSizeDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.experimentStatistics.size(); x++) {
            dataset.setValue(this.experimentStatistics.get(x)
                    .getAverageTreeSize(), ChartUtils.TREE_SIZE_CATEGORY,
                    Constants.testParameterValues.get(x));
        }

        return dataset;
    }


    /**
     * Generates {@link DefaultCategoryDataset} from experiments for number of
     * tree collapses per experiment where category axis values are experiments
     * test parameter values.
     * 
     * @return {@link DefaultCategoryDataset} that represents number of tree
     *         collapses per experiment test parameter value
     */
    private CategoryDataset buildNumberOfCollapsesDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.experimentStatistics.size(); x++) {
            dataset.setValue(this.experimentStatistics.get(x)
                    .getNumberOfTreeCollapses(),
                    ChartUtils.NUMBER_OF_MC_TREE_COLLAPSES_CATHEGORY,
                    Constants.testParameterValues.get(x));
        }

        return dataset;
    }

}
