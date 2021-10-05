# ReebGraphPairing
## A tool for pairing critical points coming from Reeb graphs and contour trees


### Citation
If you use this code, please consider citing:

**Propagate and pair: A single-pass approach to critical point pairing in reeb graphs** \
International Symposium on Visual Computing. Springer, Cham, 2019 \
Junyi Tu, Mustafa Hajij, and Paul Rosen


### Running to Program

Make sure you have a relatviely recent version of Java installed.

From the command line, go to ReebGraphPairing/build

#### For the Merge Pairing Approach
> java -jar ReebGraphPairingMP.jar &lt;file1&gt; &lt;file2&gt; ... &lt;fileN&gt;

#### For the Pair and Propagate Approach
> java -jar ReebGraphPairingPPP.jar &lt;file1&gt; &lt;file2&gt; ... &lt;fileN&gt;

Both should provide the identical results, though Pair and Propagate should be faster in general.


### Input Format

Example input Reeb Graphs are provided in the test directory.


### Output Format

Output should be compatible with CSV format.


### For questions

Contact: Paul Rosen <prosen@usf.edu>

