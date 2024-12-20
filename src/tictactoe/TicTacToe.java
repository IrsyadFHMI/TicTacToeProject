/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #7
 * 1 - 5026221163 - Mohammad Geresidi Rachmadi
 * 2 - 5026221187 - Muhammad Irsyad Fahmi
 */
package tictactoe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class TicTacToe extends JPanel {
    private static final long serialVersionUID = 1L;

    // Define named constants for the drawing graphics
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.cyan;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    private static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Define game objects
    private Board board;         // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private JLabel statusBar;    // for displaying status message
    private JFrame frame;        // The main frame
    private AIPlayer aiPlayer;   // AI player to play against

    /** Constructor to setup the UI and game components */
    public TicTacToe() {
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();

                // Get the row and column clicked based on resized dimensions
                int row = mouseY / (getHeight() / Board.ROWS);
                int col = mouseX / (getWidth() / Board.COLS);

                if (currentState == State.PLAYING && currentPlayer == Seed.CROSS) { // Only human can play when it's their turn
                    SoundEffect.Playing.play();
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        // Update cells[][] and return the new game state after the move
                        currentState = board.stepGame(currentPlayer, row, col);
                        // Switch player to AI
                        currentPlayer = Seed.NOUGHT;

                        // Now, let the AI make its move
                        if (currentState == State.PLAYING) {
                            aiMove();
                        }
                    }
                } else if (currentState != State.PLAYING) {
                    // Game over: restart the game
                    SoundEffect.DIE.play();
                    newGame();
                }
                // Refresh the drawing canvas
                repaint();  // Callback paintComponent().
            }
        });

        // Add a component listener to handle resizing
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();  // Redraw the board when the window is resized
            }
        });

        setLayout(new BorderLayout());

        // Setup the status bar
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        add(statusBar, BorderLayout.PAGE_END);

        // Initialize the game components
        initGame();
        newGame();
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board();
        aiPlayer = new AIPlayerMinimax(board); // Initialize the AI player using Minimax strategy
    }

    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        board.newGame();
        currentPlayer = Seed.CROSS;    // Human (cross) plays first
        currentState = State.PLAYING; // Ready to play
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG); // Set the background color

        // Recalculate dynamic dimensions based on panel size
        Dimension size = getSize();
        int width = size.width;
        int height = size.height;

        // Menghitung sisa tinggi untuk papan permainan setelah dikurangi status bar
        int availableHeight = height - statusBar.getHeight(); // Subtract status bar height

        // Paint the board according to the new dimensions (adjusting height)
        board.paint(g, width, availableHeight);  // Pass the available height

        // Update status bar
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.RED);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "Your Turn" : "Computer Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("You Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'Computer' Won! Click to play again.");
        }
    }


    /** The entry "main" method */
    public void play() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            frame = new JFrame(TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(this);
            frame.setSize(400, 400); // Initial size
            frame.setLocationRelativeTo(null); // Center the window
            frame.setVisible(true);
        });
    }

    /** Make a move for the AI */
    private void aiMove() {
        int[] aiMove = aiPlayer.move();
        int row = aiMove[0];
        int col = aiMove[1];

        // Update cells[][] and return the new game state after the move
        currentState = board.stepGame(currentPlayer, row, col);

        // Switch player to human
        currentPlayer = Seed.CROSS;

        repaint();  // Refresh the screen after AI move
    }
}
