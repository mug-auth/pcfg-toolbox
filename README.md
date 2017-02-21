# A Probabilistic Context-Free Grammar (PCFG) toolbox for Java

This toolbox contains classes to represent PCFGs and two parsers: a
simple parser based on dynamic programming that requires the PCFG to
be in Chomsky Normal Form (CNF), and the Earley parser.

The toolbox includes various utilities, including automatically
transforming a PCFG to CNF (the algorithm's principle is presented in
pcfg_cnf.pdf), saving and restoring grammars in human-readable format
and in a custom format, generating parse trees, computing
probabilities of sub-trees (either by multiplication or addition), and
generating [Graphviz
DOT](http://www.graphviz.org/content/dot-language) code to visualize
the trees.

The Javadoc is available at http://vader.ee.auth.gr/pcfg/javadoc/.

Help to use the toolbox with MATLAB is available at
http://vader.ee.auth.gr/pcfg/matlabdoc/.
