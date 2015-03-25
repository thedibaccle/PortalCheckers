
/*
   This applet lets two uses play checkers against each other.
   Red always starts the game.  If a player can jump an opponent's
   piece, then the player must jump.  When a plyer can make no more
   moves, the game ends.
   
   This file defines four classes: the main applet class, Checkers;
   CheckersCanvas, CheckersMove, and CheckersData.
   (This is not very good style; the other classes really should be
   nested classes inside the Checkers class.)
*/

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.Vector;

//import original.CheckersData;


public class Checkers extends Applet {

   /* The main applet class only lays out the applet.  The work of
      the game is all done in the CheckersCanvas object.   Note that
      the Buttons and Label used in the applet are defined as 
      instance variables in the CheckersCanvas class.  The applet
      class gives them their visual appearance and sets their
      size and positions.*/

   public void init() {
   
      setLayout(null);  // I will do the layout myself.
   
	  setBackground(new Color(0,150,0));  // Dark green background.
     
      /* Create the components and add them to the applet. */

      CheckersCanvas board = new CheckersCanvas();
          // Note: The constructor creates the buttons board.resignButton
          // and board.newGameButton and the Label board.message.
      add(board);

      board.newGameButton.setBackground(Color.lightGray);
      add(board.newGameButton);

      board.resignButton.setBackground(Color.lightGray);
      add(board.resignButton);

      board.message.setForeground(Color.green);
      board.message.setFont(new Font("Serif", Font.BOLD, 14));
      add(board.message);
      
      /* Set the position and size of each component by calling
         its setBounds() method. */

      board.setBounds(20,20,164,164); // Note:  size MUST be 164-by-164 !
      board.newGameButton.setBounds(210, 60, 100, 30);
      board.resignButton.setBounds(210, 120, 100, 30);
      board.message.setBounds(0, 200, 330, 30);
      setSize(335, 240);
   }
   
} // end class Checkers




class CheckersCanvas extends Canvas implements ActionListener, MouseListener, KeyListener {

     // This canvas displays a 160-by-160 checkerboard pattern with
     // a 2-pixel black border.  It is assumed that the size of the
     // canvas is set to exactly 164-by-164 pixels.  This class does
     // the work of letting the users play checkers, and it displays
     // the checkerboard.

   Button resignButton;   // Current player can resign by clicking this button.
   Button newGameButton;  // This button starts a new game.  It is enabled only
                          //     when the current game has ended.
   
   Label message;   // A label for displaying messages to the user.
   
   CheckersData board;  // The data for the checkers board is kept here.
                        //    This board is also responsible for generating
                        //    lists of legal moves.

   boolean gameInProgress; // Is a game currently in progress?
   
   /* The next three variables are valid only when the game is in progress. */
   
   int currentPlayer;      // Whose turn is it now?  The possible values
                           //    are CheckersData.WHITE and CheckersData.BLACK.
   int selectedRow, selectedCol;  // If the current player has selected a piece to
                                  //     move, these give the row and column
                                  //     containing that piece.  If no piece is
                                  //     yet selected, then selectedRow is -1.
   CheckersMove[] legalMoves;  // An array containing the legal moves for the
                               //   current player.
   

   public CheckersCanvas() {
          // Constructor.  Create the buttons and lable.  Listen for mouse
          // clicks and for clicks on the buttons.  Create the board and
          // start the first game.
      setBackground(Color.black);
      addMouseListener(this);
      //addKeyListener(this);
      setFont(new  Font("Serif", Font.BOLD, 14));
      resignButton = new Button("Resign");
      resignButton.addActionListener(this);
      newGameButton = new Button("New Game");
      newGameButton.addActionListener(this);
      message = new Label("",Label.CENTER);
      board = new CheckersData();
      doNewGame();
   }
   

   public void actionPerformed(ActionEvent evt) {
         // Respond to user's click on one of the two buttons.
      Object src = evt.getSource();
      if (src == newGameButton)
         doNewGame();
      else if (src == resignButton)
         doResign();
   }
   

   void doNewGame() {
         // Begin a new game.
      if (gameInProgress == true) {
             // This should not be possible, but it doens't 
             // hurt to check.
         message.setText("Finish the current game first!");
         return;
      }
      board.setUpGame();   // Set up the pieces.
      currentPlayer = CheckersData.WHITE;   // WHITE moves first.
      legalMoves = board.getLegalMoves(CheckersData.WHITE);  // Get WHITE's legal moves.
      selectedRow = -1;   // WHITE has not yet selected a piece to move.
      message.setText("Red:  Make your move.");
      gameInProgress = true;
      newGameButton.setEnabled(false);
      resignButton.setEnabled(true);
      repaint();
   }
   

   void doResign() {
          // Current player resigns.  Game ends.  Opponent wins.
       if (gameInProgress == false) {
          message.setText("There is no game in progress!");
          return;
       }
       if (currentPlayer == CheckersData.WHITE)
          gameOver("WHITE resigns.  BLACK wins.");
       else
          gameOver("BLACK resigns.  WHITE wins.");
   }
   

   void gameOver(String str) {
          // The game ends.  The parameter, str, is displayed as a message
          // to the user.  The states of the buttons are adjusted so playes
          // can start a new game.
      message.setText(str);
      newGameButton.setEnabled(true);
      resignButton.setEnabled(false);
      gameInProgress = false;
   }
      

   void doClickSquare(int row, int col) {
         // This is called by mousePressed() when a player clicks on the
         // square in the specified row and col.  It has already been checked
         // that a game is, in fact, in progress.
         
      /* If the player clicked on one of the pieces that the player
         can move, mark this row and col as selected and return.  (This
         might change a previous selection.)  Reset the message, in
         case it was previously displaying an error message. */

      for (int i = 0; i < legalMoves.length; i++)
         if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
            selectedRow = row;
            selectedCol = col;
            if (currentPlayer == CheckersData.WHITE)
               message.setText("WHITE:  Make your move.");
            else
               message.setText("BLACK:  Make your move.");
            repaint();
            return;
         }

      /* If no piece has been selected to be moved, the user must first
         select a piece.  Show an error message and return. */

      if (selectedRow < 0) {
          message.setText("Click the piece you want to move.");
          return;
      }
      
      /* If the user clicked on a squre where the selected piece can be
         legally moved, then make the move and return. */

      for (int i = 0; i < legalMoves.length; i++)
         if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
                 && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
            doMakeMove(legalMoves[i]);
            return;
         }
         
      /* If we get to this point, there is a piece selected, and the square where
         the user just clicked is not one where that piece can be legally moved.
         Show an error message. */

      message.setText("Click the square you want to move to.");

   }  // end doClickSquare()
   

   void doMakeMove(CheckersMove move) {
          // Thiis is called when the current player has chosen the specified
          // move.  Make the move, and then either end or continue the game
          // appropriately.
          
      board.makeMove(move);
      
      /* If the move was a jump, it's possible that the player has another
         jump.  Check for legal jumps starting from the square that the player
         just moved to.  If there are any, the player must jump.  The same
         player continues moving.
      */
      
      if (move.isJump()) {
    	  /*
         legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow,move.toCol);
         if (legalMoves != null) {
            if (currentPlayer == CheckersData.WHITE)
               message.setText("WHITE:  You must continue jumping.");
            else
               message.setText("BLACK:  You must continue jumping.");
            selectedRow = move.toRow;  // Since only one piece can be moved, select it.
            selectedCol = move.toCol;
            repaint();
            return;
         }
         */
      }
      
      /* The current player's turn is ended, so change to the other player.
         Get that player's legal moves.  If the player has no legal moves,
         then the game ends. */
      
      if (currentPlayer == CheckersData.WHITE) {
         currentPlayer = CheckersData.BLACK;
         legalMoves = board.getLegalMoves(currentPlayer);
         if (legalMoves == null)
            gameOver("BLACK has no moves.  WHITE wins.");
         else if (legalMoves[0].isJump())
            message.setText("BLACK:  Make your move.  You must jump.");
         else
            message.setText("BLACK:  Make your move.");
      }
      else {
         currentPlayer = CheckersData.WHITE;
         legalMoves = board.getLegalMoves(currentPlayer);
         if (legalMoves == null)
            gameOver("WHITE has no moves.  BLACK wins.");
         else if (legalMoves[0].isJump())
            message.setText("WHITE:  Make your move.  You must jump.");
         else
            message.setText("WHITE:  Make your move.");
      }
      
      /* Set selectedRow = -1 to record that the player has not yet selected
          a piece to move. */
      
      selectedRow = -1;
      
      /* As a courtesy to the user, if all legal moves use the same piece, then
         select that piece automatically so the use won't have to click on it
         to select it. */
      
      if (legalMoves != null) {
         boolean sameStartSquare = true;
         for (int i = 1; i < legalMoves.length; i++)
            if (legalMoves[i].fromRow != legalMoves[0].fromRow
                                 || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                sameStartSquare = false;
                break;
            }
         if (sameStartSquare) {
            selectedRow = legalMoves[0].fromRow;
            selectedCol = legalMoves[0].fromCol;
         }
      }
      
      /* Make sure the board is redrawn in its new state. */
      
      repaint();
      
   }  // end doMakeMove();
   

   public void update(Graphics g) {
        // The paint method completely redraws the canvas, so don't erase
        // before calling paint().
      paint(g);
   }
   

   public void paint(Graphics g) {
        // Draw  checkerboard pattern in gray and lightGray.  Draw the
        // checkers.  If a game is in progress, hilite the legal moves.
      
      /* Draw a two-pixel black border around the edges of the canvas. */
      
      g.setColor(Color.black);
      g.drawRect(0,0,getSize().width-1,getSize().height-1);
      g.drawRect(1,1,getSize().width-3,getSize().height-3);
      
      /* Draw the squares of the checkerboard and the checkers. */
      
      for (int row = 0; row < 8; row++)
          for (int col = 0; col < 8; col++) {
              if ( row % 2 == col % 2)
                 g.setColor(Color.lightGray);
              else
                 g.setColor(Color.gray);
              g.fillRect(2 + col*20, 2 + row*20, 20, 20);
              
              if(row == 7)
              {
                  g.setColor(Color.blue);
                  g.fillRect(2 + 20, 2 + 100, 20, 20);
                  g.fillRect(2 + 120, 2 + 40, 20, 20);
                  g.setColor(Color.cyan);
                  g.fillRect(2 + 120, 2 + 100, 20, 20);
                  g.fillRect(2 + 20, 2 + 40, 20, 20);
                  g.setColor(Color.red);
                  g.fillRect(2 + 60, 2 + 40, 20, 20);
                  g.fillRect(2 + 80, 2 + 100, 20, 20);
                  g.setColor(Color.orange);
                  g.fillRect(2 + 80, 2 + 40, 20, 20);
                  g.fillRect(2 + 60, 2 + 100, 20, 20);
                  g.setColor(Color.green);
                  g.fillRect(2 + 40, 2 + 80, 20, 20);
                  g.fillRect(2 + 100, 2 + 60, 20, 20);
                  g.setColor(Color.yellow);
                  g.fillRect(2 + 40, 2 + 60, 20, 20);
                  g.fillRect(2 + 100, 2 + 80, 20, 20); 
              }
              
              

              }
       for (int row = 0; row < 8; row++)
       for (int col = 0; col < 8; col++)
       {
       switch (board.pieceAt(row,col))
       {
       case CheckersData.WHITE:
          g.setColor(Color.white);
          g.fillOval(4 + col*20, 4 + row*20, 16, 16);
          break;
       case CheckersData.BLACK:
          g.setColor(Color.black);
          g.fillOval(4 + col*20, 4 + row*20, 16, 16);
          break;
       case CheckersData.WHITE_SPLIT:
          g.setColor(Color.white);
          g.fillOval(4 + col*20, 4 + row*20, 16, 16);
          g.setColor(Color.black);
          g.drawString("S", 7 + col*20, 16 + row*20);
          break;
       case CheckersData.BLACK_SPLIT:
          g.setColor(Color.black);
          g.fillOval(4 + col*20, 4 + row*20, 16, 16);
          g.setColor(Color.white);
          g.drawString("S", 7 + col*20, 16 + row*20);
          break;
       }
       }
    
      /* If a game is in progress, hilite the legal moves.   Note that legalMoves
         is never null while a game is in progress. */      
      
      if (gameInProgress) {
            // First, draw a cyan border around the pieces that can be moved.
         g.setColor(Color.cyan);
         for (int i = 0; i < legalMoves.length; i++) {
            g.drawRect(2 + legalMoves[i].fromCol*20, 2 + legalMoves[i].fromRow*20, 19, 19);
         }
            // If a piece is selected for moving (i.e. if selectedRow >= 0), then
            // draw a 2-pixel white border around that piece and draw green borders 
            // around eacj square that that piece can be moved to.
         if (selectedRow >= 0) {
            g.setColor(Color.white);
            g.drawRect(2 + selectedCol*20, 2 + selectedRow*20, 19, 19);
            g.drawRect(3 + selectedCol*20, 3 + selectedRow*20, 17, 17);
            g.setColor(Color.green);
            for (int i = 0; i < legalMoves.length; i++) {
               if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow)
                  g.drawRect(2 + legalMoves[i].toCol*20, 2 + legalMoves[i].toRow*20, 19, 19);
            }
         }
      }
   }  // end paint()
   
   
   public Dimension getPreferredSize() {
         // Specify desired size for this component.  Note:
         // the size MUST be 164 by 164.
      return new Dimension(164, 164);
   }


   public Dimension getMinimumSize() {
      return new Dimension(164, 164);
   }
   

   public void mousePressed(MouseEvent evt) {
         // Respond to a user click on the board.  If no game is
         // in progress, show an error message.  Otherwise, find
         // the row and column that the user clicked and call
         // doClickSquare() to handle it.
      if (gameInProgress == false)
         message.setText("Click \"New Game\" to start a new game.");
      else {
         int col = (evt.getX() - 2) / 20;
         int row = (evt.getY() - 2) / 20;
         if (col >= 0 && col < 8 && row >= 0 && row < 8)
            doClickSquare(row,col);
      }
   }
   
   
   public void mouseReleased(MouseEvent evt) { }
   public void mouseClicked(MouseEvent evt) { }
   public void mouseEntered(MouseEvent evt) { }
   public void mouseExited(MouseEvent evt) { }


@Override
public void keyPressed(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}


@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}


@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}



}  // end class SimpleCheckerboardCanvas




class CheckersMove {
     // A CheckersMove object represents a move in the game of Checkers.
     // It holds the row and column of the piece that is to be moved
     // and the row and column of the square to which it is to be moved.
     // (This class makes no guarantee that the move is legal.)
   int fromRow, fromCol;  // Position of piece to be moved.
   int toRow, toCol;      // Square it is to move to.
   CheckersMove(int r1, int c1, int r2, int c2) {
        // Constructor.  Just set the values of the instance variables.
      fromRow = r1;
      fromCol = c1;
      toRow = r2;
      toCol = c2;
   }
   boolean isJump() {
        // Test whether this move is a jump.  It is assumed that
        // the move is legal.  In a jump, the piece moves two
        // rows.  (In a regular move, it only moves one row.)
      return (fromRow - toRow == 2 || fromRow - toRow == -2);
   }
}  // end class CheckersMove.




class CheckersData {

      // An object of this class holds data about a game of checkers.
      // It knows what kind of piece is on each sqaure of the checkerboard.
      // Note that WHITE moves "up" the board (i.e. row number decreases)
      // while BLACK moves "down" the board (i.e. row number increases).
      // Methods are provided to return lists of available legal moves.
      
   /*  The following constants represent the possible contents of a square
       on the board.  The constants WHITE and BLACK also represent players
       in the game.
   */

   public static final int
             EMPTY = 0,
             WHITE = 1,
             WHITE_SPLIT = 2,
             BLACK = 3,
             BLACK_SPLIT = 4;
             /*
             //WARP = 5,
             RED_WARP = 6,
             ORANGE_WARP = 7,
             YELLOW_WARP = 8,
             GREEN_WARP = 9,
             CYAN_WARP = 10,
             BLUE_WARP = 11;
             */
   public static final int[]
		   WARP = {5, 6, 7, 8, 9, 10};

   private int[][] board;  // board[r][c] is the contents of row r, column c.  
   private int[][] isWarp;

   public CheckersData() {
         // Constructor.  Create the board and set it up for a new game.
      board = new int[8][8];
      isWarp = new int[8][8];
      setUpGame();
   }
   
   
   
   
   public void setUpGame() {
          // Set up the board with checkers in position for the beginning
          // of a game.  Note that checkers can only be found in squares
          // that satisfy  row % 2 == col % 2.  At the start of the game,
          // all such squares in the first three rows contain black squares
          // and all such squares in the last three rows contain WHITE squares.
	  
      for (int row = 0; row < 8; row++) {
         for (int col = 0; col < 8; col++) {
        	 
        	   isWarp[row][col] = 0;
        	   
               if (row == 0)
                  board[row][col] = BLACK;
               else if (row == 7)
                  board[row][col] = WHITE;
               else
                  board[row][col] = EMPTY;
           
         }
      }
      //Cyan
      board[2][1] = WARP[4];
      board[5][6] = WARP[4];
      isWarp[2][1] = 9;
      isWarp[5][6] = 9;
      //Red
      board[2][3] = WARP[0];
      board[5][4] = WARP[0];
      isWarp[2][3] = 5;
      isWarp[5][4] = 5;
      //Orange
      board[2][4] = WARP[1];
      board[5][3] = WARP[1];
      isWarp[2][4] = 6;
      isWarp[5][3] = 6;
      //Blue
      board[2][6] = WARP[5];
      board[5][1] = WARP[5];
      isWarp[2][6] = 10;
      isWarp[5][1] = 10;
      //Yellow
      board[3][2] = WARP[2];
      board[4][5] = WARP[2];
      isWarp[3][2] = 7;
      isWarp[4][5] = 7;
      //Green
      board[3][5] = WARP[3];
      board[4][2] = WARP[3];
      isWarp[3][5] = 8;
      isWarp[4][2] = 8;
      
      
   }  // end setUpGame()
   

   public int pieceAt(int row, int col) {
          // Return the contents of the square in the specified row and column.
       return board[row][col];
   }
   

   public void setPieceAt(int row, int col, int piece) {
          // Set the contents of the square in the specified row and column.
          // piece must be one of the constants EMPTY, WHITE, BLACK, WHITE_SPLIT,
          // BLACK_SPLIT.
       board[row][col] = piece;
   }
   

   public void makeMove(CheckersMove move) {
         // Make the specified move.  It is assumed that move
         // is non-null and that the move it represents is legal.
      makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
   }
   

   public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
         // Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
         // assumed that this move is legal.  If the move is a jump, the
         // jumped piece is removed from the board.  If a piece moves
         // the last row on the opponent's side of the board, the 
         // piece becomes a king.
	   
	   System.out.println(board[toRow][toCol]);
	   /*
	   for(int i=0; i<6; i++)
	   {
	   		if(board[toRow][toCol] == WARP[i])
	   		{
	   			board[toRow][toCol] = board[fromRow][fromCol];
	   			board[fromRow][fromCol] = WARP[i];
	   			//System.out.println("Woo!");
	   			break;
	   		}
	   		else if(i==5)
	   		{
	   			board[toRow][toCol] = board[fromRow][fromCol];
	   			board[fromRow][fromCol] = EMPTY;
	   		}
	   }
	   */
	   board[toRow][toCol] = board[fromRow][fromCol];
	   if(isWarp[fromRow][fromCol] != 0)
		   board[fromRow][fromCol] = isWarp[fromRow][fromCol];
	   else
		   board[fromRow][fromCol] = EMPTY;
      
      if (fromRow - toRow >= 2 || fromRow - toRow <= -2 || fromCol - toCol >= 2 || fromCol - toCol <= -2) {
            // The move is a jump.  Remove the jumped piece from the board.
         int jumpRow = (fromRow + toRow) / 2;  // Row of the jumped piece.
         int jumpCol = (fromCol + toCol) / 2;  // Column of the jumped piece.
         
         if(isWarp[jumpRow][jumpCol] != 0)
        	 board[jumpRow][jumpCol] = isWarp[jumpRow][jumpCol];
         else
        	 board[jumpRow][jumpCol] = EMPTY;
      }
      
      if (toRow == 0 && board[toRow][toCol] == WHITE)
      {
         board[toRow][toCol] = WHITE_SPLIT;
         int i = 1;
         while(i < 7 && board[7][i] != EMPTY)
        	 i++;
        	 board[7][i] = WHITE_SPLIT;
        	 
      }
      if (toRow == 7 && board[toRow][toCol] == BLACK)
      {
         board[toRow][toCol] = BLACK_SPLIT;
         int i = 1;
         while(i < 7 && board[0][i] != EMPTY)
        	 i++;
        	 board[0][i] = BLACK_SPLIT;
      }
   }
   
   public int
    warpRow,
    warpCol,
    warpJumpRow,
    warpJumpCol;

   @SuppressWarnings("unchecked")
public CheckersMove[] getLegalMoves(int player) {
          // Return an array containing all the legal CheckersMoves
          // for the specfied player on the current board.  If the player
          // has no legal moves, null is returned.  The value of player
          // should be one of the constants RED or BLACK; if not, null
          // is returned.  If the returned value is non-null, it consists
          // entirely of jump moves or entirely of regular moves, since
          // if the player can jump, only jumps are legal moves.

      if (player != WHITE && player != BLACK)
         return null;

      int playerKing, enemy;  // The constant representing a King belonging to player.
      if (player == WHITE)
      {
         playerKing = WHITE_SPLIT;
         enemy = BLACK;
      }
      else
      {
         playerKing = BLACK_SPLIT;
         enemy = WHITE;
      }
      
      

      Vector moves = new Vector();  // Moves will be stored in this vector.
      
      /*  First, check for any possible jumps.  Look at each square on the board.
          If that square contains one of the player's pieces, look at a possible
          jump in each of the four directions from that square.  If there is 
          a legal jump in that direction, put it in the moves vector.
      */

      for (int row = 0; row < 8; row++) {
         for (int col = 0; col < 8; col++) {
            if (board[row][col] == player || board[row][col] == playerKing) {
            	//Diagonal
               if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                  moves.addElement(new CheckersMove(row, col, row+2, col+2));
               if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                  moves.addElement(new CheckersMove(row, col, row-2, col+2));
               if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                  moves.addElement(new CheckersMove(row, col, row+2, col-2));
               if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                  moves.addElement(new CheckersMove(row, col, row-2, col-2));
               //Cardinal
               if(canJump(player, row, col, row-1, col, row-2, col))
              	 moves.addElement(new CheckersMove(row, col, row-2, col));
               if(canJump(player, row, col, row+1, col, row+2, col))
              	 moves.addElement(new CheckersMove(row, col, row+2, col));
               if(canJump(player, row, col, row, col-1, row, col-2))
              	 moves.addElement(new CheckersMove(row, col, row, col-2));
               if(canJump(player, row, col, row, col+1, row, col+2))
              	 moves.addElement(new CheckersMove(row, col, row, col+2));
            }
         }
      }
      
      /*  If any jump moves were found, then the user must jump, so we don't 
          add any regular moves.  However, if no jumps were found, check for
          any legal regualar moves.  Look at each square on the board.
          If that square contains one of the player's pieces, look at a possible
          move in each of the four directions from that square.  If there is 
          a legal move in that direction, put it in the moves vector.
      */
      
         for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
               if (board[row][col] == player || board[row][col] == playerKing) {
            	   //System.out.println("working");
            	  //Diagonal
            	   
                  if (canMove(player,row,col,row+1,col+1))
                     moves.addElement(new CheckersMove(row,col,row+1,col+1));
                  if (canMove(player,row,col,row-1,col+1))
                     moves.addElement(new CheckersMove(row,col,row-1,col+1));
                  if (canMove(player,row,col,row+1,col-1))
                     moves.addElement(new CheckersMove(row,col,row+1,col-1));
                  if (canMove(player,row,col,row-1,col-1))
                     moves.addElement(new CheckersMove(row,col,row-1,col-1));
                     
                  //Cardinal
                  if (canMove(player,row,col,row+1,col))
                     moves.addElement(new CheckersMove(row,col,row+1,col));
                  if (canMove(player,row,col,row,col+1))
                     moves.addElement(new CheckersMove(row,col,row,col+1));
                  if (canMove(player,row,col,row,col-1))
                     moves.addElement(new CheckersMove(row,col,row,col-1));
                  if (canMove(player,row,col,row-1,col))
                     moves.addElement(new CheckersMove(row,col,row-1,col));
               }
      
      
          for (int row = 0; row < 8; row++)
             for (int col = 0; col < 8; col++)
                if (board[row][col] == player || board[row][col] == playerKing)
                {
              
                	//System.out.println(col +"\nBefore:" + warpRow + " " + warpCol);
                if(canWarp(player, row, col, row+1, col+1))
                	moves.addElement(new CheckersMove(row,col,warpRow,warpCol));
                if(canWarp(player, row, col, row+1, col-1))
                	moves.addElement(new CheckersMove(row,col,warpRow,warpCol));
                if(canWarp(player, row, col, row-1, col+1))
                	moves.addElement(new CheckersMove(row,col,warpRow,warpCol));
                if(canWarp(player, row, col, row-1, col-1))
                	moves.addElement(new CheckersMove(row,col,warpRow,warpCol));
                
                if(canWarp(player, row, col, row+1, col))
                	moves.addElement(new CheckersMove(row,col,warpRow,warpCol));
                if(canWarp(player, row, col, row, col+1))
                	moves.addElement(new CheckersMove(row,col,warpRow,warpCol));
                if(canWarp(player, row, col, row-1, col))
                	moves.addElement(new CheckersMove(row,col,warpRow,warpCol));
                if(canWarp(player, row, col, row, col-1))
                	moves.addElement(new CheckersMove(row,col,warpRow,warpCol));
                //System.out.println("After:" + warpRow + " " + warpCol);
                }
          
          
          for (int row = 0; row < 8; row++)
              for (int col = 0; col < 8; col++)
                 if (board[row][col] == player || board[row][col] == playerKing)
                 {  
                	 getAlternateWarpCoordinates(enemy ,row+1, col+1, 1, 1);
                     if(canWarpJump(player, row, col, row+1, col+1, warpJumpRow, warpJumpCol))
                     	moves.addElement(new CheckersMove(row,col,warpJumpRow,warpJumpCol));
                     
                     getAlternateWarpCoordinates(enemy, row+1, col-1, 1, -1);
                     if(canWarpJump(player, row, col, row+1, col-1, warpJumpRow, warpJumpCol))
                     	moves.addElement(new CheckersMove(row,col,warpJumpRow,warpJumpCol));
                     
                     getAlternateWarpCoordinates(enemy, row-1, col+1, -1, 1);
                     if(canWarpJump(player, row, col, row-1, col+1, warpJumpRow, warpJumpCol))
                     	moves.addElement(new CheckersMove(row,col,warpJumpRow,warpJumpCol));
                     
                     getAlternateWarpCoordinates(enemy, row-1, col-1, -1, -1);
                     if(canWarpJump(player, row, col, row-1, col-1, warpJumpRow, warpJumpCol))
                     	moves.addElement(new CheckersMove(row,col,warpJumpRow,warpJumpCol));
                     
                     
                     
                     getAlternateWarpCoordinates(enemy, row+1, col, 1, 0);
                     if(canWarpJump(player, row, col, row+1, col, warpJumpRow, warpJumpCol))
                     	moves.addElement(new CheckersMove(row,col,warpJumpRow,warpJumpCol));
                     
                     getAlternateWarpCoordinates(enemy, row, col+1, 0, 1);
                     if(canWarpJump(player, row, col, row, col+1, warpJumpRow, warpJumpCol))
                     	moves.addElement(new CheckersMove(row,col,warpJumpRow,warpJumpCol));
                     
                     getAlternateWarpCoordinates(enemy, row-1, col, -1, 0);
                     if(canWarpJump(player, row, col, row-1, col, warpJumpRow, warpJumpCol))
                     	moves.addElement(new CheckersMove(row,col,warpJumpRow,warpJumpCol));
                     
                     getAlternateWarpCoordinates(enemy, row, col-1, 0 , -1);
                     if(canWarpJump(player, row, col, row, col-1, warpJumpRow, warpJumpCol))
                     	moves.addElement(new CheckersMove(row,col,warpJumpRow,warpJumpCol));
                 }
      
          for (int row = 0; row < 8; row++)
              for (int col = 0; col < 8; col++)
                 if (board[row][col] == player || board[row][col] == playerKing)
                 {
                	 if(justWarp(player, row, col))
                     	moves.addElement(new CheckersMove(row,col,warpRow,warpCol));
                 }
          
      /* If no legal moves have been found, return null.  Otherwise, create
         an array just big enough to hold all the legal moves, copy the
         legal moves from the vector into the array, and return the array. */
      
      if (moves.size() == 0)
         return null;
      else {
         CheckersMove[] moveArray = new CheckersMove[moves.size()];
         for (int i = 0; i < moves.size(); i++)
            moveArray[i] = (CheckersMove)moves.elementAt(i);
         return moveArray;
      }

   }  // end getLegalMoves
   

   @SuppressWarnings("unchecked")
public CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
         // Return a list of the legal jumps that the specified player can
         // make starting from the specified row and column.  If no such
         // jumps are possible, null is returned.  The logic is similar
         // to the logic of the getLegalMoves() method.
      if (player != WHITE && player != BLACK)
         return null;
      int playerKing;  // The constant representing a King belonging to player.
      if (player == WHITE)
         playerKing = WHITE_SPLIT;
      else
         playerKing = BLACK_SPLIT;
      Vector moves = new Vector();  // The legal jumps will be stored in this vector.
      if (board[row][col] == player || board[row][col] == playerKing) {
    	  //Diagonal
         if (canJump(player, row, col, row+1, col+1, row+2, col+2))
            moves.addElement(new CheckersMove(row, col, row+2, col+2));
         if (canJump(player, row, col, row-1, col+1, row-2, col+2))
            moves.addElement(new CheckersMove(row, col, row-2, col+2));
         if (canJump(player, row, col, row+1, col-1, row+2, col-2))
            moves.addElement(new CheckersMove(row, col, row+2, col-2));
         if (canJump(player, row, col, row-1, col-1, row-2, col-2))
            moves.addElement(new CheckersMove(row, col, row-2, col-2));
         //Cardinal
         if(canJump(player, row, col, row-1, col, row-2, col))
        	 moves.addElement(new CheckersMove(row, col, row-2, col));
         if(canJump(player, row, col, row+1, col, row+2, col))
        	 moves.addElement(new CheckersMove(row, col, row+2, col));
         if(canJump(player, row, col, row, col-1, row, col-2))
        	 moves.addElement(new CheckersMove(row, col, row, col-2));
         if(canJump(player, row, col, row, col+1, row, col+2))
        	 moves.addElement(new CheckersMove(row, col, row, col+2));
      }
      
      if (moves.size() == 0)
         return null;
      else {
         CheckersMove[] moveArray = new CheckersMove[moves.size()];
         for (int i = 0; i < moves.size(); i++)
            moveArray[i] = (CheckersMove)moves.elementAt(i);
         return moveArray;
      }
   }  // end getLegalMovesFrom()
 
   public boolean justWarp(int player, int r, int c)
   {
	   if(isWarp[r][c] == WARP[0])
	   {
		   if(r == 2 && c == 3)
		   {
			   	warpRow = 5;
			   	warpCol = 4;
		   }
		   else
			 {
			   	warpRow = 2;
				warpCol = 3;
			 }
		   return true;
	   }
	   if(isWarp[r][c] == WARP[1])
	   {
		   if(r == 2 && c == 4)
			 {
				 warpRow = 5;
				 warpCol = 3;
			 }
			 else
			 {
				 warpRow = 2;
				 warpCol = 4;
			 }
			 return true;
		 }
	   if(isWarp[r][c] == WARP[2])
	   {
		   if(r == 3 && c == 2)
			 {
				 warpRow = 4;
				 warpCol = 5; 
			 }
			 else
			 {
				 warpRow = 3;
				 warpCol = 2; 
			 }
			 return true;		 
		 }
	   if(isWarp[r][c] == WARP[3])
	   {
		   if(r == 3 && c == 5)
			 {
				 warpRow = 4;
				 warpCol = 2; 
			 }
			 else
			 {
				 warpRow = 3; 
				 warpCol = 5;
			 }
			 return true;
		 }
	   if(isWarp[r][c] == WARP[4])
	   {
		   if(r == 2 && c == 1)
			 {
				 warpRow = 6;
				 warpCol = 5;
			 }
			 else
			 {
				 warpRow = 2;
				 warpCol = 1;
			 }
		   return true;	   
	   }
	   if(isWarp[r][c] == WARP[5])
	   {
		   if(r == 2 && c == 6)
			 {
				 warpRow = 5;
				 warpCol = 1;
			 }
			 else
			 {
				 warpRow = 2;
				 warpCol = 6;
			 }
		   return true;
	   }
	   
	   return false;
   }
   
   public boolean canWarp(int player, int r1, int c1, int r2, int c2)
   {
	   if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
	         return false;  // (r2,c2) is off the board.
	  
	 if(board[r2][c2] == WARP[0])
	 {
		 //moves.addElement(new CheckersMove(r1, c1, r2, c2));
		 if(board[2][3] == WARP[0] && board[5][4] == WARP[0])
		 {
			 if(r2 == 2 && c2 == 3)
			 {
				 warpRow = 5;
				 warpCol = 4;
			 }
			 else
			 {
				 warpRow = 2;
				 warpCol = 3;
			 }
			 return true;
		 }
	 }
	   
	 if(board[r2][c2] == WARP[1])
	 {
		 if(board[2][4] == WARP[1] && board[5][3] == WARP[1])
		 {
			 if(r2 == 2 && c2 == 4)
			 {
				 warpRow = 5;
				 warpCol = 3;
			 }
			 else
			 {
				 warpRow = 2;
				 warpCol = 4;
			 }
			 return true;
		 }
	 }
	 
	 if(board[r2][c2] == WARP[2])
	 {
		 if(board[3][2] == WARP[2] && board[4][5] == WARP[2])
		 {
			 if(r2 == 3 && c2 == 2)
			 {
				 warpRow = 4;
				 warpCol = 5; 
			 }
			 else
			 {
				 warpRow = 3;
				 warpCol = 2; 
			 }
			 return true;		 
		 }
	 }
	 
	 if(board[r2][c2] == WARP[3])
	 {
		 if(board[3][5] == WARP[3] && board[4][2] == WARP[3])
		 {
			 if(r2 == 3 && c2 == 5)
			 {
				 warpRow = 4;
				 warpCol = 2; 
			 }
			 else
			 {
				 warpRow = 3; 
				 warpCol = 5;
			 }
			 return true;
		 }
	 }
	 
	 if(board[r2][c2] == WARP[4])
	 {
		 if(board[2][1] == WARP[4] && board[6][5] == WARP[4])
		 {
			 if(r2 == 2 && c2 == 1)
			 {
				 warpRow = 6;
				 warpCol = 5;
			 }
			 else
			 {
				 warpRow = 2;
				 warpCol = 1;
			 }
			 return true;
		 }
	 }
	 
	 if(board[r2][c2] == WARP[5])
	 {
		 if(board[2][6] == WARP[5] && board[5][1] == WARP[5])
		 {
			 if(r2 == 2 && c2 == 6)
			 {
				 warpRow = 5;
				 warpCol = 1;
			 }
			 else
			 {
				 warpRow = 2;
				 warpCol = 6;
			 }
			 return true;
		 }
	 }
	   return false;
   }
   
   public boolean canWarpJump(int player, int r1, int c1, int r2, int c2, int r3, int c3)
   {
	   if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
	         return false;
	   int enemy;
	   if(player == WHITE)
		   enemy = BLACK;
	   else
		   enemy = WHITE;
	   
	   if(board[r2][c2] == WARP[0])
		   if(board[2][3] == WARP[0] || board[5][4] == WARP[0] && player != board[2][3] && player != board[5][4])
		   {
			   if(board[2][3] == enemy)
			   {
				   //board[5][4] = WARP[0];
				   return true;
			   }
			   if(board[5][4] == enemy)
			   {
				   //board[2][3] = WARP[0];
				   return true;
			   }
		   }
	   
	   
	   
	   return false; 
   }
   
   public boolean getAlternateWarpCoordinates(int enemy, int r2, int c2, int incr, int incc)
   {
	   if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
	         return false;
	   
	   if(board[r2][c2] == WARP[0])
		 {
				 if(r2 == 2 && c2 == 3)
				 {
					 warpJumpRow = 5 + incr;
					 warpJumpCol = 4 + incc;
				 }
				 else
				 {
					 warpJumpRow = 2 + incr;
					 warpJumpCol = 3 + incc;
				 }
				 return true;
		 }
		   
		 if(board[r2][c2] == WARP[1])
		 {
				 if(r2 == 2 && c2 == 4)
				 {
					 warpJumpRow = 5 + incr;
					 warpJumpCol = 3 + incc;
				 }
				 else
				 {
					 warpJumpRow = 2 + incr;
					 warpJumpCol = 4 + incc;
				 }
				 return true;
		 }
		 
		 if(board[r2][c2] == WARP[2])
		 {
			 if(r2 == 3 && c2 == 2)
				 {
					 warpJumpRow = 4 + incr;
					 warpJumpCol = 5 + incc; 
				 }
				 else
				 {
					 warpJumpRow = 3 + incr;
					 warpJumpCol = 2 + incc; 
				 }
				 return true;		 
		 }
		 
		 if(board[r2][c2] == WARP[3])
		 {
				 if(r2 == 3 && c2 == 5)
				 {
					 warpJumpRow = 4 + incr;
					 warpJumpCol = 2 + incc; 
				 }
				 else
				 {
					 warpJumpRow = 3 + incr; 
					 warpJumpCol = 5 + incc;
				 }
				 return true;
			 
		 }
		 
		 if(board[r2][c2] == WARP[4])
		 {
				 if(r2 == 2 && c2 == 1)
				 {
					 warpJumpRow = 6 + incr;
					 warpJumpCol = 5 + incc;
				 }
				 else
				 {
					 warpJumpRow = 2 + incr;
					 warpJumpCol = 1 + incc;
				 }
				 return true;
			
		 }
		 
		 if(board[r2][c2] == WARP[5])
		 {

				 if(r2 == 2 && c2 == 6)
				 {
					 warpJumpRow = 5 + incr;
					 warpJumpCol = 1 + incc;
				 }
				 else
				 {
					 warpJumpRow = 2 + incr;
					 warpJumpCol = 6 + incc;
				 }
				 return true;
		 }
	   
	   return false;
   }
   
   private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {
           // This is called by the two previous methods to check whether the
           // player can legally jump from (r1,c1) to (r3,c3).  It is assumed
           // that the player has a piece at (r1,c1), that (r3,c3) is a position
           // that is 2 rows and 2 columns distant from (r1,c1) and that 
           // (r2,c2) is the square between (r1,c1) and (r3,c3).
           
      if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
         return false;  // (r3,c3) is off the board.
         
      /*
      for(int i=0; i<6;i++)
    	  if(board[r2][c2] == WARP[i])
    	  {
    		  return true;
    	  }
      */
      if (board[r3][c3] == BLACK || board[r3][c3] == WHITE)
         return false;  // (r3,c3) already contains a piece.
         
      if (player == WHITE) {
         if (board[r2][c2] != BLACK && board[r2][c2] != BLACK_SPLIT)
            return false;  // There is no black piece to jump.
         return true;  // The jump is legal.
      }
      else {
       
         if (board[r2][c2] != WHITE && board[r2][c2] != WHITE_SPLIT)
            return false;  // There is no red piece to jump.
         return true;  // The jump is legal.
      }

   }  // end canJump()
   

   private boolean canMove(int player, int r1, int c1, int r2, int c2) {
         // This is called by the getLegalMoves() method to determine whether
         // the player can legally move from (r1,c1) to (r2,c2).  It is
         // assumed that (r1,r2) contains one of the player's pieces and
         // that (r2,c2) is a neighboring square.
         
      if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
         return false;  // (r2,c2) is off the board.
      
      for(int i=0; i<6;i++)
    	  if(board[r2][c2] == WARP[i])
    	  {
    		  return true;
    	  }
      
      if (board[r2][c2] != EMPTY )
         return false;  // (r2,c2) already contains a piece.
      /*
      if (player == WHITE) {
         if (board[r1][c1] == WHITE)
             //return false;  // Regular red piece can only move down.
          return true;  // The move is legal.
      }
      else {
         if (board[r1][c1] == BLACK)
            // return false;  // Regular black piece can only move up.
          return true;  // The move is legal.
      }
      */
      return true;
   }  // end canMove()
   

} // end class CheckersData
