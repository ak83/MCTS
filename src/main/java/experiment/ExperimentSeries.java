package experiment;

import java.io.File;

import utils.ExperimentUtils;
import utils.IOUtils;
import utils.Utils;
import exec.Constants;

/** Class that handles running multiple experiments */
public class ExperimentSeries {

    /** Statistic for this series of experiments */
    private ExperimentSeriesStatistics stasts = new ExperimentSeriesStatistics();


    /**
     * Run experiments defined in configuration file
     */
    public void runExperiments() {
        String rootDir = ExperimentUtils
                .testParameterToString(Constants.testParameter)
                + "--" + Utils.today();

        new File(rootDir).mkdir();

        // run experiment for each test parameter value
        for (int x = 0; x < Constants.testParameterValues.size(); x++) {

            Double parameterValue = Constants.testParameterValues.get(x);
            ExperimentUtils.setTestParameter(Constants.testParameter,
                    parameterValue);

            Experiment experiment = new Experiment(rootDir + "/experiment" + x);
            experiment.runExperiment();
            this.stasts
                    .addExperimentStatistics(experiment.getExperimentStats());
        }

        // save statistics outputs
        this.stasts.writeDTMDiffToCsv(rootDir);
        this.stasts.saveDTMDiffGraph(rootDir + "/"
                + IOUtils.WHITE_DTM_DIFFERENCE_FILE_NAME + ".jpg",
                Constants.testParameter);
        this.stasts.saveUltimateChart(rootDir + "/"
                + IOUtils.ULTIMATE_FILE_NAME + ".jpg", Constants.testParameter);

        this.stasts.writeUltimateCSV(rootDir + "/" + IOUtils.ULTIMATE_FILE_NAME
                + ".csv");
    }
}
