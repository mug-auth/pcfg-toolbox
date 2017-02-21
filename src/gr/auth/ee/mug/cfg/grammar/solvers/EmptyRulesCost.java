package gr.auth.ee.mug.cfg.grammar.solvers;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * This class implements the cost function that is minimized in the
 * least-squares problem that is solved to compute the probability of erasing
 * for erasable non-terminals. This is action is part of the function that
 * transforms a context-free grammar to Chomsky nomral form.
 * 
 * @author Vasileios Papapanagiotou
 */
public class EmptyRulesCost implements MultivariateVectorFunction {

	private final int n;
	private final RealMatrix[] A;
	private final RealMatrix B;
	private final RealVector C;

	public EmptyRulesCost(int n, double[][][] A, double[][] B, double[] C) {
		this.n = n;
		this.A = new RealMatrix[n];
		for (int i = 0; i < n; i++) {
			this.A[i] = MatrixUtils.createRealMatrix(A[i]);
		}
		this.B = MatrixUtils.createRealMatrix(B);
		this.C = MatrixUtils.createRealVector(C);
	}

	@Override
	public double[] value(double[] arg0) throws IllegalArgumentException {

		// Convert to RealVector
		RealVector x = MatrixUtils.createRealVector(arg0);

		// v = B x + C
		double[] v = B.operate(x).add(C).toArray();

		// Add quadratic terms
		for (int i = 0; i < n; i++) {
			v[i] += A[i].preMultiply(x).dotProduct(x);
		}

		return v;
	}

}
