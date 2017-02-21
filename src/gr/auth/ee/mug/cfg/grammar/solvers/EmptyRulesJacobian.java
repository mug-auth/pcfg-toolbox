package gr.auth.ee.mug.cfg.grammar.solvers;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * This class implements the Jacobian of the cost function that is minimized in
 * the least-squares problem that is solved to compute the probability of
 * erasing for erasable non-terminals. This is action is part of the function
 * that transforms a context-free grammar to Chomsky nomral form.
 * 
 * @author Vasileios Papapanagiotou
 */
public class EmptyRulesJacobian implements MultivariateMatrixFunction {

	private final int n;
	private final RealMatrix[] A;
	private final RealMatrix B;

	public EmptyRulesJacobian(int n, double[][][] A, double[][] B, double[] C) {
		this.n = n;
		this.A = new RealMatrix[n];
		for (int i = 0; i < n; i++) {
			RealMatrix t = MatrixUtils.createRealMatrix(A[i]);
			this.A[i] = t.add(t.transpose());
		}
		this.B = MatrixUtils.createRealMatrix(B);
	}

	@Override
	public double[][] value(double[] arg0) throws IllegalArgumentException {

		// Convert to RealVector
		RealVector x = MatrixUtils.createRealVector(arg0);

		//
		RealMatrix v = MatrixUtils.createRealMatrix(n, n);
		for (int i = 0; i < n; i++) {
			v.setRowVector(i, A[i].preMultiply(x));
		}

		v = v.add(B);

		return v.getData();
	}

}
