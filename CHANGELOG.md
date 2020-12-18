# Release 1.5
- Known bugs fixed

# Release 1.4
- Welcome screen has been extended with further information
- New menu entry „Functions“: this entry is added if the user opens an algorithm and provides the main functions of the toolbar extensions that the plugin contains
- Dijkstra, Eulerian Cycle, and Triple Algorithm Plugin: new toolbar extension to create randomly generated graphs
- Known Bugs fixed

# Release 1.3
- Logistics package plugins updated to version 1.3
- Relabel-to-front algorithm plugin (version 1.0) added
- Tripel algorithm plugin:
  - Changed assumptions meaning that now it is checked whether the graph contains circles of negative length
  - New toolbar extension to layout the vertices in a circle
- Dijkstra algorithm plugin: new toolbar extension to layout the vertices in a circle
- Pseudocode update of all algorithms
- Release date of the current version added to About dialog
- Step backward and forward is now made accessible to arrow keys (left + right) in combination with ALT
- Known bugs fixed

# Release 1.2
__INFO: the minimum version has increased to 1.2 meaning that old plugins have to be updated to the new SDK version to be executable in the new LAVES version__
- Base package plugins updated to version 1.2
- Display error fixed in Vogel’s Approximation Method plugin
- Bug fixed that crashes the 2-opt algorithm plugin in exercise mode
- First exercise in 2-opt plugin exercise mode now expects input for both columns (out and in)
- Due to the improvments in the Network class the Ford-Fulkerson algorithm plugin is now updated and supports the correct definition of residual networks
- Updated language files for host system and Vogel’s Approximation Method plugin
- Bug fixed that crashes LAVES when plugins aren’t compatible with the current SDK
- New configuration options in the preferences dialog of LAVES
- Updated LAVES help files

# Release 1.1
- New information dialog that contains information about the exercise mode (can be disabled by the user)
- Updated help files
- Dijkstra's algorithm plugin, Tripel algorithm plugin: extended by final exercise to verify whether the user can apply the algorithm results meaning the user has to specify a shortest path between two random vertices
- Integration of input hints in several plugins to simplify the work with the exercises