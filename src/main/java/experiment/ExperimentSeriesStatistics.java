package experiment;

import java.util.ArrayList;
import java.util.Vector;

import mct.MCTNodeStatistics;
import moveChoosers.WhiteMoveChooser;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import utils.ChartUtils;
import utils.ExperimentUtils;
import utils.IOUtils;
import chess.chessgame.ChessGameStatistics;
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
        IOUtils.writeCSV(rootDir + "/" + IOUtils.WHITE_DTM_DIFFERENCE_FILE_NAME + ".csv", columnNames, data);
    }


    /**
     * Writes CSV with all output parameters.
     * 
     * @param filePath
     *            where file will be saved
     */
    public void writeUltimateCSV(String filePath) {
        IOUtils.writeCSV(filePath, this.buildAllColumnNames(), this.buildDataForCSV());
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

        ChartUtils.addToPlot(plot, this.buildDTMDataset(), ChartUtils.DTM_DIFF_CATHEGORY, new LineAndShapeRenderer(), 0);
        ChartUtils.addToPlot(plot, this.buildNumberOfCollapsesDataset(), ChartUtils.NUMBER_OF_MC_TREE_COLLAPSES_CATHEGORY, new LineAndShapeRenderer(), 1);
        ChartUtils.addToPlot(plot, this.buildAverageTreeSizeDataset(), ChartUtils.TREE_SIZE_CATEGORY, new LineAndShapeRenderer(), 2);
        ChartUtils.addToPlot(plot, this.buildAGLDataset(), ChartUtils.GAME_LENGTH_CATEGORY, new LineAndShapeRenderer(), 3);
        ChartUtils.addToPlot(plot, this.buildWSRDataset(), ChartUtils.WHITE_SUCCESS_RATE_CATEGORY, new LineAndShapeRenderer(), 4);

        CategoryAxis categoryAxis = new CategoryAxis(ExperimentUtils.testParameterToString(parameter));

        CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot(categoryAxis);
        combinedPlot.add(plot);

        JFreeChart chart = new JFreeChart(combinedPlot);
        IOUtils.saveChart(filePath, chart);

    }


    /**
     * Saves each output data into its own chart.
     * 
     * @param dir
     *            directory where charts will be saved
     */
    public void saveIndividualCharts(String dir) {
        String ending = ".jpg";
        String separator = "/";

        String categoryAxis = ExperimentUtils.testParameterToString(Constants.testParameter);

        // DTM diff
        ChartUtils.saveIndividualChart(dir + separator + IOUtils.WHITE_DTM_DIFFERENCE_FILE_NAME + ending, this.buildDTMDataset(),
                ChartUtils.DTM_DIFF_CATHEGORY, categoryAxis);

        // game length
        ChartUtils.saveIndividualChart(dir + separator + IOUtils.GAME_LENGTH_FILE_NAME + ending, this.buildAGLDataset(), ChartUtils.GAME_LENGTH_CATEGORY,
                categoryAxis);

        // collapses
        ChartUtils.saveIndividualChart(dir + separator + IOUtils.NUMBER_OF_MCTS_TREE_COLLAPSES_FILE_NAME + ending, this.buildNumberOfCollapsesDataset(),
                ChartUtils.NUMBER_OF_MC_TREE_COLLAPSES_CATHEGORY, categoryAxis);

        // tree size
        ChartUtils.saveIndividualChart(dir + separator + IOUtils.TREE_SIZE_FILE_NAME + ending, this.buildAverageTreeSizeDataset(),
                ChartUtils.TREE_SIZE_CATEGORY, categoryAxis);

        // WSR
        ChartUtils.saveIndividualChart(dir + separator + IOUtils.WHITE_SUCCESS_RATE_FILE_NAME + ending, this.buildWSRDataset(),
                ChartUtils.WHITE_SUCCESS_RATE_CATEGORY, categoryAxis);
    }


    /**
     * Gets average game length.
     * 
     * @return average number of turns per game in experiments series
     */
    public double getAverageGameLength() {
        int counter = 0;
        int total = 0;
        for (ExperimentStatistics experimentStats : this.experimentStatistics) {
            for (ChessGameStatistics gameStats : experimentStats.getChessGameStatistics()) {
                counter++;
                total += gameStats.getNumberOfTurnsMade();
            }
        }

        return total / (double) counter;
    }


    /**
     * Gets average DTM difference from optimal move.
     * 
     * @return average DTM difference from optimal move per ply
     */
    public double getAverageDTMDiff() {
        int counter = 0;
        int total = 0;
        for (ExperimentStatistics experimentStats : this.experimentStatistics) {
            for (ChessGameStatistics gameStats : experimentStats.getChessGameStatistics()) {
                for (Integer dtmDiff : gameStats.getWhitesDiffFromOptimal().values()) {
                    counter++;
                    total += dtmDiff;
                }
            }
        }

        return total / (double) counter;
    }


    /**
     * Gets whites success rate from all games in experiment series.
     * 
     * @return white success rate
     */
    public double getWhiteSuccessRate() {
        int counter = 0;
        int total = 0;
        for (ExperimentStatistics experimentStats : this.experimentStatistics) {
            for (ChessGameStatistics gameStats : experimentStats.getChessGameStatistics()) {
                if (gameStats.didWhiteWin()) {
                    total++;
                }
                counter++;
            }
        }

        return total / (double) counter;
    }


    /**
     * Gets average tree size per turn.
     * 
     * @return average MCTS tree size per turn
     */
    public double getAverageTreeSize() {
        int counter = 0;
        int total = 0;
        for (ExperimentStatistics experimentStats : this.experimentStatistics) {
            for (ChessGameStatistics gameStats : experimentStats.getChessGameStatistics()) {
                for (Integer treeSize : gameStats.getTreeSize().values()) {
                    counter++;
                    total += treeSize;
                }
            }
        }

        return total / (double) counter;
    }


    /**
     * Gets average number of tree collapses per chess game.
     * 
     * @return number of MCTS tree collapses per chess game.
     */
    public double getAverageNumberOfCollapses() {
        int counter = 0;
        int total = 0;
        for (ExperimentStatistics experimentStats : this.experimentStatistics) {
            for (ChessGameStatistics gameStats : experimentStats.getChessGameStatistics()) {
                counter++;
                total += gameStats.getNumberOfMCTSTreeCollapses();
            }
        }

        return total / (double) counter;
    }


    /**
     * Summarizes statistics.
     * 
     * @return {@link String} representation of statistics.
     */
    public String getSummary() {
        String rez = "";
        rez += "White success rate was " + this.getWhiteSuccessRate() + System.getProperty("line.separator");
        rez += "Average game length: " + this.getAverageGameLength() + System.getProperty("line.separator");
        rez += "Average DTM difference: " + this.getAverageDTMDiff() + System.getProperty("line.separator");
        rez += "Average MCTS tree size: " + this.getAverageTreeSize() + System.getProperty("line.separator");
        rez += "Number of MCTS tree collapses per game: " + this.getAverageNumberOfCollapses() + System.getProperty("line.separator");

        return rez;
    }


    /**
     * Calculates node related statistics.
     * 
     * @return node related statistics
     */
    public MCTNodeStatistics getNodeStatistics() {
        MCTNodeStatistics rez = new MCTNodeStatistics();
        for (ExperimentStatistics stats : this.experimentStatistics) {
            for (ChessGameStatistics chessStats : stats.getChessGameStatistics()) {
                rez.updateNodeStats(chessStats.getStatisticsOfMCTS().getNodeStatistics());
            }
        }

        return rez;
    }


    /**
     * Calculates statistics related to {@link MCTNode}s that were selected by
     * {@link WhiteMoveChooser}.
     * 
     * @return static related to nodes chosen by white player
     */
    public MCTNodeStatistics getNodesSelectedStatistics() {
        MCTNodeStatistics rez = new MCTNodeStatistics();
        for (ExperimentStatistics stats : this.experimentStatistics) {
            for (ChessGameStatistics chessStats : stats.getChessGameStatistics()) {
                rez.updateNodeStats(chessStats.getStatisticsOfMCTS().getNodesSelectedStatistics());
            }
        }

        return rez;
    }


    /**
     * Calculates statistics related to {@link MCTNode}s that were selected by
     * {@link WhiteMoveChooser}.
     * 
     * @return calculated statistics
     */
    public MCTNodeStatistics getSelectedNodeStatistics() {
        MCTNodeStatistics rez = new MCTNodeStatistics();
        for (ExperimentStatistics stats : this.experimentStatistics) {
            for (ChessGameStatistics chessStats : stats.getChessGameStatistics()) {
                rez.updateNodeStats(chessStats.getStatisticsOfMCTS().getNodeStatistics());
            }
        }

        return rez;
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
            row.add(this.experimentStatistics.get(x).getAverageWhiteSuccessRate());

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
            dataset.setValue(this.experimentStatistics.get(x).getWhitesAverageDTMDiff(), ChartUtils.DTM_DIFF_CATHEGORY, Constants.testParameterValues.get(x));
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
            dataset.setValue(this.experimentStatistics.get(x).getAverageWhiteSuccessRate(), ChartUtils.WHITE_SUCCESS_RATE_CATEGORY,
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
            dataset.setValue(this.experimentStatistics.get(x).getAverageGameLength(), ChartUtils.GAME_LENGTH_CATEGORY, Constants.testParameterValues.get(x));
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
            dataset.setValue(this.experimentStatistics.get(x).getAverageTreeSize(), ChartUtils.TREE_SIZE_CATEGORY, Constants.testParameterValues.get(x));
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
            dataset.setValue(this.experimentStatistics.get(x).getNumberOfTreeCollapses(), ChartUtils.NUMBER_OF_MC_TREE_COLLAPSES_CATHEGORY,
                    Constants.testParameterValues.get(x));
        }

        return dataset;
    }

}
