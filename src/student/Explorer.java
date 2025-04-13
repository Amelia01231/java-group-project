package student;

import game.*;
import java.util.*;

/**
 * Main explorer
 */
public class Explorer {
    /**
     * Explore the cavern, trying to find the orb in as few steps as possible.
     * Once you find the orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     * <p>
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * <p>
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles).
     * <p>
     * To get information about the current state, use functions
     * getCurrentLocation(),
     * getNeighbours(), and
     * getDistanceToTarget()
     * in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
      * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        new OrbFinder(state).find();  // finds orb
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        new EscapePathfinder(state).escape();
    }
}

/**
 * Finds orbs
 */
class OrbFinder {
    private final ExplorationState state;
    private final Set<Long> visited = new HashSet<>();  // been here befor
    private final Stack<Long> path = new Stack<>();  // where we went
    private final Stack<List<NodeStatus>> neighborsStack = new Stack<>();  // neighbors stack lol

    /**
     * Constrctor
     * @param state the state of current exploration
     */
    public OrbFinder(ExplorationState state) {
        this.state = state;  // set the state
    }

    /**
     * Get the orb
     */
    public void find() {
        startLooking();  // starts looking
        while (!foundOrbYet()) {  // keep going til found
            if (!goToNextBestPlace()) {  // try move
                goBack();  // oops went wrong way
            }
        }
    }

    /**
     * Start the serch
     */
    private void startLooking() {
        visited.add(state.getCurrentLocation());  // been here
        path.push(state.getCurrentLocation());  // add to path
        neighborsStack.push(new ArrayList<>(state.getNeighbours()));  // neighbors i gues
    }

    /**
     * Check if found
     * @return true if yes
     */
    private boolean foundOrbYet() {
        return state.getDistanceToTarget() == 0;  // zero means got it
    }

    /**
     * Go somwhere better
     * @return true if moved
     */
    private boolean goToNextBestPlace() {
        List<NodeStatus> places = getGoodPlaces();  // get good places
        for (NodeStatus next : places) {  // look at places
            if (!visited.contains(next.nodeID())) {  // not been here
                moveThere(next);  // go there
                return true;
            }
        }
        return false;  // oops cant move
    }

    /**
     * Sort the places
     * @return list of good places
     */
    private List<NodeStatus> getGoodPlaces() {
        List<NodeStatus> places = new ArrayList<>(neighborsStack.peek());  // get places
        places.sort(Comparator.comparingInt(NodeStatus::distanceToTarget));
        return places;
    }

    /**
     * Go to that node
     * @param node the place to go
     */
    private void moveThere(NodeStatus node) {
        state.moveTo(node.nodeID());  // move there
        visited.add(node.nodeID());  // now been here
        path.push(node.nodeID());  // add to path
        neighborsStack.push(new ArrayList<>(state.getNeighbours()));  // new neighbors
    }

    /**
     * Go back when stuck
     */
    private void goBack() {
        path.pop();
        neighborsStack.pop();
        if (!path.isEmpty()) {
            state.moveTo(path.peek());  // go back
        }
    }
}

/**
 * Finds way out
 */
class EscapePathfinder {
    private final EscapeState state;
    private Node currentPlace;
    private Node exitPlace;

    /**
     * Make new pathfinder
     * @param state the escape state object
     */
    public EscapePathfinder(EscapeState state) {
        this.state = state;  // set state
        this.currentPlace = state.getCurrentNode();  // where we are
        this.exitPlace = state.getExit();  // where to go
    }

    /**
     * Main escape function to call
     */
    public void escape() {
        if (currentPlace.equals(exitPlace)) return;  // already there

        List<Node> wayOut = findWay();  // find way
        goThatWay(wayOut);
    }

    /**
     * Find the way out
     * @return the path out
     */
    private List<Node> findWay() {
        Map<Node, Node> cameFrom = new HashMap<>();  // where came from
        Queue<Node> toCheck = new LinkedList<>();  // places to look

        toCheck.add(currentPlace);  // start here
        cameFrom.put(currentPlace, null);  // came from nowhere

        while (!toCheck.isEmpty()) {  // keep looking loop
            Node here = toCheck.poll();
            if (here.equals(exitPlace)) break;  // found exit

            for (Node near : here.getNeighbours()) {
                if (!cameFrom.containsKey(near)) {
                    cameFrom.put(near, here);
                    toCheck.add(near);
                }
            }
        }

        return makePath(cameFrom);  // return path
    }

    /**
     * Make path from cameFrom map
     * @param cameFrom the map itself
     * @return the path
     */
    private List<Node> makePath(Map<Node, Node> cameFrom) {
        LinkedList<Node> path = new LinkedList<>();
        Node here = exitPlace;

        while (here != null) {  // go backwards
            path.addFirst(here);  // add to front
            here = cameFrom.get(here);  // get prev
        }

        return path;  // return it
    }

    /**
     * Follow the path
     * @param path the way to go
     */
    private void goThatWay(List<Node> path) {
        for (int i = 1; i < path.size(); i++) {  // skip first
            Node next = path.get(i);  // get next
            state.moveTo(next);  // go there
            grabGold();  // take gold
            if (state.getTimeRemaining() <= 0) break;  // ran out out time
        }
    }

    /**
     * Take the gold if here
     */
    private void grabGold() {
        if (state.getCurrentNode().getTile().getGold() > 0) {  // gold here?
            state.pickUpGold();  // take it
        }
    }
}