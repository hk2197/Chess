import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chess8 {
    static int wR = 1, wN = 2, wB = 3, wQ = 4, wK = 5, wP = 6;
    static int bR = 7, bN = 8, bB = 9, bQ = 10, bK = 11, bP = 12, nP = 0;
    static int[] boardtrack = new int[64];
    static JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    static int selectedPiece = -1;
    static int selectedIndex = -1;
    static boolean isWhiteTurn = true;
    public static void main(String[] args) {
        
        JFrame frame = new JFrame("Chess Board with Pieces");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        Color darkBrown = new Color(139, 69, 19);
        Color lightBrown = new Color(245, 222, 179);

        int[] initialBoardtrack = {
                bR, bN, bB, bQ, bK, bB, bN, bR,
                bP, bP, bP, bP, bP, bP, bP, bP,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                wP, wP, wP, wP, wP, wP, wP, wP,
                wR, wN, wB, wQ, wK, wB, wN, wR
        };
        System.arraycopy(initialBoardtrack, 0, boardtrack, 0, 64);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel square = new JPanel();
                square.setBackground((row + col) % 2 == 0 ? lightBrown : darkBrown);
                square.setLayout(new BorderLayout());

                int index = row * 8 + col;
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(index);
                    }
                });

                boardPanel.add(square);
            }
        }

        for (int i = 0; i < 64; i++) {
            addPiece(i, boardtrack[i]);
        }

        frame.add(boardPanel);
        frame.setVisible(true);
    }

    public static void handleSquareClick(int index) {
        int pieceCode = boardtrack[index];

        if (selectedPiece == -1) {
            if (pieceCode != nP && isValidTurn(pieceCode)) {
                selectedPiece = pieceCode;
                selectedIndex = index;
                System.out.println("Piece selected at index " + index + ": " + selectedPiece);

                List<Integer> possibleMoves = getPossibleMoves(selectedPiece, selectedIndex);
                System.out.println("Possible moves: " + possibleMoves);
            }
        } else {
            if (getPossibleMoves(selectedPiece, selectedIndex).contains(index)) {
                System.out.println("Moving piece " + selectedPiece + " to index " + index);
                performMove(selectedIndex, index, selectedPiece);
                isWhiteTurn = !isWhiteTurn;

                if (!isWhiteTurn) {
                    playRandomMoveForBlack();
                }
            } else {
                System.out.println("Invalid move for piece " + selectedPiece);
            }

            selectedPiece = -1;
            selectedIndex = -1;
        }
    }

    public static boolean isValidTurn(int pieceCode) {
        return (pieceCode >= 1 && pieceCode <= 6 && isWhiteTurn) ||
               (pieceCode >= 7 && pieceCode <= 12 && !isWhiteTurn);
    }

    public static void playRandomMoveForBlack() {
        List<Integer> blackPieces = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if (boardtrack[i] >= 7 && boardtrack[i] <= 12) {
                blackPieces.add(i);
            }
        }

        Random random = new Random();
        while (!blackPieces.isEmpty()) {
            int randomIndex = random.nextInt(blackPieces.size());
            int pieceIndex = blackPieces.get(randomIndex);
            int pieceCode = boardtrack[pieceIndex];
            List<Integer> possibleMoves = getPossibleMoves(pieceCode, pieceIndex);

            if (!possibleMoves.isEmpty()) {
                int moveIndex = possibleMoves.get(random.nextInt(possibleMoves.size()));
                System.out.println("Black plays: " + pieceCode + " from " + pieceIndex + " to " + moveIndex);
                performMove(pieceIndex, moveIndex, pieceCode);
                isWhiteTurn = true;
                return;
            }

            blackPieces.remove(randomIndex);
        }

        System.out.println("Black has no valid moves!");
    }
   
   
   
   
   

    public static List<Integer> getPossibleMoves(int pieceCode, int index) {
        List<Integer> moves = new ArrayList<>();
        int row = index / 8;
        int col = index % 8;

        switch (pieceCode) {
            case 1: // White Rook
            case 7: // Black Rook
                addLinearMoves(moves, row, col, 1, 0);  // Right
                addLinearMoves(moves, row, col, -1, 0); // Left
                addLinearMoves(moves, row, col, 0, 1);  // Down
                addLinearMoves(moves, row, col, 0, -1); // Up
                break;

            case 2: // White Knight
            case 8: // Black Knight
                addKnightMoves(moves, row, col);
                break;

            case 3: // White Bishop
            case 9: // Black Bishop
                addLinearMoves(moves, row, col, 1, 1);   // Bottom-right diagonal
                addLinearMoves(moves, row, col, -1, -1); // Top-left diagonal
                addLinearMoves(moves, row, col, 1, -1);  // Bottom-left diagonal
                addLinearMoves(moves, row, col, -1, 1);  // Top-right diagonal
                break;

            case 4: // White Queen
            case 10: // Black Queen
                addLinearMoves(moves, row, col, 1, 0);   // Right
                addLinearMoves(moves, row, col, -1, 0);  // Left
                addLinearMoves(moves, row, col, 0, 1);   // Down
                addLinearMoves(moves, row, col, 0, -1);  // Up
                addLinearMoves(moves, row, col, 1, 1);   // Bottom-right diagonal
                addLinearMoves(moves, row, col, -1, -1); // Top-left diagonal
                addLinearMoves(moves, row, col, 1, -1);  // Bottom-left diagonal
                addLinearMoves(moves, row, col, -1, 1);  // Top-right diagonal
                break;

            case 5: // White King
            case 11: // Black King
                addKingMoves(moves, row, col);
                break;

            case 6: // White Pawn
            case 12: // Black Pawn
                addPawnMoves(moves, row, col, pieceCode);
                break;
        }
        return moves;
    }

    private static void addLinearMoves(List<Integer> moves, int row, int col, int rowDelta, int colDelta) {
        int currentRow = row + rowDelta;
        int currentCol = col + colDelta;
        while (currentRow >= 0 && currentRow < 8 && currentCol >= 0 && currentCol < 8) {
            int index = currentRow * 8 + currentCol;

            if (boardtrack[index] != nP) { // Stop if square is occupied
                if (isOpponentPiece(boardtrack[index], boardtrack[row * 8 + col])) {
                    moves.add(index); // Add if opponent piece
                }
                break;
            }

            moves.add(index); // Add empty square
            currentRow += rowDelta;
            currentCol += colDelta;
        }
    }

    private static void addKnightMoves(List<Integer> moves, int row, int col) {
        int[][] deltas = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] delta : deltas) {
            int newRow = row + delta[0];
            int newCol = col + delta[1];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                int index = newRow * 8 + newCol;
                if (boardtrack[index] == nP || isOpponentPiece(boardtrack[index], boardtrack[row * 8 + col])) {
                    moves.add(index);
                }
            }
        }
    }

    private static void addKingMoves(List<Integer> moves, int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                    int index = newRow * 8 + newCol;
                    if (boardtrack[index] == nP || isOpponentPiece(boardtrack[index], boardtrack[row * 8 + col])) {
                        moves.add(index);
                    }
                }
            }
        }
    }

    private static void addPawnMoves(List<Integer> moves, int row, int col, int pieceCode) {
        int direction = (pieceCode == wP) ? -1 : 1; // Direction depends on pawn color
        int startRow = (pieceCode == wP) ? 6 : 1;

        // Single move forward
        if (row + direction >= 0 && row + direction < 8) {
            int forwardIndex = (row + direction) * 8 + col;
            if (boardtrack[forwardIndex] == nP) {
                moves.add(forwardIndex);

                // Double move forward (only from starting position)
                if (row == startRow) {
                    int doubleForwardIndex = (row + 2 * direction) * 8 + col;
                    if (boardtrack[doubleForwardIndex] == nP) {
                        moves.add(doubleForwardIndex);
                    }
                }
            }
        }

        // Capture diagonally
        int[][] captureOffsets = {{direction, -1}, {direction, 1}};
        for (int[] offset : captureOffsets) {
            int captureRow = row + offset[0];
            int captureCol = col + offset[1];
            if (captureRow >= 0 && captureRow < 8 && captureCol >= 0 && captureCol < 8) {
                int captureIndex = captureRow * 8 + captureCol;
                if (isOpponentPiece(boardtrack[captureIndex], pieceCode)) {
                    moves.add(captureIndex);
                }
            }
        }
    }

    private static boolean isOpponentPiece(int piece, int currentPiece) {
        return (currentPiece <= 6 && piece > 6) || (currentPiece > 6 && piece <= 6 && piece != nP);
    }



    public static void performMove(int fromIndex, int toIndex, int pieceCode) {
        removePiece(fromIndex);
        addPiece(toIndex, pieceCode);
    }

    public static void addPiece(int index, int pieceCode) {
        if (pieceCode == nP) {
            return;
        }

        String basePath = "C:\\Users\\hrida\\Downloads\\chess1.0\\";
        String piece = null;
        switch (pieceCode) {
            case 1: piece = "wR"; break;
            case 2: piece = "wN"; break;
            case 3: piece = "wB"; break;
            case 4: piece = "wQ"; break;
            case 5: piece = "wK"; break;
            case 6: piece = "wP"; break;
            case 7: piece = "bR"; break;
            case 8: piece = "bN"; break;
            case 9: piece = "bB"; break;
            case 10: piece = "bQ"; break;
            case 11: piece = "bK"; break;
            case 12: piece = "bP"; break;
        }

        String imagePath = basePath + piece + ".png";
        ImageIcon originalIcon = new ImageIcon(imagePath);

        JLabel pieceLabel = scaleImageIconToLabel(originalIcon, 80, 80);

        JPanel square = (JPanel) boardPanel.getComponent(index);
        square.removeAll();
        boardtrack[index] = pieceCode;
        square.add(pieceLabel);
        square.revalidate();
        square.repaint();
    }

    public static void removePiece(int index) {
        JPanel square = (JPanel) boardPanel.getComponent(index);
        boardtrack[index] = nP;
        square.removeAll();
        square.revalidate();
    
        square.repaint();
    }

    private static JLabel scaleImageIconToLabel(ImageIcon icon, int width, int height) {
        Image originalImage = icon.getImage();
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaledImage));
    }
}
