package test.student;

import student.Explorer;

// A basic test class to test the Explorer class
public class ExplorerTest {

    class SimpleExplorationState implements game.ExplorationState {
        private long currentLocation = 1;  // Start at node 1
        private final long orbLocation = 3; // Orb is at node 3

        @Override
        public long getCurrentLocation() {
            return currentLocation;
        }

        @Override
        public void moveTo(long id) {
            currentLocation = id;
        }

        @Override
        public int getDistanceToTarget() {
            if (currentLocation == orbLocation) {
                return 0;
            } else if (currentLocation == 2) {
                return 1;
            } else {
                return 2;
            }
        }

        @Override
        public java.util.Collection<game.NodeStatus> getNeighbours() {
            if (currentLocation == 1) {
                return java.util.List.of(
                        new game.NodeStatus(2L, 1),
                        new game.NodeStatus(3L, 0) // Orb is here
                );
            }
            return java.util.List.of(); // no neighbors elsewhere
        }

        @Override
        public void moveTo(Object id) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'moveTo'");
        }
    }

    @Test
    public void testExplorerFindsOrb() {
        SimpleExplorationState fakeState = new SimpleExplorationState();
        Explorer explorer = new Explorer();

        explorer.explore(fakeState);

        assertEquals(0, fakeState.getDistanceToTarget(), "Explorer should reach the orb.");
    }

    private void assertEquals(int i, int distanceToTarget, String string) {
        throw new UnsupportedOperationException("Unimplemented method 'assertEquals'");
    }
}
