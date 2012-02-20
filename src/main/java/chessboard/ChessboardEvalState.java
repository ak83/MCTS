package chessboard;

public enum ChessboardEvalState {
    /** Normal chess board state */
    NORMAl,
    /** Black king is checked by white */
    BLACK_KING_CHECKED,
    /** White achieved check mate */
    BLACK_KING_MATED,
    /** Black king is in pat position */
    PAT,
    /** White did not manage to achieve check mate in allowed number of turns */
    TOO_MANY_MOVES_MADE,
    /** Chess board state was repeated three times */
    DRAW,
    /** White piece can be eaten by black */
    WHITE_PIECE_IN_DANGER
}
