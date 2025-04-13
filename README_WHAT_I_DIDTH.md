# Software and Programming III

## Group Coursework

#### Author: Afra Melia Daghmoumi

Explore Phase
========
- At first I tried to use a simple DFS algorithm. The solution was very slow and although it always found the orb it always explore the every surrounding tile until orb was found
- I then tried a naive BFS algorithm with much better results

Escape Phase
========
- I simply repurposed the BFS aglorithm used in my explore phase to escape. There is no real consideration to see how to maximise amount of gold retrieved. I simply `pickUpGold` when passing through the escape


Group Contributions
========
`Vgurus01 ` provided the unit tests in her code base.

Limitations:
========
- Unit testing not merged / coordinated with other group members
- Could have refactored the BFS code with an optional parameter for start and end node (entrance + orb for explore and orb + exit for escape) with optional parameter for pickupgold
