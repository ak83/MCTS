package experiment;

import java.util.Vector;

import utils.IOUtils;

public class ExperimentSeriesStatistics {

    /**
     * Statistics for each experiment performed
     */
    private Vector<ExperimentStatistics> experimentStatistics = new Vector<ExperimentStatistics>();


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
        IOUtils.writeCSV(rootDir + "/experimentDTMDiff.csv", columnNames, data);
    }

}
