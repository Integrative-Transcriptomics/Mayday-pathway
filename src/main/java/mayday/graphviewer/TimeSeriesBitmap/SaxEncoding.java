package mayday.graphviewer.TimeSeriesBitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mayday.core.math.Statistics;
import mayday.graphviewer.core.Utilities;

public class SaxEncoding 
{
	private List<String> symbolicData;
	private List<Integer> pointers;

	private List<Double> cutoffs;
	private int alphabetSize;

	public SaxEncoding(int alphabetSize) {
		this.alphabetSize=alphabetSize;
		generateCutoffs();
	}

	/**
	 * @param data The time series data to be encoded
	 * @param N the length of the sliding window
	 * @param n the number of symbols per window in the encoded sequence
	 * @return true
	 */
	public boolean encodeTimeSeries(List<Double> data, int N, int n)
	{


		//		int winSize=(int) Math.floor( (1.0*N)/(1.0*n));

		if(N > data.size())
			N=data.size();
		
		pointers=new ArrayList<Integer>();
		symbolicData=new ArrayList<String>();

		String last="";
		for(int i=0; i!= data.size()-(N-1); ++i)
		{
			List<Double> subSection= new ArrayList<Double>( data.subList(i, i+N));
//			System.out.println(">"+subSection.toString());
			zScoreNormalize(subSection);
//			System.out.println(subSection.toString());
			List<Double> PAA;
			if(N==n)
				PAA=subSection;

			PAA= windowedMean(subSection, n);

//			System.out.println("PAA:"+PAA.toString());
			String current=mapToString(PAA);
			
			if(!current.equals(last))
			{
				symbolicData.add(current);
				pointers.add(i);
//				System.out.println(current);
			}
			last=current;
		}


		return true;
	}

	private String mapToString(List<Double> PAA)
	{
		StringBuffer sb=new StringBuffer();
		for(double d: PAA)
		{
			int i=Collections.binarySearch(cutoffs, d);
			char c;
			if(i>=0)
			{
				c=(char)('a'+i-1);
			}
			else
				c=(char) ('a'-i-2);
			sb.append(c);

		}
		return sb.toString();
	}
	
	

	/**
	 * In-place zscore normalize the data.
	 * @param data
	 */
	private void zScoreNormalize(List<Double> data)
	{
		double mean=Statistics.mean(data);
		double sd=Statistics.sd(data);
		for(int i=0; i!= data.size(); ++i)
		{
			data.set(i, (data.get(i)-mean)/sd);
		}

	}

	public static List<Double> windowedMean(List<Double> data, int n)
	{
		List<Double> res=new ArrayList<Double>(n);
		int N=data.size();
		double frac=1.0/(1.0*n);
		double sum=0;
		for(int i=0; i!=n*N; ++i)
		{
			int localIdx=i/n;

			double ds=data.get(localIdx)*n;
			double dsFrc=ds*frac;
			sum+=dsFrc;
			//System.out.println(i+": "+localIdx+" >>"+ds +" // "+dsFrc);
			if( (i+1) %N==0 && i!=0)
			{
				res.add(sum/(1.0*N));
				//System.out.println("Yield: "+ (sum/(1.0*N)));
				sum=0;
			}
		}

		return res;
	}

	private void generateCutoffs()
	{
		if(alphabetSize < 2)
			throw new IllegalStateException("Illegal alphabet size (must be >=2)");
		cutoffs=new ArrayList<Double>();
		cutoffs.add(Double.NEGATIVE_INFINITY);
		
		//Utilities.seriesOver(num, w)
		double step=1.0d/((double)alphabetSize);
		for(int i=1; i!=alphabetSize; ++i)
		{
			cutoffs.add(Utilities.qnorm(((double)i)*step));
		}		
	}
	
	public List<Integer> getPointers() {
		return pointers;
	}
	
	public List<String> getSymbolicData() {
		return symbolicData;
	}
	
//	private void createCutoffs()
//	{
//		double[] cutPoints;
//
//		switch (alphabetSize) {
//		case 2:
//			cutPoints=new double[]{Double.NEGATIVE_INFINITY, 0};
//			break;
//		case 3: 
//			cutPoints=new double[]{Double.NEGATIVE_INFINITY, -0.43, 0.43}; 
//			break;
//		case 4: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -0.67, 0, 0.67}; break;
//		case 5: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -0.84, -0.25, 0.25, 0.84};  break;
//		case 6: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -0.97, -0.43, 0, 0.43, 0.97};  break;
//		case 7: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.07, -0.57, -0.18, 0.18, 0.57, 1.07}; break;
//		case 8: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.15, -0.67, -0.32, 0, 0.32, 0.67, 1.15};  break;
//		case 9: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.22, -0.76, -0.43, -0.14, 0.14, 0.43, 0.76, 1.22};  break;
//		case 10: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.28, -0.84, -0.52, -0.25, 0, 0.25, 0.52, 0.84, 1.28};  break;
//		case 11: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.34, -0.91, -0.6, -0.35 -0.11, 0.11, 0.35, 0.6, 0.91, 1.34};  break;
//		case 12: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.38, -0.97, -0.67, -0.43, -0.21, 0, 0.21, 0.43, 0.67, 0.97, 1.38};  break;
//		case 13: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.43, -1.02, -0.74, -0.5, -0.29, -0.1, 0.1, 0.29, 0.5, 0.74, 1.02, 1.43};  break;
//		case 14: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.47, -1.07, -0.79, -0.57, -0.37, -0.18, 0, 0.18, 0.37, 0.57, 0.79, 1.07, 1.47};  break;
//		case 15: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.5, -1.11, -0.84, -0.62, -0.43, -0.25, -0.08, 0.08, 0.25, 0.43, 0.62, 0.84, 1.11, 1.5};  break;
//		case 16: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.53, -1.15, -0.89, -0.67, -0.49, -0.32, -0.16, 0, 0.16, 0.32, 0.49, 0.67, 0.89, 1.15, 1.53};  break;
//		case 17: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.56, -1.19, -0.93, -0.72, -0.54, -0.38, -0.22, -0.07, 0.07, 0.22, 0.38, 0.54, 0.72, 0.93, 1.19, 1.56};  break;
//		case 18: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.59, -1.22, -0.97, -0.76, -0.59, -0.43, -0.28, -0.14, 0, 0.14, 0.28, 0.43, 0.59, 0.76, 0.97, 1.22, 1.59}; break; 
//		case 19: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.62, -1.25, -1, -0.8, -0.63, -0.48, -0.34, -0.2, -0.07, 0.07, 0.2, 0.34, 0.48, 0.63, 0.8, 1, 1.25, 1.62}; break;
//		case 20: cutPoints=new double[]{Double.NEGATIVE_INFINITY, -1.64, -1.28, -1.04, -0.84, -0.67, -0.52, -0.39, -0.25, -0.13, 0, 0.13, 0.25, 0.39, 0.52, 0.67, 0.84, 1.04, 1.28, 1.64}; break;
//
//		default:
//			throw new IllegalStateException("Illegal Alphabet size");
//
//		}
//		cutoffs=new ArrayList<Double>();
//		for(double d: cutPoints)
//		{
//			cutoffs.add(d);
//		}
//
//		System.out.println(cutoffs);
//
//	}

	public static void main(String[] args) throws Exception {
		List<Double> v=new ArrayList<Double>();
		v.add(0d);
		v.add(-1.25);
		v.add(-1.5);
		v.add(0d);
		v.add(1.25);
		v.add(0.75);
		v.add(0.40);
		v.add(0.75);
		
		for(int i=2; i!= 20; ++i)
		{
		SaxEncoding enc=new SaxEncoding(i);
//		enc.encodeTimeSeries(v, 8, 8);
		
		enc.generateCutoffs();
		}
	}
	
	public List<Character> getAlphabet()
	{
		List<Character> res=new ArrayList<Character>();
		for(int i=0; i!= alphabetSize; ++i)
		{
			res.add(new Character(  (char)('a'+i) ));
		}
		return res;
	}
	
}
