import java.util.Comparator;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;


class State{
    State parent;
    byte[] state;
    
    //constructor
    State(){
        parent = null;
        state = new byte[22];
    }
    
    //deep copy constructor
    State(State copyme){
        parent = copyme;
        state = new byte[22];
        for (int i = 0; i < 22; i++)
        {
            state[i] = copyme.state[i];
        }
    }
}
class Game{
    State root;
    
    Game(){
        root = new State();
        //board = new boolean[10][10];
        
    }
    
    
    
    
    /*public void printBoard(){
        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++){
                if (board[j][i]) System.out.print(" X ");
                else System.out.print("   ");
            }
            System.out.println();
        }
    }*/
    
    
    
    
    public boolean draw(boolean[][] board, State test, int id, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4){
        if (drawBlock(board, test.state[2*id] + x1, test.state[2*id + 1] + y1)) return true;
        if (drawBlock(board, test.state[2*id] + x2, test.state[2*id + 1] + y2)) return true;
        if (drawBlock(board, test.state[2*id] + x3, test.state[2*id + 1] + y3)) return true;
        if (drawBlock(board, test.state[2*id] + x4, test.state[2*id + 1] + y4)) return true;
        
        return false;
    }
    
    public boolean draw(boolean[][] board, State test, int id, int x1, int y1, int x2, int y2, int x3, int y3){
        if (drawBlock(board, test.state[2*id] + x1, test.state[2*id + 1] + y1)) return true;
        if (drawBlock(board, test.state[2*id] + x2, test.state[2*id + 1] + y2)) return true;
        if (drawBlock(board, test.state[2*id] + x3, test.state[2*id + 1] + y3)) return true;
        
        return false;
    }
    
    public boolean drawBlock(boolean[][] board, int x, int y){
        //check if board is already occupied true = occupied
        if (x > 9 || x < 0 || y > 9 || y < 0) return true;
        if (board[x][y]) return true;
        
        
        board[x][y] = true;
        return false;
    }
    
    
    public void updateState(State update, int piece, int move){
        //select piece
        /*Scanner scan = new Scanner(System.in);
        System.out.print("Select piece you'd like to move: ");
        int piece = scan.nextInt();
        System.out.print("Enter move: ");
        int move = scan.nextInt();*/
        if (move == 1) update.state[2*piece + 1]--; //up
        if (move == 2) update.state[2*piece + 1]++; //down
        if (move == 3) update.state[2*piece]--; //left
        if (move == 4) update.state[2*piece]++; //right
        
    }
    public boolean testState(State test){
        //reset the board
        boolean[][] board = new boolean[10][10];
        //"draw" black spaces
        for (int i = 0; i < 10; i++){
            board[0][i] = true;
            board[9][i] = true;
        }
        for (int i = 1; i < 9; i++){
            board[i][0] = true;
            board[i][9] = true;
        }
        board[1][1] = true; board[1][2] = true; board[2][1] = true;
        
        board[7][1] = true; board[8][1] = true; board[8][2] = true;
        
        board[1][7] = true; board[1][8] = true; board[2][8] = true;
        
        board[8][7] = true; board[7][8] = true; board[8][8] = true;
        
        board[3][4] = true; board[4][3] = true; board[4][4] = true;
        
        //"draw" pieces
        //draw returns true == overlap
        for (int id = 0; id < 11; id++){
            if (id == 0){
                if (draw(board, test, id, 1, 3, 2, 3, 1, 4, 2, 4)) return false;
            }
            else if(id == 1){
                if (draw(board, test, id, 1, 5, 1, 6, 2, 6)) return false;
            }
            else if(id == 2){
                if (draw(board, test, id, 2, 5, 3, 5, 3, 6)) return false;
            }
            else if(id == 3){
                if (draw(board, test, id, 3, 7, 3, 8, 4, 8)) return false;
            }
            else if(id == 4){
                if (draw(board, test, id, 4, 7, 5, 7, 5, 8)) return false;
            }
            else if(id == 5){
                if (draw(board, test, id, 6, 7, 7, 7, 6, 8)) return false;
            }
            else if(id == 6){
                if (draw(board, test, id, 5, 4, 5, 5, 5, 6, 4, 5)) return false;
            }
            else if(id == 7){
                if (draw(board, test, id, 6, 4, 6, 5, 6, 6, 7, 5)) return false;
            }
            else if(id == 8){
                if (draw(board, test, id, 8, 5, 8, 6, 7, 6)) return false;
            }
            else if(id == 9){
                if (draw(board, test, id, 6, 2, 6, 3, 5, 3)) return false;
            }
            else if(id == 10){
                if (draw(board, test, id, 5, 1, 6, 1, 5, 2)) return false;
            }
            
        }
        
        
        return true;
    }
    public boolean winState(State test){
        if (test.state[0] == 4 && test.state[1] == -2) return true;
        
        return false;
    }
    
    class StateComparator implements Comparator<State>
    {
        public int compare(State a, State b)
        {
            for(int i = 0; i < 22; i++)
            {
                if(a.state[i] < b.state[i])
                    return -1;
                else if(a.state[i] > b.state[i])
                    return 1;
            }
            return 0;
        }
    }
    
    public State BFS(){
        StateComparator comparer = new StateComparator();
        TreeSet<State> set = new TreeSet<State>(comparer); //stores visited states
        Queue<State> queue = new LinkedList<State>(); //stores states to be visited FIFOs
        
        

        queue.add(root); //adds initial state to queue
        set.add(root);
        while (queue.peek() != null){
            //take first state on queue
            State parent = queue.remove();
            //check if state is a win state
            if (winState(parent)) return parent;
            
            //generate all possible children states from that parent
            for (int pieces = 0; pieces < 11; pieces++){
                for (int moves = 1; moves < 5; moves++){
                
                    //create deep copy of parent
                    State child = new State(parent);
                    
                    //update deep copy to make a child 
                    updateState(child, pieces, moves);
                        
                    //check if child state has been visited
                    if (!set.contains(child)){
                        
                        //check if child state is valid
                        if (testState(child)){
                            //add child to set to prevent duplicates being put on the queue
                            set.add(child);
                            //add child to back of queue
                            queue.add(child);
                        }
                    }
                }
            }
        }

        //return null if no win state found
        return null;

    }
    
    public void writeMoves(State win) throws IOException{
        State curr = win;
        FileWriter filewrite = new FileWriter("results.txt");
        PrintWriter output = new PrintWriter(filewrite);
        while (curr.parent != null){
            String data = stateToString(curr.state);
            output.println(data);
            curr = curr.parent;
        }
        String data = stateToString(curr.state);
        output.println(data);
        output.close();
    }
    static String stateToString(byte[] b)
	{
		StringBuilder sb = new StringBuilder();
		
		
		for(int i = 0; i < b.length; i = i + 2) {
            sb.append("(");
            sb.append(Byte.toString(b[i]));
			sb.append(",");
			sb.append(Byte.toString(b[i + 1]));
			sb.append("), ");
		}
		return sb.toString();
	}
}
class Main{
    
    public static void main(String[] args){
        Game game = new Game();
        State win = game.BFS();
        try
        {
            game.writeMoves(win);
        } 
        catch(Exception e)
        {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        
        if (game.winState(win)) System.out.println("Congratulations! You won!");
        
        
        
        
        
    }
}
