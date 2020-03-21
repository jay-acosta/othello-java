import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // set the rules for the board
        OthelloBoard board = new OthelloBoard();
        board.showMoves(yesOrNoPrompt(input, "Would you like to show possible moves? (Y/N) > "));

        OthelloAI ai = new OthelloAI();
        boolean playWithFriends = yesOrNoPrompt(input, "Do you have a friend to play with? (Y/N) > ");
        if (!playWithFriends) {
            boolean goingFirst = yesOrNoPrompt(input, "Would you like to go first? (Y/N) > ");

            if (!goingFirst) {
                board.switchTurns();
            }

            ai.setPiece(board.nextTurn());
        }

        do {
            System.out.println("Starting new game");
            board.resetBoard(); // reset the current board

            if (playWithFriends) {
                playGameWithFriends(board, input);
            } else {
                playGameWithComputer(board, ai, input);
            }

            showWinner(board);
        }
        while (yesOrNoPrompt(input, "Would you like to play again? (Y/N) > "));
    }

    private static void showWinner(OthelloBoard board) {

        // count the number of pieces
        int blackTotal = board.countPieces(OthelloConstants.BLACK_PIECE);
        int whiteTotal = board.countPieces(OthelloConstants.WHITE_PIECE);

        // display the final board
        board.printBoard();

        // display how many pieces each person had
        System.out.printf("Black, you had %d pieces.\n", blackTotal);
        System.out.printf("White, you had %d pieces.\n", whiteTotal);

        // show who wins
        if (blackTotal == whiteTotal) {
            System.out.println("It's a tie!");
        } else if (blackTotal > whiteTotal) {
            System.out.println("Black wins");
        } else {
            System.out.println("White wins");
        }
    }

    private static void playGameWithComputer(OthelloBoard board, OthelloAI ai, Scanner input) {
        while (!board.isGameOver()) {
            board.printBoard();

            if (ai.getPiece() == board.currentTurn()) {
                System.out.println("It's the AI's turn");
                ai.playPiece(board);
                board.switchTurns();

            } else {
                System.out.println("Player, it's your turn");

                int[] playerInput = handleInput(input);
                int row = playerInput[0];
                int col = playerInput[1];
                char currentTurn = board.currentTurn();

                if (board.canBePlaced(row, col, currentTurn)) {

                    board.placePiece(row, col, currentTurn);
                    board.switchTurns();

                } else {
                    System.out.println("Sorry, " + currentTurn + ". That can't be played there.");
                }
            }
        }
    }

    // plays Othello
    private static void playGameWithFriends(OthelloBoard board, Scanner input) {

        while (!board.isGameOver()) {
            if (board.currentTurn() == OthelloConstants.BLACK_PIECE) {
                System.out.println("Black, it's your turn");
            } else {
                System.out.println("White, it's your turn");
            }
            board.printBoard();
        }
    }

    private static int[] handleInput(Scanner input) {
        int x = -1;
        int y = -1;

        while (x == -1 && y == -1) {
            try {
                System.out.print("Input row > ");
                x = Integer.parseInt(input.next());
                System.out.print("Input col > ");
                y = Integer.parseInt(input.next());

                if (x < 0 || x >= OthelloConstants.SIZE || y < 0 || y >= OthelloConstants.SIZE) {
                    throw new IllegalStateException("invalid row-column selection");
                }
            } catch (Exception e) {
                System.out.println("Something was wrong with your input, please input a valid number");
                x = -1;
                y = -1;
            }
        }

        System.out.println();

        return new int[] {x, y};
    }

    private static boolean yesOrNoPrompt(Scanner input, String prompt) {
        System.out.print(prompt);
        String answer = input.next();

        while (answer == null || !answer.matches("[YNyn]")) {
            System.out.println("\nPlease answer 'Y' or 'N'");
            System.out.print(prompt);
            answer = input.next();
        }

        return answer.matches("[Yy]");
    }

    private static boolean playAgain(Scanner input) {
        System.out.print("Would you like to play again? (Y/N) > ");
        String answer = input.next();

        while (answer == null || !answer.matches("[YNyn]")) {
            System.out.println("\nPlease answer 'Y' or 'N'");
            System.out.print("Would you like to play again? (Y/N) > ");
            answer = input.next();
        }

        return answer.matches("[Yy]");
    }
}