// Important Notes
// In the Array
//     1 == RED
//     2 == BLACK
//     3 == RED King
//     4 == BLACK King
// Default movement is:
// Red is starting top going down
// Blask is starting bottom going up

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;

import javax.swing.JFrame;


public class GameBrain {

	public static void main(String[] args) {
		int[][] board = new int[8][8];
		//Scanner keyboard = new Scanner(System.in);
		int x=-1, y=-1, color=0;
		Display table = new Display();
		table.setVisible(true);
		table.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//table.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//table.setUndecorated(true);
       
       /*
	    printBoard(board);
		resetBoard(board);
		printBoard(board);
		
		board[1][2] = 0;
		board[3][2] = 2;
		board[5][2] = 0;
		board[7][2] = 0;
		
		board[1][4] = 1;
		board[3][4] = 1;
		board[5][4] = 2;
		
		board[0][5] = 0;
		board[2][5] = 0;
		board[4][5] = 0;
		
		board[3][6] = 1;
		board[5][6] = 0;
		board[7][6] = 0;
        */
	
        ArrayList<ArrayList<Integer>> validMoves = new ArrayList<ArrayList<Integer>>();
        
        int portNumber = 8080;

        try {
            ServerSocket serverSocket =
                new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();     
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            System.out.println("Connection Established");
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recieved: "+inputLine);
                String[] t = inputLine.split("\\s");
                System.out.println(t[0]);
                if(t[0].equals("MIA")) {
                    System.out.println("X: " + t[1] + "Y: " + t[2]);
                    x = 7-Integer.parseInt(t[1]);
                    y = Integer.parseInt(t[2]);
                    color = board[x][y];
		            validMoves = pickup(x,y, board, false);
		            System.out.println(validMoves);
		            table.highlightMoves(validMoves);
                    board[x][y] = 0;
                } else if(t[0].equals("YES")) {
                    //inputLine = in.readLine();
                    placeDown(7-Integer.parseInt(t[1]),Integer.parseInt(t[2]),x,y,board,validMoves,table,Integer.parseInt(t[3]));
                    printBoard(board);
                    System.out.println();
                } else if(t[0].equals("quit")) {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

                /*
		System.out.println();
		printBoard(board);
		
		System.out.println("Pick up: ");
		x = keyboard.nextInt();
		y = keyboard.nextInt();
		
		ArrayList<ArrayList<Integer>> validMoves = checkMoves(x, y, board);
		System.out.println(validMoves);
		table.highlightMoves(validMoves);
		
		System.out.println("Place Down: ");
		int placeX = keyboard.nextInt();
		int placeY = keyboard.nextInt();
		
		System.out.println(determineJumped(x,y,placeX,placeY,validMoves));
		keyboard.close();
        */
	}

	//Resets the board to standard checkers starting
	public static void resetBoard(int[][] board) {
		for(int i=0; i < board[0].length; i++) {
			for(int j=0; j < board.length; j++) {
				if(i%2 != j%2){
					if(i < 3){
						board[j][i] = 1;
					} else if(i > 4) {
						board[j][i] = 2;
					}
				} 
			}
		}
	}
	
	//Prints how the board currently looks to the console
	public static void printBoard(int[][] board) {
		for(int i=0; i < board[0].length; i++) {
			for(int j=0; j < board.length; j++) {
				System.out.print(board[j][i] + " ");
			}
			System.out.println();
		}
	}
	
	//Used for testing things with ArrayLists
	//Delete this at some point
	public static void test(ArrayList<Integer> temp) {
		ArrayList<Integer> q = new ArrayList<Integer>();
		q.add(12);
		
		temp.clear();
		temp.addAll(q);
	}
	
	//Checks for valid moves from a starting location
	public static ArrayList<ArrayList<Integer>> checkMoves(int x, int y, int[][] board) {
		ArrayList<ArrayList<Integer>> validMoves = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> tmp;
		ArrayList<ArrayList<Integer>> temp;
		boolean blackRed = board[x][y]%2==1;
		
		if(blackRed){
			//Check top left
			if(x<7 && y<7 && board[x+1][y+1] == 0){
				//valid move
				tmp = new ArrayList<Integer>();
				tmp.add(x+1);
				tmp.add(y+1);
				validMoves.add(tmp);
			} else if(x<6 && y<6 && board[x+1][y+1]%2 == 0) {
				if(board[x+2][y+2] == 0) {
					//valid jump
					tmp = new ArrayList<Integer>();
					temp = new ArrayList<ArrayList<Integer>>();
					tmp.add(x+2);
					tmp.add(y+2);
					temp.add(tmp);
					checkJump(x+2, y+2, board, x, y, (board[x][y] > 2), blackRed, temp);
					validMoves.addAll(temp);
				} else {
					//Invalid
				}
			} else {
				//Invalid
			}


			//Check top right
			if(x>0 && y<7 && board[x-1][y+1] == 0){
				//valid move
				tmp = new ArrayList<Integer>();
				tmp.add(x-1);
				tmp.add(y+1);
			} else if(x>1 && y<6 && board[x-1][y+1]%2 == 0) {
				if(board[x-2][y+2] == 0) {
					//valid jump
					tmp = new ArrayList<Integer>();
					temp = new ArrayList<ArrayList<Integer>>();
					tmp.add(x-2);
					tmp.add(y+2);
					temp.add(tmp);
					checkJump(x-2, y+2, board, x, y, (board[x][y] > 2), blackRed, temp);
					validMoves.addAll(temp);
				} else {
					//Invalid
				}
			} else {
				//Invalid
			}


            if(board[x][y] == 3) {
                //Check bottom left
                if(x>0 && y>0 && board[x-1][y-1] == 0){
                    //valid move
                    tmp = new ArrayList<Integer>();
                    tmp.add(x-1);
                    tmp.add(y-1);
                } else if(x>1 && y>1 && board[x-1][y-1]%2 == 0) {
                    if(board[x-2][y-2] == 0) {
                        //valid jump
                        tmp = new ArrayList<Integer>();
                        temp = new ArrayList<ArrayList<Integer>>();
                        tmp.add(x-2);
                        tmp.add(y-2);
                        temp.add(tmp);
                        checkJump(x-2, y-2, board, x, y, (board[x][y] > 2), blackRed, temp);
                        validMoves.addAll(temp);
                    } else {
                        //Invalid
                    }
                } else {
                    //Invalid
                }


                //Check Bottom Right
                if(x<7 && y>0 && board[x+1][y-1] == 0){
                    //valid move
                    tmp = new ArrayList<Integer>();
                    tmp.add(x+1);
                    tmp.add(y-1);
                } else if(x<6 && y>1 && board[x+1][y-1]%2 == 0) {
                    if(board[x+2][y-2] == 0) {
                        //valid jump
                        tmp = new ArrayList<Integer>();
                        temp = new ArrayList<ArrayList<Integer>>();
                        tmp.add(x+2);
                        tmp.add(y-2);
                        temp.add(tmp);
                        checkJump(x+2, y-2, board, x, y, (board[x][y] > 2), blackRed, temp);
                        validMoves.addAll(temp);
                    } else {
                        //Invalid
                    }
                } else {
                    //Invalid
                }
            }
		} else { // Black Piece
			//Check top left
			if(x>0 && y>0 && board[x-1][y-1] == 0){
				//valid move
				tmp = new ArrayList<Integer>();
				tmp.add(x-1);
				tmp.add(y-1);
			} else if(x>1 && y>1 && board[x-1][y-1]%2 == 1) {
				if(board[x-2][y-2] == 0) {
					//valid jump
					tmp = new ArrayList<Integer>();
					temp = new ArrayList<ArrayList<Integer>>();
					tmp.add(x-2);
					tmp.add(y-2);
					temp.add(tmp);
					checkJump(x-2, y-2, board, x, y, (board[x][y] > 2), blackRed, temp);
					validMoves.addAll(temp);
				} else {
					//Invalid
				}
			} else {
				//Invalid
			}


			//Check top right
			if(x<7 && y>0 && board[x+1][y-1] == 0){
				//valid move
				tmp = new ArrayList<Integer>();
				tmp.add(x+1);
				tmp.add(y-1);
			} else if(x<6 && y>1 && board[x+1][y-1]%2 == 1) {
				if(board[x+2][y-2] == 0) {
					//valid jump
					tmp = new ArrayList<Integer>();
					temp = new ArrayList<ArrayList<Integer>>();
					tmp.add(x+2);
					tmp.add(y-2);
					temp.add(tmp);
					checkJump(x+2, y-2, board, x, y, (board[x][y] > 2), blackRed, temp);
					validMoves.addAll(temp);
				} else {
					//Invalid
				}
			} else {
				//Invalid
			}


            if(board[x][y] == 4) {
                //Check bottom left
                if(x<7 && y<7 && board[x+1][y+1] == 0){
                    //valid move
                    tmp = new ArrayList<Integer>();
                    tmp.add(x+1);
                    tmp.add(y+1);
                } else if(x<6 && y<6 && board[x+1][y+1]%2 == 1) {
                    if(board[x+2][y+2] == 0) {
                        //valid jump
                        tmp = new ArrayList<Integer>();
                        temp = new ArrayList<ArrayList<Integer>>();
                        tmp.add(x+2);
                        tmp.add(y+2);
                        temp.add(tmp);
                        checkJump(x+2, y+2, board, x, y, (board[x][y] > 2), blackRed, temp);
                        validMoves.addAll(temp);
                    } else {
                        //Invalid
                    }
                } else {
                    //Invalid
                }


                //Check bottom right
                if(x>0 && y<7 && board[x-1][y+1] == 0){
                    //valid move
                    tmp = new ArrayList<Integer>();
                    tmp.add(x-1);
                    tmp.add(y+1);
                } else if(x>1 && y<6 && board[x-1][y+1]%2 == 1) {
                    if(board[x-2][y+2] == 0) {
                        //valid jump
                        tmp = new ArrayList<Integer>();
                        temp = new ArrayList<ArrayList<Integer>>();
                        tmp.add(x-2);
                        tmp.add(y+2);
                        temp.add(tmp);
                        checkJump(x-2, y+2, board, x, y, (board[x][y] > 2), blackRed, temp);
                        validMoves.addAll(temp);
                    } else {
                        //Invalid
                    }
                } else {
                    //Invalid
                }
            }
		}
		
		return validMoves;
	}
	
	//Checks for valid jumps from given location
	//Calls itself recursively until there are no more valid moves
	//Checks to make sure it is not jumping to where it came from to keep infinite loops from happening
	//Potential problem is running into a set of jumps that form a loop of some sort
	public static void checkJump(int x, int y, int[][] board, int ox, int oy, boolean king, boolean blackRed, ArrayList<ArrayList<Integer>> jump) {
		ArrayList<Integer> tmp;
		ArrayList<ArrayList<Integer>> finalList = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> temp;
		
		
		if(blackRed) {
			//Check top left
			if(x<6 && y<6 && board[x+1][y+1] != 0 && board[x+1][y+1]%2 == 0) {
				if((ox-2 != x || oy-2 != y) && board[x+2][y+2] == 0) {
					//valid jump
					tmp = new ArrayList<Integer>();
					tmp.addAll(jump.get(0));
					temp = new ArrayList<ArrayList<Integer>>();
					tmp.add(x+2);
					tmp.add(y+2);
					temp.add(tmp);
					checkJump(x+2, y+2, board, x, y, king, blackRed, temp);
					finalList.addAll(temp);
				} else {
					//Invalid
				}
			} else {
				//Invalid
			}

			//Check top right
			if(x>1 && y<6 && board[x-1][y+1] != 0 && board[x-1][y+1]%2 == 0) {
				if((ox+2 != x || oy-2 != y) && board[x-2][y+2] == 0) {
					//valid jump
					tmp = new ArrayList<Integer>();
					tmp.addAll(jump.get(0));
					temp = new ArrayList<ArrayList<Integer>>();
					tmp.add(x-2);
					tmp.add(y+2);
					temp.add(tmp);
					checkJump(x-2, y+2, board, x, y, king, blackRed, temp);
					finalList.addAll(temp);
				} else {
					//Invalid
				}
			} else {
				//Invalid
			}

			if(king) {
				//Check bottom right
				if(x>1 && y>1 && board[x-1][y-1] != 0 && board[x-1][y-1]%2 == 0) {
					if((ox+2 != x || oy+2 != y) && board[x-2][y-2] == 0) {
						//valid jump
						tmp = new ArrayList<Integer>();
						tmp.addAll(jump.get(0));
						temp = new ArrayList<ArrayList<Integer>>();
						tmp.add(x-2);
						tmp.add(y-2);
						temp.add(tmp);
						checkJump(x-2, y-2, board, x, y, king, blackRed, temp);
						finalList.addAll(temp);
					} else {
						//Invalid
					}
				} else {
					//Invalid
				}

				//Check bottom left
				if(x<6 && y>1 && board[x+1][y-1] != 0 && board[x+1][y-1]%2 == 0) {
					if((ox-2 != x || oy+2 != y) && board[x+2][y-2] == 0) {
						//valid jump
						tmp = new ArrayList<Integer>();
						tmp.addAll(jump.get(0));
						temp = new ArrayList<ArrayList<Integer>>();
						tmp.add(x+2);
						tmp.add(y-2);
						temp.add(tmp);
						checkJump(x-2, y-2, board, x, y, king, blackRed, temp);
						finalList.addAll(temp);
					} else {
						//Invalid
					}
				} else {
					//Invalid
				}
			}
		} else {
			//Check top left
			if(x>1 && y>1 && board[x-1][y-1]%2 == 1) {
				if((ox+2 != x || oy+2 != y) && board[x-2][y-2] == 0) {
					//valid jump
					tmp = new ArrayList<Integer>();
					tmp.addAll(jump.get(0));
					temp = new ArrayList<ArrayList<Integer>>();
					tmp.add(x-2);
					tmp.add(y-2);
					temp.add(tmp);
					checkJump(x-2, y-2, board, x, y, king, blackRed, temp);
					finalList.addAll(temp);
				} else {
					//Invalid
				}
			} else {
				//Invalid
			}

			//Check top right
			if(x<6 && y>1 && board[x+1][y-1]%2 == 1) {
				if((ox-2 != x || oy+2 != y) && board[x+2][y-2] == 0) {
					//valid jump
					tmp = new ArrayList<Integer>();
					tmp.addAll(jump.get(0));
					temp = new ArrayList<ArrayList<Integer>>();
					tmp.add(x+2);
					tmp.add(y-2);
					temp.add(tmp);
					checkJump(x+2, y-2, board, x, y, king, blackRed, temp);
					finalList.addAll(temp);
				} else {
					//Invalid
				}
			} else {
				//Invalid
			}

			if(king) {
				//Check bottom right
				if(x<6 && y<6 && board[x+1][y+1]%2 == 1) {
					if((ox-2 != x || oy-2 != y) && board[x+2][y+2] == 0) {
						//valid jump
						tmp = new ArrayList<Integer>();
						tmp.addAll(jump.get(0));
						temp = new ArrayList<ArrayList<Integer>>();
						tmp.add(x+2);
						tmp.add(y+2);
						temp.add(tmp);
						checkJump(x+2, y+2, board, x, y, king, blackRed, temp);
						finalList.addAll(temp);
					} else {
						//Invalid
					}
				} else {
					//Invalid
				}

				//Check bottom left
				if(x>1 && y<6 && board[x-1][y+1]%2 == 1) {
					if((ox+2 != x || oy-2 != y) && board[x-2][y+2] == 0) {
						//valid jump
						tmp = new ArrayList<Integer>();
						tmp.addAll(jump.get(0));
						temp = new ArrayList<ArrayList<Integer>>();
						tmp.add(x-2);
						tmp.add(y+2);
						temp.add(tmp);
						checkJump(x-2, y+2, board, x, y, king, blackRed, temp);
						finalList.addAll(temp);
					} else {
						//Invalid
					}
				} else {
					//Invalid
				}
			}
		}
		
		if(finalList.size() != 0){
			jump.clear();
			jump.addAll(finalList);
		}
		//return jump;
	}

	//Determines which path the user took
	//Returns the pieces that need to be removed from the game
	//If two paths end where the piece was placed returns both sets of pieces that need to be removed
	public static ArrayList<ArrayList<Integer>> determineJumped(int x, int y, int endX, int endY, ArrayList<ArrayList<Integer>> moves) {
		ArrayList<ArrayList<Integer>> remove = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i < moves.size(); i++) {
			if(moves.get(i).get(moves.get(i).size()-1) == endY && moves.get(i).get(moves.get(i).size() - 2) == endX){
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				tmp.add(moves.get(i).get(0)+(x-moves.get(i).get(0))/2);
				tmp.add(moves.get(i).get(1)+(y-moves.get(i).get(1))/2);
				for(int j=0; j < moves.get(i).size()-3; j+=2) {
					tmp.add(moves.get(i).get(j+2)+(moves.get(i).get(j)-moves.get(i).get(j+2))/2);
					tmp.add(moves.get(i).get(j+3)+(moves.get(i).get(j+1)-moves.get(i).get(j+3))/2);
				}
				remove.add(tmp);
			}
		}
		return remove;
	}

	// Function to be called when a piece is picked up
	public static ArrayList<ArrayList<Integer>> pickup(int x, int y, int[][] board, boolean pieceRemoval) {
		if(pieceRemoval) {
			board[x][y] = 0;
			return null;
		} else {
			return checkMoves(x,y,board);
		}
	}
	
	// Function to be called when a piece is placed down
	public static void placeDown(int x, int y, int startX, int startY, int[][] board, ArrayList<ArrayList<Integer>> moves, Display table, int color) {
		if(startX == x && startY == y) {
			//still same players turn
            table.clearHighlights();
            board[x][y] = color;
		} else {
			// Determine and highlight which pieces have been jumped
			if(moves.size()!=0) {
                table.highlightJumped(determineJumped(startX, startY, x, y, moves));
            }
            board[x][y] = color;
			// Next players turnS
		}
		
	}
}
