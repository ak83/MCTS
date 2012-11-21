package mct;

import moveChoosers.WhiteChooserStrategy;
import moveFinders.BlackFinderStrategy;
import config.MCTSSetup;

/**
 * Holds all values of MCTS parameters.
 * 
 * @author ak83
 */
public class MCTSParameters {

    /**
     * Determines exploration/exploitation ratio in the algorithm.
     */
    private double               c                         = MCTSSetup.C;

    /**
     * Threshold T determines if the selection or the simulation phase begins
     * from a node.
     */
    private int                  thresholdT                = MCTSSetup.THRESHOLD_T;

    /**
     * Number of MCTS iterations performed before every chess game.
     */
    private int                  numberOfInitalIterations  = MCTSSetup.NUMBER_OF_INITAL_STEPS;

    /**
     * Number of MCTS iterations performed before each white's player move.
     */
    private int                  nuberOfRunningSimulations = MCTSSetup.NUMBER_OF_RUNNING_STEPS;

    /**
     * How the white player will choose his moves.
     */
    private WhiteChooserStrategy whiteMovePlayingStrategy  = MCTSSetup.WHITE_MOVE_CHOOSER_STRATEGY;

    /**
     * How the black player will choose his moves.
     */
    private BlackFinderStrategy  blackMovePlayingStrategy  = MCTSSetup.BLACK_MOVE_CHOOSER_STRATEGY;


    public double getC() {
        return c;
    }


    public void setC(double c) {
        this.c = c;
    }


    public int getThresholdT() {
        return thresholdT;
    }


    public void setThresholdT(int thresholdT) {
        this.thresholdT = thresholdT;
    }


    public int getNumberOfInitalIterations() {
        return numberOfInitalIterations;
    }


    public void setNumberOfInitalIterations(int numberOfInitalIterations) {
        this.numberOfInitalIterations = numberOfInitalIterations;
    }


    public int getNuberOfRunningSimulations() {
        return nuberOfRunningSimulations;
    }


    public void setNuberOfRunningSimulations(int nuberOfRunningSimulations) {
        this.nuberOfRunningSimulations = nuberOfRunningSimulations;
    }


    public WhiteChooserStrategy getWhiteMovePlayingStrategy() {
        return whiteMovePlayingStrategy;
    }


    public void setWhiteMovePlayingStrategy(WhiteChooserStrategy whiteMovePlayingStrategy) {
        this.whiteMovePlayingStrategy = whiteMovePlayingStrategy;
    }


    public BlackFinderStrategy getBlackMovePlayingStrategy() {
        return blackMovePlayingStrategy;
    }


    public void setBlackMovePlayingStrategy(BlackFinderStrategy blackMovePlayingStrategy) {
        this.blackMovePlayingStrategy = blackMovePlayingStrategy;
    }

}
