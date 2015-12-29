package mayday.graphviewer.crossViz3.probes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.graphviewer.core.SummaryOption;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;

public class ProbeUnit implements IProbeUnit, Comparable<IProbeUnit>
{
	private List<Probe> probes;
	private String name;

	
	
	public ProbeUnit(List<Probe> probes, String name) 
	{
		super();
		this.probes = probes;
		this.name = name;
	}

	@Override
	public void addProbe(Probe p) 
	{
		probes.add(p);
	}

	@Override
	public double[] getCollapsedValues(IExperimentMapping mapping) 
	{
		double[] values=new double[mapping.getNumberOfExperiments()];

		for(int i=0; i!= mapping.getNumberOfExperiments(); ++i)
		{

			for(Probe p:probes)
			{
				if(p==null )
				{
					continue;
				}
				int exp=mapping.getLocalExperiment(p.getMasterTable().getDataSet(), i);
				if(exp >= 0)
				{
					values[i] =p.getValues()[exp];
					break;
				}else
				{
					values[i] =Double.NaN;
				}

			}
		}
		return values;
	}

	@Override
	public Set<DataSet> getDataSets() 
	{
		Set<DataSet> res=new HashSet<DataSet>();
		for(Probe p: probes)
			res.add(p.getMasterTable().getDataSet());
		return res;
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public int getNumberOfDataSets() 
	{
		return getDataSets().size();
	}

	@Override
	public int getNumberOfProbes() 
	{
		return probes.size();
	}

	@Override
	public List<Probe> getProbes() 
	{
		return probes;
	}

	@Override
	public List<double[]> getValues(IExperimentMapping mapping) 
	{
//		List<double[]> values=new ArrayList<double[]>();
//		for(int i=0; i!=probes.size(); ++i)
//		{
//			values.add(new double[mapping.getNumberOfExperiments()]);
//		}
//		for(int i=0; i!= mapping.getNumberOfExperiments(); ++i)
//		{
//			int j=0;
//			for(Probe p:probes)
//			{
//				
//				if(values.get(j)==null || p==null)
//					continue;
//				int exp=mapping.getLocalExperiment(p.getMasterTable().getDataSet(), i);
//				if(exp <0 )
//				{
//					values.get(j)[i]=Double.NaN;
//				}else
//				{					
//					values.get(j)[i]=p.getValue(exp);
//				}
//				j++;
//			}
//		}
		List<double[]> values=new ArrayList<double[]>();
		for(Probe p:probes)
		{
			if(p==null)
			{
				continue;
			}
			double[] vals=new double[mapping.getNumberOfExperiments()];
			for(int i=0; i!= mapping.getNumberOfExperiments(); ++i)
			{
				int exp=mapping.getLocalExperiment(p.getMasterTable().getDataSet(), i);
				if(exp < 0 ) 
					vals[i]=Double.NaN;
				else
				{	
					vals[i]= p.getValues()[exp];
				}
			}
			values.add(vals);
		}
		return values;
	}

	@Override
	public boolean isCollapsable() 
	{
		Set<DataSet> ds=new TreeSet<DataSet>();
		for(Probe p:probes)
		{
			if(p==null) continue;
			if(ds.contains(p.getMasterTable().getDataSet()))
			{
				return false;
			}
			ds.add(p.getMasterTable().getDataSet());
		}
		return true;
	}

	@Override
	public void setName(String name) 
	{
		this.name=name;
	}

	@Override
	public double[] summarize(SummaryOption summary, IExperimentMapping mapping) 
	{
		List<double[]> val=getValues(mapping);
		double[] res=new double[mapping.getNumberOfExperiments()];


		for(int i=0; i!= mapping.getNumberOfExperiments(); ++i)
		{
			DoubleVector v=new DoubleVector(val.size());
			for(int j=0; j!= val.size(); ++j)
			{
				v.set(j, val.get(j)[i]);
			}
			switch (summary) 
			{
			case MEAN:
				res[i]=v.mean();
				break;
			case MEDIAN:
				res[i]=v.median();
				break;
			default:
				break;
			}
		}
		return res;
	}

	@Override
	public int compareTo(IProbeUnit o) 
	{
		return name.compareTo(o.getName());
	}
	
	@Override
	public Probe getProbeForDataset(DataSet ds) 
	{
		for(Probe p: probes)
			if(p.getMasterTable().getDataSet()==ds)
				return p;
		return null;
	}



	//	/**
	//	 * @param mapping
	//	 * @return a double matrix: [numProbes][mapping.numExp]
	//	 */
	//	public double[][] getUnitValues(ExperimentMapping mapping)
	//	{
	//		int numRows=(mapping.getMaxNumberOfExperiments()==1)?1:getNumberOfProbes();//mapping.getDataSets().size();
	//		double[][] values=new double[numRows][mapping.getNumberOfExperiments()];
	//		
	//		if(mapping.getMaxNumberOfExperiments()==1)
	//		{
	//			for(int i=0; i!= mapping.getNumberOfExperiments(); ++i)
	//			{
	//				
	//				for(Probe p:probes)
	//				{
	//					if(p==null )
	//					{
	//						continue;
	//					}
	//					int exp=mapping.getLocalExperiment(p.getMasterTable().getDataSet(), i);
	//					if(exp >= 0)
	//					{
	//						values[0][i] =p.getValue(exp);
	//						break;
	//					}else
	//					{
	//						values[0][i] =Double.NaN;
	//					}
	//									
	//				}
	//			}
	//		}else
	//		{
	//			for(int i=0; i!= mapping.getNumberOfExperiments(); ++i)
	//			{
	//				int j=0;
	//				for(Probe p:probes)
	//				{
	//					if(p==null) 
	//					{
	//						continue;
	//					}
	//					int exp=mapping.getLocalExperiment(p.getMasterTable().getDataSet(), i);
	//					if(exp <0 )
	//					{
	//						values[j][i]=Double.NaN;
	//					}else
	//					{
	//						values[j][i]=p.getValue(exp);
	//					}
	//					j++;
	//				}
	//			}
	//		}
	//		return values;
	//	}

	//	public String getName() 
	//	{
	//		return name;
	//	}
	//	
	//	public void setName(String name) 
	//	{
	//		this.name = name;
	//	}


}
