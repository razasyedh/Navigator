# Navigator - A Wavefront Pathfinder
## Overview

Navigator is a pathfinding app that uses the Wavefront algorithm to find a path between two points on a 2D grid while taking obstacles into account.

The app consists of two parts, a pathfinding library and a GUI.

**Library**:

* Node.java - A node in the grid that knows it's neighbors.
* Point2D.java - A 2D coordinate of a point on the grid.
* LinkedGrid.java - A grid of nodes.
* PathFinder.java - The implementation of the Wavefront algorithm that utilizes the linked grid.

**GUI**

* Navigator.java - The main code for the GUI.
* GridCanvas.java - The code for drawing the grid.

## Usage
1. Create a LinkedGrid of size x by y.
2. Block any nodes by setting them to `LinkedGrid.BLOCKED`.
3. Use the grid and endpoints to create a PathFinder object and get a path.

## Building
To compile both packages, from the `src` directory, run the following in a terminal:

    javac com/syedraza/WaveFront/PathFinder.java
    javac com/syedraza/Navigator/Navigator.java

To create a JAR file for WaveFront:

    jar cf com/syedraza/WaveFront/*class

To create a JAR file for the Navigator:

    jar cfm "Navigator.jar" Manifest.txt com/syedraza/*/*class
