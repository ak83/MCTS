package experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import utils.IOUtils;
import utils.StatisticsUtils;
import chess.chessgame.ChessGameStatistics;

/**
 * Class holds and calculates statistics data for experiments
 * 
 * @author Andraz
 */
public class ExperimentStatistics {

    private ArrayList<ChessGameStatistics> chessGameStatistics = new ArrayList<ChessGameStatistics>();


    /**
     * Add new statistics for chess game
     * 
     * @param statistics
     *            {@link ChessGameStatistics} to be added
     */
    public void addChessGameStatistics(ChessGameStatistics statistics) {
        this.chessGameStatistics.add(statistics);
    }


    /**
     * Gets average DTM difference for experiment that this statistics belong
     * to.
     * 
     * @return average DTM difference
     */
    public double getWhitesAverageDTMDiff() {
        int moveCounter = 0;
        int totalDTMDiff = 0;

        for (ChessGameStatistics stats : this.chessGameStatistics) {
            for (Integer dtmDiff : stats.getWhitesDiffFromOptimal().values()) {
                moveCounter++;
                totalDTMDiff += dtmDiff;
            }
        }

        return totalDTMDiff / (double) moveCounter;
    }


    /**
     * Writes average DTM difference to CSV file.
     * 
     * @param filePath
     *            file to which DTM differences will be saved
     */
    public void writeAverageDTMDiffToCVS(String filePath) {

        Vector<Object> dtmDiff = new Vector<Object>();
        for (ChessGameStatistics stats : this.chessGameStatistics) {
            dtmDiff.add(stats.getAverageWhitesDTMDiff());
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        data.add(dtmDiff);

        IOUtils.writeCSV(filePath, this.buildChessGameColumnNames(), data);
    }


    /**
     * Creates line chart of average dtm difference per chess game
     * 
     * @param filePath
     *            file to which graph will be saved as jpg picture
     */
    public void saveWhiteDTMDifferenceGraph(String filePath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.chessGameStatistics.size(); x++) {
            dataset.setValue(this.chessGameStatistics.get(x)
                    .getAverageWhitesDTMDiff(), "whites DTM difference",
                    (x + 1) + "");
        }

        String title = "Whites average DTM difference from optimal move";
        JFreeChart chart = ChartFactory.createLineChart(title, "Chess game",
                "average DTM difference", dataset, PlotOrientation.VERTICAL,
                false, false, false);

        IOUtils.saveChart(filePath, chart);
    }


    /**
     * Writes average MC tree size to CSV file.
     * 
     * @param filePath
     *            file to which DTM differences will be saved
     */
    public void writeAverageTreeSizeToCVS(String filePath) {

        // build vector and fill it with tree sizes
        Vector<Object> treeSizes = new Vector<Object>();
        for (ChessGameStatistics stats : this.chessGameStatistics) {
            treeSizes.add(stats.getAverageTreeSize());
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        data.add(treeSizes);

        IOUtils.writeCSV(filePath, this.buildChessGameColumnNames(), data);
    }


    /**
     * Calculates average game length.
     * 
     * @return average game length
     */
    public double getAverageGameLength() {
        int totalLength = 0;

        for (ChessGameStatistics stats : this.chessGameStatistics) {
            totalLength += stats.getNumberOfPliesMade();
        }

        return (double) (totalLength / (double) this.chessGameStatistics.size()) / 2.0d;
    }


    /**
     * Gets total number of all collapses that happened in experiments chess
     * games.
     * 
     * @return total number of chess games
     */
    public int getNumberOfTreeCollapses() {
        int total = 0;
        for (ChessGameStatistics stats : this.chessGameStatistics) {
            total += stats.getNumberOfMCTSTreeCollapses();
        }

        return total;
    }


    /**
     * Creates chart with number of MCTS tree collapses, dtm difference and
     * average tree size (per match).
     * 
     * @param filePath
     *            file to which chart will be saved as jpg picture
     */
    public void saveCollapsesDTMTreeSizechart(String filePath) {

        CategoryPlot plot = new CategoryPlot();
        plot.setDomainGridlinesVisible(true);

        StatisticsUtils.addToPlot(plot, this.getNumberOfCollapsesDataSet(),
                StatisticsUtils.NUMBER_OF_MC_TREE_COLLAPSES_CATHEGORY,
                new BarRenderer(), 2);

        StatisticsUtils.addToPlot(plot, this.getDTMDiffDataset(),
                StatisticsUtils.DTM_DIFF_CATHEGORY, new LineAndShapeRenderer(),
                0);

        StatisticsUtils.addToPlot(plot, this.getAverageTreeSizeDataset(),
                StatisticsUtils.TREE_SIZE_CATEGORY, new LineAndShapeRenderer(),
                1);

        CategoryAxis categoryAxis = new CategoryAxis("Chess game");
        CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot(
                categoryAxis);

        combinedPlot.add(plot);

        JFreeChart chart = new JFreeChart(combinedPlot);

        IOUtils.saveChart(filePath, chart);
    }


    /**
     * Creates chart with game length per individual chess game.
     * 
     * @param filePath
     *            file where chart will be saved as jpg picture
     */
    public void saveGameLengthPerGameChart(String filePath) {
        CategoryPlot plot = new CategoryPlot();
        StatisticsUtils.addToPlot(plot, this.getGameLengthPerGameDataset(),
                "Chess game length (in turns)", new LineAndShapeRenderer(), 0);

        CategoryAxis categoryAxis = new CategoryAxis("Chess game");
        CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot(
                categoryAxis);
        combinedPlot.add(plot);

        JFreeChart chart = new JFreeChart(combinedPlot);
        IOUtils.saveChart(filePath, chart);
    }


    /**
     * Save histogram of chess game length.
     * 
     * @param filePath
     *            file where chart will be saved as jpg picture
     */
    public void saveGameLengthHistogram(String filePath) {
        // key is game length, value is number of chess game of that length
        HashMap<Integer, Integer> gamesPerLength = new HashMap<Integer, Integer>();

        // will hashmap with game length counters
        for (ChessGameStatistics stats : this.chessGameStatistics) {
            // turns made in game
            int gameLength = stats.getNumberOfTurnsMade();
            if (gamesPerLength.get(gameLength) == null) {
                gamesPerLength.put(gameLength, 1);
            }
            else {
                // if game with current game length exists we increase counter
                // of such games
                gamesPerLength.put(gameLength,
                        gamesPerLength.get(gameLength) + 1);
            }
        }

        // sort keys
        ArrayList<Integer> sortedKeys = new ArrayList<Integer>(
                new TreeSet<Integer>(gamesPerLength.keySet()));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Integer gameLength : sortedKeys) {
            dataset.addValue(gamesPerLength.get(gameLength), "number of games",
                    gameLength);
        }

        CategoryPlot plot = new CategoryPlot();
        StatisticsUtils.addToPlot(plot, dataset, "", new BarRenderer(), 0);

        CategoryAxis categoryAxis = new CategoryAxis("Chess game length");
        CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot(
                categoryAxis);
        combinedPlot.add(plot);

        JFreeChart chart = new JFreeChart(combinedPlot);

        IOUtils.saveChart(filePath, chart);

    }


    /**
     * Writes number of MCTS tree collapses per match to CSV file.
     * 
     * @param filePath
     *            file to which number of collapses will be saved.
     */
    public void writeNumberOfCollapsesToCSV(String filePath) {

        Vector<String> columnNames = buildChessGameColumnNames();

        Vector<Object> numberOfCollapses = new Vector<Object>();
        for (ChessGameStatistics stats : this.chessGameStatistics) {
            numberOfCollapses.add(stats.getNumberOfMCTSTreeCollapses());
        }

        // prepare correct data object
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        data.add(numberOfCollapses);

        IOUtils.writeCSV(filePath, columnNames, data);
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
     * Create chart with number of MCTS tree collapses and DTM diff (per match).
     * 
     * @param filePath
     *            file to which chart will be saved as jpg picture
     */
    public void saveCollapseDTMChart(String filePath) {
        CategoryPlot plot = new CategoryPlot();
        plot.setDomainGridlinesVisible(true);

        StatisticsUtils.addToPlot(plot, this.getDTMDiffDataset(),
                StatisticsUtils.DTM_DIFF_CATHEGORY, new LineAndShapeRenderer(),
                0);
        StatisticsUtils.addToPlot(plot, this.getNumberOfCollapsesDataSet(),
                StatisticsUtils.NUMBER_OF_MC_TREE_COLLAPSES_CATHEGORY,
                new BarRenderer(), 1);

        CategoryAxis categoryAxis = new CategoryAxis("Chess game");
        CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot(
                categoryAxis);
        combinedPlot.add(plot);

        JFreeChart chart = new JFreeChart(combinedPlot);

        IOUtils.saveChart(filePath, chart);
    }


    /**
     * @return
     */
    private Vector<String> buildChessGameColumnNames() {
        Vector<String> columnNames = new Vector<String>();
        for (int x = 1; x <= this.chessGameStatistics.size(); x++) {
            columnNames.add("ChessGame" + x);
        }
        return columnNames;
    }


    /**
     * Builds column names for csv file that contains all output parameters.
     * 
     * @return column names
     */
    private Vector<String> buildAllColumnNames() {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("ChessGame");
        columnNames.add("\"Winning side\"");
        columnNames.add("\"game length\"");
        columnNames.add("\"average DTM difference\"");
        columnNames.add("\"number of MCTS tree collapses\"");
        columnNames.add("\"average MCTS tree size\"");
        return columnNames;

    }


    /**
     * Build data for CSV file that contains all output parameters.
     * 
     * @return data for CSV file
     */
    private Vector<Vector<Object>> buildDataForCSV() {
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();

        for (int x = 0; x < this.chessGameStatistics.size(); x++) {
            // build a row of values
            Vector<Object> values = new Vector<Object>();
            values.add(x);
            values.add(this.chessGameStatistics.get(x).getVictor());
            values.add(this.chessGameStatistics.get(x).getNumberOfPliesMade() / 2);
            values.add(this.chessGameStatistics.get(x)
                    .getAverageWhitesDTMDiff());
            values.add(this.chessGameStatistics.get(x)
                    .getNumberOfMCTSTreeCollapses());
            values.add(this.chessGameStatistics.get(x).getAverageTreeSize());

            // add row to data
            data.add(values);
        }

        return data;
    }


    /**
     * Builds {@link CategoryDataset} of number of MC tree collapse per match.
     * 
     * @return {@link CategoryDataset} that represent number of MCtree collapses
     */
    private CategoryDataset getNumberOfCollapsesDataSet() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.chessGameStatistics.size(); x++) {
            dataset.setValue(this.chessGameStatistics.get(x)
                    .getNumberOfMCTSTreeCollapses(),
                    StatisticsUtils.NUMBER_OF_MC_TREE_COLLAPSES_CATHEGORY,
                    (x + 1) + "");
        }

        return dataset;
    }


    /**
     * Builds {@link CategoryDataset} for average DTM difference per match.
     * 
     * @return {@link CategoryDataset} that represents average DTM difference
     *         from optimal move
     */
    private CategoryDataset getDTMDiffDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.chessGameStatistics.size(); x++) {
            dataset.setValue(this.chessGameStatistics.get(x)
                    .getAverageWhitesDTMDiff(),
                    StatisticsUtils.DTM_DIFF_CATHEGORY, (x + 1) + "");
        }
        return dataset;
    }


    /**
     * Builds {@link CategoryDataset} for average MCTS tree site per chess game.
     * 
     * @return {@link CategoryDataset} that represents average tree size per
     *         chess game
     */
    private CategoryDataset getAverageTreeSizeDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.chessGameStatistics.size(); x++) {
            dataset.setValue(this.chessGameStatistics.get(x)
                    .getAverageTreeSize(), StatisticsUtils.TREE_SIZE_CATEGORY,
                    (x + 1) + "");
        }
        return dataset;
    }


    /**
     * Builds {@link CategoryDataset} for individual chess game length.
     * 
     * @return {@link CategoryDataset} that represents individual chess game
     *         length(in turns)
     */
    private CategoryDataset getGameLengthPerGameDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.chessGameStatistics.size(); x++) {
            dataset.setValue(this.chessGameStatistics.get(x)
                    .getNumberOfTurnsMade(),
                    StatisticsUtils.GAME_LENGTH_CATEGORY, (x + 1) + "");
        }

        return dataset;
    }

}
