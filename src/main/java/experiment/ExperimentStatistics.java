package experiment;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

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
        // names for columns in csv file (ChessGame1, ChessGame2,....)
        StringBuffer sbColumnNames = new StringBuffer();

        // data (only one row) for each column
        StringBuffer sbRow = new StringBuffer();

        int x = 1;
        for (ChessGameStatistics stats : this.chessGameStatistics) {
            sbColumnNames.append("ChessGame" + x + "\t");
            sbRow.append(stats.getAverageWhitesDTMDiff() + "\t");
            x++;
        }

        try {
            FileWriter fw = new FileWriter(new File(filePath));
            fw.write(sbColumnNames.toString() + "\r\n" + sbRow.toString());
            fw.close();
        }
        catch (Exception e) {
            System.err.println("Could not write to file " + filePath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

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
        // names for columns in csv file (ChessGame1, ChessGame2,....)
        StringBuffer sbColumnNames = new StringBuffer();

        // data (only one row) for each column
        StringBuffer sbRow = new StringBuffer();

        int x = 1;
        for (ChessGameStatistics stats : this.chessGameStatistics) {
            sbColumnNames.append("ChessGame" + x + "\t");
            sbRow.append(stats.getAverageTreeSize() + "\t");
            x++;
        }

        try {
            FileWriter fw = new FileWriter(new File(filePath));
            fw.write(sbColumnNames.toString() + "\r\n" + sbRow.toString());
            fw.close();
        }
        catch (Exception e) {
            System.err.println("Could not write file " + filePath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

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
     * Create chart with number of MCTS tree collapses, dtm diff and average
     * tree size (per match).
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

        JFreeChart chart = new JFreeChart("", combinedPlot);

        IOUtils.saveChart(filePath, chart);
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
     * Builds {@link CategoryDataset} of number of MC tree collapse per match.
     * 
     * @return {@link CategoryDataset} that represent number of MCtree collapses
     */
    private CategoryDataset getNumberOfCollapsesDataSet() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int x = 0; x < this.chessGameStatistics.size(); x++) {
            dataset.setValue(this.chessGameStatistics.get(x)
                    .getStatisticsOfMCTS().numberOfMCTreeColapses,
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

}
