package utils;

import exec.Constants;
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
    public static Class<? extends Number> MCTestParameterValueType(
	    MCTestParameter testedParameter) {
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
	    Constants.C = value;
	    break;
	case GOBAN:
	    Constants.GOBAN = value.intValue();

	}
    }

}
