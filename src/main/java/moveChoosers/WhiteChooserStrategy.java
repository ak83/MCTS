package moveChoosers;

/**
 * Describes whites MCT ply choosing strategies (actual ply choosing from Monte
 * Carlo tree).
 * 
 * @author Andraz Kohne
 */
public enum WhiteChooserStrategy {

    /** Wihte chooses random node */
    RANDOM,
    /** White chooses node with highest visit count */
    VISIT_COUNT,
    /** White chooses node with highest MCT rating */
    RATING
}
