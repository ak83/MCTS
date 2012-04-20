package utils;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;

public class ChartUtils {

    /** Category for number of MC tree collapses that will apeear in charts */
    public static final String NUMBER_OF_MC_TREE_COLLAPSES_CATHEGORY = "Number of MCTS tree collapses";

    /** Category for average DTM diff */
    public static final String DTM_DIFF_CATHEGORY                    = "Average DTM differnce from optimal move";

    /** Category for average tree size */
    public static final String TREE_SIZE_CATEGORY                    = "Average MCTS tree size";

    /** Category for game length */
    public static final String GAME_LENGTH_CATEGORY                  = "Game Length";

    /** Category for white success rate used in charts */
    public static final String WHITE_SUCCESS_RATE_CATEGORY           = "Whites success rate";

    /** Category axis name for chess games */
    public static final String CHESS_GAME_CATEGORY_AXIS              = "Chess game";

    /** Category for chess game wins/losses */
    public static final String DID_WHITE_LOOSE_CATEGORY              = "Did white loose";


    /**
     * Sets given arguments for plot at selected index
     * 
     * @param plot
     *            plot for which parameters will be set
     * @param dataset
     *            dataset that will be added
     * @param rangeAxisName
     *            Name of category for range Axis
     * @param renderer
     *            renderer that will be set
     * @param index
     *            index to which all previous parameters will be set
     */
    public static void addToPlot(CategoryPlot plot, CategoryDataset dataset,
            String rangeAxisName, CategoryItemRenderer renderer, int index) {
        ValueAxis rangeAxis = new NumberAxis(rangeAxisName);
        plot.setRangeAxis(index, rangeAxis);
        plot.setDataset(index, dataset);
        plot.mapDatasetToRangeAxis(index, index);
        plot.setRenderer(index, renderer);
    }

}
