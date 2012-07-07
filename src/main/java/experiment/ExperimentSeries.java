package experiment;

import java.io.File;
import java.util.logging.Logger;

import config.MCTSSetup;

import logging.Logs;
import utils.ExperimentUtils;
import utils.IOUtils;
import utils.Utils;

/** Class that handles running multiple experiments */
public class ExperimentSeries {

    private static Logger              log;

    /** Statistic for this series of experiments */
    private ExperimentSeriesStatistics stasts = new ExperimentSeriesStatistics();


    /**
     * Run experiments defined in configuration file
     */
    public void runExperiments() {
        String rootDir = ExperimentUtils.testParameterToString(MCTSSetup.testParameter) + "--" + Utils.today();

        new File(rootDir).mkdir();

        // run experiment for each test parameter value
        for (int x = 0; x < MCTSSetup.testParameterValues.size(); x++) {

            Double parameterValue = MCTSSetup.testParameterValues.get(x);
            ExperimentUtils.setTestParameter(MCTSSetup.testParameter, parameterValue);

            Experiment experiment = new Experiment(rootDir + File.separator + "experiment" + x);
            experiment.runExperiment();
            this.stasts.addExperimentStatistics(experiment.getExperimentStats());
        }

        // save statistics outputs
        this.stasts.writeDTMDiffToCsv(rootDir);
        this.stasts.saveUltimateChart(rootDir + File.separator + IOUtils.ULTIMATE_FILE_NAME + ".jpg", MCTSSetup.testParameter);

        this.stasts.writeUltimateCSV(rootDir + File.separator + IOUtils.ULTIMATE_FILE_NAME + ".csv");

        this.stasts.saveIndividualCharts(rootDir);

        // summarize statistics in log file
        Logs.initLoggers(rootDir);
        ExperimentSeries.log = Logger.getLogger("MCTS.Main");

        // write summary into log
        final String newLine = System.getProperty("line.separator");
        String summary = "Experiment series summary:" + newLine + this.stasts.getSummary() + newLine;
        ExperimentSeries.log.info(summary);
        ExperimentSeries.log.info("Node statistics: " + this.stasts.getNodeStatistics().toString() + newLine + "Nodes Selected statistics: "
                + this.stasts.getNodesSelectedStatistics().toString());
        ExperimentSeries.log.info("END");
    }
}
