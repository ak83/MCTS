package utils;

import config.MCTSSetup;
import experiment.MCTestParameter;

public class ExperimentUtils {

    private ExperimentUtils() {}


    /**
     * Get type of values for MC parameter.
     * 
     * @param testedParameter
     *            parameter for which type of its values will be returned
     * @return Class that represents value type of <code>testedParameter</code>
     */
    public static Class<? extends Number> MCTestParameterValueType(MCTestParameter testedParameter) {
        switch (testedParameter) {
            case C:
                return Double.class;
            case GOBAN:
                return Integer.class;
            default:
                // this should never happen
                return null;
        }
    }


    /**
     * Transforms string parameter to belonging enum.
     * 
     * @param parameter
     *            name of MC parameter to be transformed
     * @return enum that <code>parameter</code> represents or <code>null</code>
     *         if such enum does not exist
     */
    public static MCTestParameter mcTestParameterStringToEnum(String parameter) {
        if (parameter.equalsIgnoreCase("c")) {
            return MCTestParameter.C;
        }
        else if (parameter.equalsIgnoreCase("GOBAN")) {
            return MCTestParameter.GOBAN;
        }
        else if (parameter.equalsIgnoreCase("SIMULATIONS") || parameter.equalsIgnoreCase("STEPS")) {
            return MCTestParameter.STEPS;
        }
        else {
            return null;
        }
    }


    /**
     * Set parameter value
     * 
     * @param parameter
     *            enum that represents parameter
     * @param value
     *            new value
     */
    public static void setTestParameter(MCTestParameter parameter, Double value) {
        switch (parameter) {
            case C:
                MCTSSetup.C = value;
                break;
            case GOBAN:
                MCTSSetup.THRESHOLD_T = value.intValue();
                break;
            case STEPS:
                MCTSSetup.NUMBER_OF_RUNNING_STEPS = value.intValue();
                break;

        }
    }


    /**
     * Gets {@link String} representation of {@link MCTestParameter}.
     * 
     * @param parameter
     *            {@link MCTestParameter} to be converted to {@link String}
     * @return {@link String} representation of <code>parameter</code>
     */
    public static String testParameterToString(MCTestParameter parameter) {
        switch (parameter) {
            case C:
                return "C";
            case GOBAN:
                return "GOBAN";
            case STEPS:
                return "MC_STEPS";

            default:
                throw new IllegalArgumentException();
        }
    }

}
