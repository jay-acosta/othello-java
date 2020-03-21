import java.util.ArrayList;

public class OthelloAI implements OthelloConstants {

    private char myPiece;
    private long thinkingTime;
    private boolean show;
    private AIDifficulty difficulty;

    public OthelloAI() {
        myPiece = WHITE_PIECE;
        thinkingTime = 10;
        difficulty = AIDifficulty.RANDOM;
        show = true;
    }

    public OthelloAI(char myPiece) {
        this();

        this.myPiece = myPiece;
    }

    public void setPiece(char newPiece) {
        myPiece = newPiece;
    }

    public char getPiece() {
        return myPiece;
    }

    public void showMessages(boolean show) {
        this.show = show;
    }

    public void setDifficulty(AIDifficulty newDifficulty) {
        difficulty = newDifficulty;
    }

    public void playPiece(OthelloBoard board) {

        if (show) {
            System.out.println("Hmm... let me think where to place this");
        }

        try {

            int[] moves = new int[2];
            moves[0] = -1;
            moves[1] = -1;

            switch(difficulty) {
                case RANDOM:
                    chooseRandomMove(board, moves);
                    break;
                case BEST_MOVE:
                    chooseBestMove(board, moves);
                    break;
            }

            if (show) {
                Thread.sleep(thinkingTime); // thinking
            }

            if (moves[0] == -1 || moves[1] == -1) {

                if (show)
                    System.out.println("I couldn't find a piece. I'll just skip my turn.");
            } else {
                if (show)
                    System.out.println("Found a spot!");

                board.placePiece(moves[0], moves[1], myPiece);
            }

        } catch (InterruptedException e) {
            // error occurs when "Ctrl+C"-ing out of a program or an unwanted interuption
            System.out.println("I ran out of thinking juice...");
            e.printStackTrace();
        }
    }

    private void chooseRandomMove(OthelloBoard board, int[] moves) {

        ArrayList<int[]> possibleMoves = new ArrayList<>();

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board.canBePlaced(r, c, myPiece)) {
                    possibleMoves.add(new int[] {r, c});
                }
            }
        }

        if (possibleMoves.size() > 0) {
            int[] randomMove = possibleMoves.get((int) (Math.random() * possibleMoves.size()));
            moves[0] = randomMove[0];
            moves[1] = randomMove[1];
        }
    }

    private void chooseBestMove(OthelloBoard board, int[] moves) {
        int max = 0;

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int currentValue = board.findMaxPieces(r, c, myPiece);

                if (currentValue > max) {
                    max = currentValue;
                    moves[0] = r;
                    moves[1] = c;
                }
            }
        }
    }

    public enum AIDifficulty {
        BEST_MOVE, RANDOM
    }
}
