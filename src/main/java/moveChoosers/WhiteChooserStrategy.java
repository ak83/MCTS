package moveChoosers;

/**
 * Describes whites MCT ply choosing strategies (actual ply choosing from Monte
 * Carlo tree).
 * 
 * @author Andraz Kohne
 */
public enum WhiteChooserStrategy {

    /** White chooses random node */
    RANDOM,
    /** White chooses node with highest visit count */
    MAX_VISIT_COUNT,
    /** White chooses node with highest MCT rating */
    MAX_UCT
}
