package experiment;

import utils.ExperimentUtils;
import exec.Constants;

/** Class that handles running multiple experiments */
public class ExperimentSeries {

    /** Statistic for this series of experiments */
    private ExperimentSeriesStatistics stasts = new ExperimentSeriesStatistics();


    /**
     * Run experiments defined in configuration file
     */
    public void runExperiments() {
        for (int x = 0; x < Constants.testParameterValues.size(); x++) {

            Double parameterValue = Constants.testParameterValues.get(x);
            ExperimentUtils.setTestParameter(Constants.testParameter,
                    parameterValue);

            Experiment experiment = new Experiment("experiment" + x);
            experiment.runExperiment();
            this.stasts
                    .addExperimentStatistics(experiment.getExperimentStats());
        }

        this.stasts.writeDTMDiffToCsv();
    }
}
