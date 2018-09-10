import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.lang.Math;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.io.*;
class Agent {
    int[] dest = null;
    State path;
    Plan search = new Plan();
    boolean arrive = false;
    boolean a = false;
    boolean u = false;
    
    
	void drawPlan(Graphics g, Model m) {
		g.setColor(Color.red);
		
		if (!arrive && path != null){
            State curr = path;
            State next = new State();
            while (curr.parent != null){
                next = curr;
                curr = curr.parent;
                g.drawLine(curr.x, curr.y, next.x, next.y);
                
            }
        }
        if (!arrive && search.frontier != null){
            g.setColor(Color.blue);
            Iterator itr = search.frontier.iterator();
            while (itr.hasNext()){
                State temp = (State)itr.next();
                g.fillOval(temp.x, temp.y, 10, 10);
            }
        }
	}

	void update(Model m)
	{
        search.setModel(m);
		Controller c = m.getController();
		while(true)
		{
			MouseEvent e = c.nextMouseEvent();
			if (e == null)
                break;
                
            if (dest == null) dest = new int[2]; //"lazy loading"
			dest[0] = e.getX();
			dest[1] = e.getY();
			
				
		}
		if (dest != null){
            arrive = false;
            //set new goal and start based upon mouse event
            State goal = new State(dest[0], dest[1]);
            //start state = state of robo man (sprite[0])
            State start = new State((int)m.getX(), (int)m.getY());
            if (dest[0]/10 == start.x/10 && dest[1]/10 == start.y/10){
                arrive = true;
                return;
            }
            if (u){
                //use UCS to find final state in path
                path = search.UCS(start, goal);
            }
            else if (a){
                path = search.Asearch(start, goal);
                
            }
            //find the next step in the path and set getDestination
            State curr = path;
            State next = new State();
            while (curr.parent != null){
                next = curr;
                curr = curr.parent;
            }
            m.setDestination(next.x, next.y);
        }
	}

	public static void main(String[] args) throws Exception
	{
		Controller.playGame();
	}
}
class State{
    double cost;
    double Hcost = 0; //estimate cost for A* stays 0 for UCS
    State parent;
    int x, y;
    State(){
        cost = 0;
        x = 0;
        y = 0;
        parent = null;
    }
    State(int xx, int yy)
    {
        cost = 0;
        parent = null;
        x = xx;
        y = yy;
    }
    //Copy constructor "deep"
    State(double c, State prev){
        //copies the cost of the state
        cost = c;
        //sets the parent to the given previos state
        parent = prev;
        
        x = prev.x;
        y = prev.y;
    }
}
class StateComparator implements Comparator<State>
{
    public int compare(State a, State b)
    {
        if ((a.x/10) < (b.x/10)) return -1;
        else if ((a.x/10) > (b.x/10)) return 1;
        if ((a.y/10) < (b.y/10)) return -1;
        else if ((a.y/10) > (b.y/10)) return 1;
        return 0;
    }
}
class CostComparator implements Comparator<State>
{
    public int compare(State a, State b)
    {
        if (a.cost + a.Hcost < b.cost + b.Hcost) return -1;
        else if (a.cost + a.Hcost > b.cost + b.Hcost) return 1;
        return 0;
    }
    
}

class Plan{
    Model m;
    PriorityQueue<State> frontier; //make this a member variable so it can be drawn
    float fastSpeed = Float.MAX_VALUE;
    public void setModel(Model model){
        m = model;
    }
    public void updateChild(State child, int move)
    {
        //distance is based upon 10 pixels of movement (i.e. 10 pixels is 1 distance)
        if (move == 0){
            //up and left
            child.cost += Math.sqrt(200.0)/m.getTravelSpeed(child.x, child.y);
            
            child.x -= 10;
            child.y -= 10;
            
        }
        else if (move == 1){
            //up
            child.cost += 10/m.getTravelSpeed(child.x, child.y);
            child.y -= 10;
            
            
        }
        else if (move == 2){
            //up and right
            child.cost += Math.sqrt(200.0)/m.getTravelSpeed(child.x, child.y);
            child.x += 10;
            child.y -= 10;
            

        }
        else if (move == 3){
            //right
            child.cost += 10/m.getTravelSpeed(child.x, child.y);
            child.x += 10;
            
            
        }
        else if (move == 4){
            //down and right
            child.cost += Math.sqrt(200.0)/m.getTravelSpeed(child.x, child.y);
            child.x += 10;
            child.y += 10;
            
            
        }
        else if (move == 5){
            //down
            child.cost += 10/m.getTravelSpeed(child.x, child.y);
            child.y += 10;
            
            
        }
        else if (move == 6){
            //down and left
            child.cost += Math.sqrt(200)/m.getTravelSpeed(child.x, child.y);
            child.x -= 10;
            child.y += 10;
            
            
        }
        else if (move == 7){
            //left
            child.cost += 10/m.getTravelSpeed(child.x, child.y);
            child.x -= 10;
            
            
        }
    }
    public State UCS(State start, State goal){
        StateComparator comparer = new StateComparator();
        CostComparator costComparer = new CostComparator();
        TreeSet<State> set = new TreeSet<State>(comparer); //stores visited states
        frontier = new PriorityQueue<State>(costComparer); //stores frontier
        
        
        start.cost = 0;
        start.parent = null;
        set.add(start);
        frontier.add(start);
        
        while(frontier.peek() != null){
            State parent = frontier.poll();
            
            if (parent.x/10 == goal.x/10 && parent.y/10 == goal.y/10) return parent;
            if (parent.x < 1200 && parent.x >= 0 && parent.y < 600 && parent.y >= 0){
                for (int move = 0; move < 8; move++)
                {
                    State child = new State(parent.cost, parent);
                    
                    
                    
                        //updates the "child" state, so it is actually a child state
                        //then calculates the new cost based upon the move
                        updateChild(child, move);
                        
                        if(set.contains(child)){
                            State oldchild = set.floor(child);
                            if (oldchild.cost > child.cost){
                                set.remove(oldchild);
                                oldchild.cost = child.cost;
                                oldchild.parent = child.parent;
                                set.add(oldchild);
                            }
                        }
                        else{
                            frontier.add(child);
                            set.add(child);
                            
                        }

                }
            }
            
        
        }
       
        throw new RuntimeException("there is no path to the goal");
    }
    
    public State Asearch(State start, State goal){
        StateComparator comparer = new StateComparator();
        CostComparator costComparer = new CostComparator();
        TreeSet<State> set = new TreeSet<State>(comparer); //stores visited states
        frontier = new PriorityQueue<State>(costComparer); //stores frontier
        
        if (fastSpeed == Float.MAX_VALUE){
            //moves by 10 because each 10x10 is one state
            for (int x = 0; x < 1200; x = x + 10){
                for (int y = 0; y < 600; y = y + 10){
                    if (fastSpeed > m.getTravelSpeed(x, y)){
                        fastSpeed = m.getTravelSpeed(x, y);
                    }
                }
            }
            
        }
        
        //A* search the cost each state holds includes heuristic estimate as well
        start.cost = 0;
        start.parent = null;
        set.add(start);
        frontier.add(start);
        start.Hcost = estimate(start, goal);
        while (frontier.peek() != null){
            State parent = frontier.poll();
            if (parent.x/10 == goal.x/10 && parent.y/10 == goal.y/10) return parent;
            if (parent.x < 1200 && parent.x >= 0 && parent.y < 600 && parent.y >= 0){
                for (int move = 0; move < 8; move++)
                {
                    State child = new State(parent.cost, parent);
                    //updates the "child" state, so it is actually a child state
                    //then calculates the new cost based upon the move
                    updateChild(child, move);
                    //calculate Hcost for child (distance from child to goal times lowest travel speed
                    child.Hcost = estimate(child, goal);
                    if(set.contains(child)){
                        State oldchild = set.floor(child);
                        if (oldchild.cost + oldchild.Hcost > child.cost + child.Hcost){
                            set.remove(oldchild);
                            oldchild.cost = child.cost;
                            oldchild.Hcost = child.Hcost;
                            oldchild.parent = child.parent;
                            set.add(oldchild);
                        }
                    }
                    else{
                        frontier.add(child);
                        set.add(child);
                        
                    }
                }
            }
        }
        throw new RuntimeException("there is no path to the goal");
    }
    public double estimate(State a, State b){
        return (Math.sqrt(((a.x/10 - b.x/10) * (a.x/10 - b.x/10)) + ((a.y/10 - b.y/10) * (a.y/10 - b.y/10))) / fastSpeed);

    }
}
