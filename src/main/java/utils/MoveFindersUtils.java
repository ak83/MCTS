package utils;

import java.util.ArrayList;

import exec.Move;



public class MoveFindersUtils {

    /**
     * Calculates distance between chess board center and target rank of ply.
     * 
     * @param plyNumber
     *            ply number
     * @return distance between chess board center and target rank of ply
     */
    public static int rankDistanceOfMoveFromCenter(int plyNumber) {
        int to = Utils.getTargetPositionFromMoveNumber(plyNumber);
        int toRank = Utils.getRankFromPosition(to);
    
        int rankDiff = -1;
    
        if (toRank > 5) {
            rankDiff = Math.abs(toRank - 5);
        }
        else if (toRank < 4) {
            rankDiff = Math.abs(4 - toRank);
        }
        else {
            rankDiff = 0;
        }
    
        return rankDiff;
    }

    /**
     * Calculates distance between chess board center and target position of
     * move.
     * 
     * @param moveNumber
     *            move number
     * @return distance between chess board center and target position of ply
     */
    public static int distanceOfMoveFromCenter(int moveNumber) {
        int fileDiff = rankDistanceOfMoveFromCenter(moveNumber);
        int rankDiff = MoveFindersUtils.fileDistanceOfMoveFromCenter(moveNumber);
    
        return fileDiff + rankDiff;
    }

    /**
     * Calculates distance between chess board center and target file of move.
     * 
     * @param moveNumber
     *            move number
     * @return distance between chess board center and target file of move
     */
    public static int fileDistanceOfMoveFromCenter(int moveNumber) {
        int to = Utils.getTargetPositionFromMoveNumber(moveNumber);
        int toFile = Utils.getFileFromPosition(to);
    
        int fileDiff = -1;
    
        if (toFile > 5) {
            fileDiff = Math.abs(toFile - 5);
        }
        else if (toFile < 4) {
            fileDiff = Math.abs(4 - toFile);
        }
        else {
            fileDiff = 0;
        }
    
        return fileDiff;
    }

    /**
     * Finds minimum distance of target positions of plies from chess board
     * center.
     * 
     * @param plies
     *            plies
     * @return minimum distance of ply target position from chess board center
     */
    public static int findMinimumDistanceFromCenterFromPlies(
            ArrayList<Move> plies) {
        int minDist = -1;
        for (int x = 0; x < plies.size(); x++) {
            int moveNumber = plies.get(x).moveNumber;
            int dist = distanceOfMoveFromCenter(moveNumber);
    
            if (dist < minDist || minDist == -1) {
                minDist = dist;
            }
        }
    
        return minDist;
    }
    
    private MoveFindersUtils() {}

}
