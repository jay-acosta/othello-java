public class OthelloBoardTester {
    public static void main(String[] args) {

        // set rules for Othello board
        OthelloBoard board = new OthelloBoard();
        board.showMoves(false);

        // set rules for OthelloAI bots
        OthelloAI ai = new OthelloAI(OthelloConstants.BLACK_PIECE);
        OthelloAI otherAI = new OthelloAI(OthelloConstants.WHITE_PIECE);
        ai.showMessages(false);
        otherAI.showMessages(false);

        int N = 1_000_000; // game repetitions
        int moduloMessage = 100_000; // messages sent out to console

        // check if args passed into command line
        // first argument will be N, second argument will be moduloMessage
        if(args.length == 2) {
            try {
                N = Integer.parseInt(args[0]);
                moduloMessage = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("Something went wrong with the formatting");
                System.out.println("Going back to default settings");
            }
        }

        int blackWins = 0;
        int whiteWins = 0;

        // repeat game N times
        for(int i = 1; i <= N; i++) {
            board.resetBoard();

            while (!board.isGameOver()) {

                // let AI play until the game is over
                if (ai.getPiece() == board.currentTurn()) {
                    ai.playPiece(board);
                } else {
                    otherAI.playPiece(board);
                }

                // switch turns
                board.switchTurns();
            }

            // get the total amount of black and white pieces currently on the board
            int blackCount = board.countPieces(ai.getPiece());
            int whiteCount = board.countPieces(otherAI.getPiece());

            // determine the winner based on the amount of pieces
            if (blackCount > whiteCount) {
                blackWins++;
            }
            if (whiteCount > blackCount) {
                whiteWins++;
            }

            // display message to tester
            if (i % moduloMessage == 0) {
                System.out.printf("%d games played so far...\n", i);
            }
        }

        // show test results
        System.out.printf("We ran %d tests.\n", N);
        System.out.println("Here are the results!");
        System.out.printf("White wins (%.2f%%): %d\n", 100 * ((double) whiteWins) / N, whiteWins);
        System.out.printf("Black wins (%.2f%%): %d\n", 100 * ((double) blackWins) / N, blackWins);
        System.out.printf("Ties wins: %d\n", N - (whiteWins + blackWins));
    }
}
