package mayday.graphviewer.statistics;

import java.util.HashSet;
import java.util.Set;

import mayday.core.math.Statistics;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public final class Correlations 
{
	/**
	 * Calculates pearson's product-moment correlation coefficient. x and y need to have the same length. 
	 * @param x
	 * @param y
	 * @return pearson's product-moment correlation coefficient between x and y.
	 */
	public static double cor(double[] x, double[] y)
	{
		if (x.length != y.length) 
		{
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}

		int dim = x.length;
		double S_xy = 0.0;
		double S_x  = 0.0;
		double S_y  = 0.0;

		double mean_vec1 = Statistics.mean(x);
		double mean_vec2 = Statistics.mean(y);

		for (int i = 0; i < dim; i++) 
		{
			S_xy +=  (x[i]-mean_vec1) * (y[i]-mean_vec2);
			S_x  +=  (x[i]-mean_vec1) * (x[i]-mean_vec1);
			S_y  +=  (y[i]-mean_vec2) * (y[i]-mean_vec2);
		}
		return S_xy / (Math.sqrt(S_x) * Math.sqrt(S_y));
	}
	
	/**
	 * Calculates the partial correlation of x and y, given u
	 * @param x
	 * @param y
	 * @param u
	 * @return partial correlation of x and y, given u.
	 */
	public static double partialCor(double[] x, double[] y, double[] u)
	{
		if (x.length != y.length || x.length != u.length) 
		{
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}
		double rxy=cor(x,y);
		double rxu=cor(x,u);
		double ryu=cor(y,u);
		
		return (rxy- rxu*ryu) / Math.sqrt( (1-rxu*rxu)*(1-ryu*ryu) );		
	}
	
	public static double multipleCor(double[] x1, double[] x2, double[] y)
	{
		if (x1.length != y.length || x2.length != y.length) 
		{
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}
		double rx1y=cor(x1,y);
		double rx2y=cor(x2,y);
		double rx1x2=cor(x1,x2);
		
		double u= (rx1y*rx1y) + (rx2y*rx2y) - (2.0 *rx1x2 * rx1y *rx2y);
		double v= 1 - (rx1x2*rx1x2);
		
		return Math.sqrt(u/v);		
	}
	
	/**
	 * Calculates the covariance between x and y. x and y need to have the same length. 
	 * @param x
	 * @param y
	 * @return the covariance between x and y. 
	 */
	public static double cov(double[] x, double[] y) 
	{
		if (x.length != y.length) 
		{
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}
		
		int dim = x.length;
		double Covariance = 0.0;
		double mean_vec1 = Statistics.mean(x);
		double mean_vec2 = Statistics.mean(y);
		
		for (int i = 0; i!=dim; i++) 
		{
			Covariance +=  (x[i]-mean_vec1) * (y[i]-mean_vec2);
		}		
		Covariance /= dim - 1;
		return Covariance;		
	}
	
	public static double spearman(double[] x, double[] y)
	{
		if (x.length != y.length) 
		{
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}
		int dim = x.length;
		int[] rx=Statistics.rank(x);
		int[] ry=Statistics.rank(y);
		
		double[] rxd=new double[dim];
		double[] ryd=new double[dim];
		for(int i=0; i!= dim; ++i)
		{
			rxd[i]=rx[i];
			ryd[i]=ry[i];
		}	
		return cor(rxd,ryd);
	}
	
	public static double spearmanSimple(double[] x, double[] y)
	{
		if (x.length != y.length) 
		{
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}
		int dim = x.length;
		
		int[] rx=Statistics.rank(x);
		int[] ry=Statistics.rank(y);
		int d=0;
		for(int i=0; i!=dim; ++i)
		{
			d+=(rx[i]-ry[i])*(rx[i]-ry[i]);
		}
		return 1 - (6.0*d)/(dim*((dim*dim)-1.0));		
	}
	
	public static int numTies(int[] ranks)
	{
		Set<Integer> s=new HashSet<Integer>();
		for(int i:ranks)
			s.add(i);
		return ranks.length-s.size();
	}
	
	/**
	 * @param ranks
	 * @return
	 */
	public static int[] ties(int[] ranks)
	{
		int[] ties=new int[ranks.length];
		for(int i:ranks)
			ties[i-1]=ties[i-1]+1;
		return ties;
	}
	
	/**
	 * Calculates Kendall's tau without adjustment for ties. 
	 * @param x 
	 * @param y
	 * @return Kendall's tau without adjustment for ties. 
	 */
	public static double kendall(double[] x, double[] y)
	{
		if (x.length != y.length) 
		{
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}
		int n = x.length;
		
		int nmr=0;
		for(int i=0; i!= x.length; ++i)
			for(int j=0; j!= i; ++j)
				nmr +=  Math.signum(x[i]-x[j])*Math.signum(y[i]-y[j]);
		
		return nmr / (0.5*n*(n-1.0)); 
	}
		
	/**
	 * Calculates the inner product correlation of the two matrices. The matrices need to be of the same size
	 * @param X , n*m matrix
	 * @param Y , n*m matrix
	 * @return the inner product correlation of the two matrices.
	 */
	public static double innerProductCorrelation(DoubleMatrix X, DoubleMatrix Y)
	{
		if(X.ncol()!=Y.ncol() || X.nrow()!=Y.nrow())
			throw new IllegalArgumentException("The matrices must have the same dimension");
		
		PermutableMatrix Xt = X.staticShallowClone();
		Xt.transpose();
		PermutableMatrix Yt = Y.staticShallowClone();
		Yt.transpose();
		
		double a= trace(Algebra.multiply(Xt, Y));
		double b= trace(Algebra.multiply(Xt, X))  *  trace(Algebra.multiply(Yt, Y));
		
		return a / Math.sqrt(b);		
	}
	
	/**
	 * Calculates the RV value of the two matrices, which need to have the same number of rows.
	 * @param X
	 * @param Y
	 * @return the RV value of the two matrices
	 */
	public static double rv(DoubleMatrix X, DoubleMatrix Y)
	{
		if(X.nrow()!=Y.nrow())
			throw new IllegalArgumentException("The matrices must have the same number of rows");
		
		PermutableMatrix Xt = X.deepClone();
		
		Xt.transpose();
		System.out.println(X.nrow()+"\t"+ X.ncol()+"\t"+Xt.nrow()+"\t"+ Xt.ncol());
		PermutableMatrix Yt = Y.deepClone();
		Yt.transpose();
		System.out.println(Y.nrow()+"\t"+ Y.ncol()+"\t"+Yt.nrow()+"\t"+ Yt.ncol());
		DoubleMatrix XXt = Algebra.multiply(X, Xt);
		DoubleMatrix YYt = Algebra.multiply(Y, Yt);
		
		double a= trace(Algebra.multiply(XXt, YYt));
		double b= trace(Algebra.multiply(XXt, XXt))  *  trace(Algebra.multiply(YYt, YYt));
		
		return a / Math.sqrt(b);	
	}
	
	/**
	 * Calculates the modified RV value of the two matrices, which need to have the same number of rows.
	 * @param X
	 * @param Y
	 * @return the RV value of the two matrices
	 */	
	public static double rv2(DoubleMatrix X, DoubleMatrix Y)
	{
		if(X.nrow()!=Y.nrow())
			throw new IllegalArgumentException("The matrices must have the same number of rows");
		
		PermutableMatrix Xt = X.staticShallowClone();
		Xt.transpose();
		PermutableMatrix Yt = Y.staticShallowClone();
		Yt.transpose();
		
		DoubleMatrix XXt = Algebra.multiply(X, Xt);
		DoubleMatrix YYt = Algebra.multiply(Y, Yt);
		
		for(int i=0; i!= XXt.nrow();++i)
		{
			XXt.setValue(i, i, 0);
		}
		for(int i=0; i!= YYt.nrow();++i)
		{
			YYt.setValue(i, i, 0);
		}
		AbstractVector vX=vec(XXt);
		AbstractVector vY=vec(YYt);
		
		double a=Algebra.scalarProduct(vX, vY);				
		double b= Algebra.scalarProduct(vX, vX)*Algebra.scalarProduct(vY, vY);
		
		return a / Math.sqrt(b);	
	}
		
	public static double trace(AbstractMatrix X)
	{
		return X.getDiagonal().sum();
	}
	
	public static AbstractVector vec(AbstractMatrix X)
	{		
		AbstractVector res=new DoubleVector(X.nrow()*X.ncol());
		int c=0; 
		for(int j=0; j!=X.ncol(); ++j)
			for(int i=0; i!=X.nrow(); ++i)
			{
				res.set(c, X.getValue(i,j));
				c++;
			}
		return res;
	}


}
