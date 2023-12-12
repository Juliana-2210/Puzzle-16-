import java.util.*;

public class Puzzle16 {

    private static final int SIZE = 4;
    private static final int EMPTY_TILE = 0;

    private List<Integer> puzzle;
    private List<Integer> goalState;

    public Puzzle16(List<Integer> puzzle) {
        if (puzzle.size() != SIZE * SIZE) {
            throw new IllegalArgumentException("El tamaño del puzzle no es válido");
        }
        this.puzzle = new ArrayList<>(puzzle);
        this.goalState = generateGoalState();
    }

    private List<Integer> generateGoalState() {
        List<Integer> goal = new ArrayList<>();
        for (int i = 1; i < SIZE * SIZE; i++) {
            goal.add(i);
        }
        goal.add(EMPTY_TILE);
        return goal;
    }

    private boolean isSolved(List<Integer> state) {
        return state.equals(goalState);
    }

    private List<Integer> swapTiles(List<Integer> state, int i, int j) {
        List<Integer> newState = new ArrayList<>(state);
        Collections.swap(newState, i, j);
        return newState;
    }

    private int calculateManhattanDistance(int value, int currentIndex) {
        if (value == EMPTY_TILE) return 0;

        int goalIndex = goalState.indexOf(value);
        int rowDiff = Math.abs(currentIndex / SIZE - goalIndex / SIZE);
        int colDiff = Math.abs(currentIndex % SIZE - goalIndex % SIZE);

        return rowDiff + colDiff;
    }

    private int calculateTotalManhattanDistance(List<Integer> state) {
        int totalDistance = 0;
        for (int i = 0; i < SIZE * SIZE; i++) {
            int value = state.get(i);
            totalDistance += calculateManhattanDistance(value, i);
        }
        return totalDistance;
    }

    private List<Integer> getNeighbors(int index) {
        List<Integer> neighbors = new ArrayList<>();
        int row = index / SIZE;
        int col = index % SIZE;

        if (col > 0) neighbors.add(index - 1); // Izquierda
        if (col < SIZE - 1) neighbors.add(index + 1); // Derecha
        if (row > 0) neighbors.add(index - SIZE); // Arriba
        if (row < SIZE - 1) neighbors.add(index + SIZE); // Abajo

        return neighbors;
    }

    private static class Node {
        List<Integer> state;
        int cost;

        public Node(List<Integer> state, int cost) {
            this.state = state;
            this.cost = cost;
        }
    }

    private void printPuzzle(List<Integer> state) {
        for (int i = 0; i < SIZE * SIZE; i += SIZE) {
            for (int j = i; j < i + SIZE; j++) {
                System.out.print(state.get(j) + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void solvePuzzle() {
    	PriorityQueue<Node> openSet = new PriorityQueue<>(new Comparator<Node>() {
    	    @Override
    	    public int compare(Node node1, Node node2) {
    	        return Integer.compare(node1.cost, node2.cost);
    	    }
    	});

        Set<List<Integer>> closedSet = new HashSet<>();
        Map<List<Integer>, Integer> costMap = new HashMap<>();
        Map<List<Integer>, List<Integer>> parentMap = new HashMap<>();

        openSet.offer(new Node(puzzle, calculateTotalManhattanDistance(puzzle)));
        costMap.put(puzzle, 0);
        parentMap.put(puzzle, null);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            List<Integer> currentState = currentNode.state;

            if (isSolved(currentState)) {
                System.out.println("Se encontró una solución:");
                reconstructPath(parentMap, currentState);
                return;
            }

            closedSet.add(currentState);

            int emptyIndex = currentState.indexOf(EMPTY_TILE);
            List<Integer> neighbors = getNeighbors(emptyIndex);

            for (int neighborIndex : neighbors) {
                List<Integer> newState = swapTiles(currentState, emptyIndex, neighborIndex);

                if (!closedSet.contains(newState)) {
                    int newCost = costMap.get(currentState) + 1;
                    if (!costMap.containsKey(newState) || newCost < costMap.get(newState)) {
                        costMap.put(newState, newCost);
                        int priority = newCost + calculateTotalManhattanDistance(newState);
                        openSet.offer(new Node(newState, priority));
                        parentMap.put(newState, currentState);
                    }
                }
            }
        }

        System.out.println("No se encontró una solución.");
    }

    private void reconstructPath(Map<List<Integer>, List<Integer>> parentMap, List<Integer> state) {
        List<List<Integer>> path = new ArrayList<>();
        while (state != null) {
            path.add(state);
            state = parentMap.get(state);
        }

        for (int i = path.size() - 1; i >= 0; i--) {
            System.out.println("Paso " + (path.size() - 1 - i) + ":");
            printPuzzle(path.get(i));
        }
    }

    public static void main(String[] args) {
        List<Integer> initialState = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 13, 10, 14, 15, 12);
        Puzzle16 puzzleSolver = new Puzzle16(initialState);
        System.out.println("Estado Inicial:");
        puzzleSolver.printPuzzle(initialState);
        puzzleSolver.solvePuzzle();
    }
}

