package mayday.graphviewer.TimeSeriesBitmap;

import java.util.List;

public class SubstringMap 
{
	private int[][] values; 
	private int length;
	private int min=Integer.MAX_VALUE;
	private int max=0;
	private int nf;

	public SubstringMap(List<Character> alphabet, int l) 
	{
		values=new int[pow2(l)][];
		for(int i=0; i!=values.length; ++i)
		{
			values[i]=new int[pow2(l)];
		}		
		length=l;
		nf=pow2(l)*pow2(l);
	}

	public void clear()
	{
		values=new int[pow2(length)][];
		for(int i=0; i!=values.length; ++i)
		{
			values[i]=new int[pow2(length)];
		}
		min=Integer.MAX_VALUE;
		max=0;
		nf=pow2(length)*pow2(length);
	}

	private void addSubstring(String s)
	{
		addSubstring(s, 1);
	}

	public void addSubstrings(String s)
	{
		for(int i=0; i!=(s.length()-length+1); ++i)
		{
			addSubstring(s.substring(i, i+length));
		}
	}
	
	private int col(String s)
	{
		int col=0;
		for(int n=0; n!=length; ++n)
		{			
			col += ( (s.charAt(n)-'a') * pow2(length-n-1) )% (pow2(length-n));
		}
		return col;
	}
	
	private int row(String s)
	{
		int row=0;
		for(int n=0; n!=length; ++n)
		{
			row += ( (s.charAt(n)-'a') / 2 )* (pow2(length-n-1));
		}
		return row;
	}

	private void addSubstring(String s, int num)
	{
		if(s.isEmpty())
			return;
		int c=col(s);
		int r=row(s);	
		if(values[r][c]==0)
			nf-=1;
		values[r][c]+=num;
		if(values[r][c] < min)
			min=values[r][c];
		if(values[r][c] > max)
			max=values[r][c];
		
		
	}

	public void addSubstrings(String s, int num)
	{
		for(int i=0; i!=s.length()-length+1; ++i)
		{
			addSubstring(s.substring(i, i+length), num);
		}
	}
	
	public int mapSize()
	{
		return values.length;
	}

	public int getCount(int r, int c)
	{
		return values[r][c];
	}
	
	public double getScaledCount(int r, int c)
	{
		return (values[r][c] - getMin()) / (1.0* max);
	}

	public void addSaxEncodedString(SaxEncoding enc)
	{
		for(int i=0; i!=enc.getPointers().size(); ++i)
		{
			if(i!=enc.getPointers().size()-1)
			{
				int curr=enc.getPointers().get(i);
				int next=enc.getPointers().get(i+1);
				addSubstrings(enc.getSymbolicData().get(i), next-curr); // add each string as many times as it is repeated.
			}else
			{
				addSubstrings(enc.getSymbolicData().get(i)); // add each string as many times as it is repeated.
			}
		}
	}
	
	private static int pow2(int i)
	{
		if(i==0)
			return 1;
		return 2 << i-1;
		
	}
	
	public int getMax() {
		return max;
	}
	
	public int getMin() {
		if(min==Integer.MAX_VALUE)
			return 0;
		if(nf!=0)
			return 0;
		return min;
	}
	
}

