package moveFinders;

/**
 * Describes black simulation strategies.
 * 
 * @author Andraz Kohne
 */
public enum BlackFinderStrategy {
    /** random behavior */
    RANDOM,
    /**
     * black tries to move towards center of the chess board no matter what
     */
    CENTER,
    /**
     * black tries to move towards center of the chess board and if it has a
     * chance eats whites piece
     */
    GOOD,
    /** black plays with perfect information */
    PERFECT
}
