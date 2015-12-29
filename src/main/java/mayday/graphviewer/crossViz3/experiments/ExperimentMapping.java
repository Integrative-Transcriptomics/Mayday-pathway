package mayday.graphviewer.crossViz3.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.meta.types.TimeseriesMIO;

/**
 * Class for a mapping of experiments. This works by keeping list of global experiments which 
 * are mapped to local experiments in one or more datasets. Experiments may be repeated, omitted, reordered. 
 * Names can be set for the global experiments. 
 * Example:
 * <pre>
 * global	ds	local exp in ds.
 * 1	DS1	1
 * 1	DS2	1
 * 2	DS1	2
 * 3	DS2	2
 * 4	DS1	1
 * 5	DS1	4
 * 5	DS2	3
 * 6	DS1	3
 * </pre>
 * @author Stephan Symons
 * @see mayday.core.DataSet
 */
public class ExperimentMapping implements IExperimentMapping
{
	/**
	 * Contains the mapping. 
	 */
	private Map<Integer, Map<DataSet,Integer>> mapping;
	
	private List<DataSet> dataSets;
	
	private Map<Integer,String> globalExperimentNames; 
	
	/**
	 * Creates a new empty experiment mapping that does not map anything. 
	 */
	public ExperimentMapping(List<DataSet> dataSets) 
	{
		mapping=new TreeMap<Integer, Map<DataSet,Integer>>();
		globalExperimentNames=new HashMap<Integer,String>();
		this.dataSets=dataSets;
	}
	
	/**
	 * Creates a new experiment mapping containing the mapping defined in mapping. 
	 */
	public ExperimentMapping(ExperimentMapping otherMapping) 
	{
		mapping=new HashMap<Integer, Map<DataSet,Integer>>(otherMapping.mapping);
		globalExperimentNames=new HashMap<Integer,String>(otherMapping.globalExperimentNames);
		this.dataSets=new ArrayList<DataSet>(otherMapping.getDataSets());
	}
	
	/**
	 * Add an experiment mapping: <br>
	 * <pre>
	 * global	ds	local exp in ds.
	 * g	ds	l
	 * </pre>
	 * @param ds The dataset with the local experiment
	 * @param localExperiment the index of the local experiment
	 * @param globalExperiment the global experiment
	 */
	public void map(DataSet ds, int localExperiment, int globalExperiment)
	{
		if(!mapping.containsKey(globalExperiment))
		{
			mapping.put(globalExperiment, new HashMap<DataSet, Integer>());
		}
		mapping.get(globalExperiment).put(ds, localExperiment);
	}
	
	public int addGlobalExperiment()
	{
		int globalExperiment=getNumberOfExperiments();
		if(!mapping.containsKey(globalExperiment))
		{
			mapping.put(globalExperiment, new HashMap<DataSet, Integer>());
		}
		return globalExperiment;
		
	}
	
	/**
	 * Set the name of a global experiment
	 * @param exp The index of that experiment
	 * @param name The name of that experiment
	 */
	public void setGlobalName(int exp, String name)
	{
		globalExperimentNames.put(exp, name);
	}
	
	/**
	 * Get the name of a global experiment.
	 * @param exp the experiment
	 * @return the global experiment name of the experiment
	 */
	public String getGlobalName(int exp)
	{
		return globalExperimentNames.get(exp);
	}
	
	/**
	 * @return the number of global experiments
	 */
	public int getNumberOfExperiments()
	{
		return mapping.size();
	}
	
	/**
	 * @return the number of global experiments
	 */
	public int getMaxNumberOfExperiments()
	{
		int res=0;
		for(Map<DataSet,Integer> m:mapping.values())
		{
			
			res=Math.max(res, m.size());
		}
		return res;
	}
		
	/**
	 * Get the local experiment index in the DataSet ds of the global experiment index globalExperiment
	 * @param ds The dataSet for which the local experiment equivalent is searched. 
	 * @param globalExperiment The global experiment to be matched to a local experiment
	 * @return The experiment index of the global experiment in dataset ds. 
	 * <b>Caution</b> If no such global experiment is available, this functiopn returns -1; 
	 */
	public int getLocalExperiment(DataSet ds, int globalExperiment)
	{
		if(!mapping.containsKey(globalExperiment) || !mapping.get(globalExperiment).containsKey(ds))
			return -1;
		
		return mapping.get(globalExperiment).get(ds);		
	}
	
	public Map<DataSet, Integer> getMapping(int globalExperiment)
	{
		return mapping.get(globalExperiment);
	}
	
	/**
	 * Convenience function for getting the name of a local experiment in a dataset for a global experiment.  
	 * @param ds The dataset for which the local experiment name is searched. 
	 * @param globalExperiment globalExperiment The global experiment to be matched to a local experiment
	 * @return The experiment name of the global experiment in dataset ds. 
	 * <b>Caution</b> This can return null, if the global experiment is not present in this dataset. 
	 */
	public String getLocalName(DataSet ds, int globalExperiment)
	{
		if(getLocalExperiment(ds, globalExperiment)==-1)
			return null;
		return ds.getMasterTable().getExperimentName(getLocalExperiment(ds, globalExperiment));		
	}
	
	/**
	 * Creates a trivial experiment mapping by concatenating all experiments from the datasets, in the order of input. 
	 * @param dataSets
	 * @return
	 */
	public static ExperimentMapping createMapping(List<DataSet> dataSets)
	{
		ExperimentMapping mapping=new ExperimentMapping(dataSets);
		int g=0;
		for(DataSet ds:dataSets)
		{
			for(int i=0; i!= ds.getMasterTable().getNumberOfExperiments();++i)
			{
				mapping.map(ds, i, g);
				
				mapping.setGlobalName(g,ds.getMasterTable().getExperimentName(i));
				g++;
			}
		}
		return mapping;
	}
	
	/**
	 * Creates a trivial experiment mapping by placing all datasets next to each other, in the order of input. 
	 * @param dataSets
	 * @return
	 */
	public static ExperimentMapping createBySideMapping(List<DataSet> dataSets)
	{
		ExperimentMapping mapping=new ExperimentMapping(dataSets);
		int maxExp=0;
		for(DataSet ds: dataSets)
		{
			maxExp=Math.max(ds.getMasterTable().getNumberOfExperiments(), maxExp);
		}		
		for(DataSet ds:dataSets)
		{
			for(int i=0; i!= ds.getMasterTable().getNumberOfExperiments();++i)
			{
				mapping.map(ds, i, i);
			}
		}
		for(int i=0; i!=maxExp; ++i)
		{
			mapping.setGlobalName(i,Integer.toString(i));
		}		
		return mapping;
	}
	
	/**
	 * Map all experiments with the same name to each other, ordered by name
	 * @param dataSets 
	 * @return
	 */
	public static ExperimentMapping createMappingByName(List<DataSet> dataSets)
	{
		Set<String> names=new TreeSet<String>();
		for(DataSet ds:dataSets)
		{
			names.addAll(ds.getMasterTable().getExperimentNames());
		}
		ExperimentMapping mapping=new ExperimentMapping(dataSets);
		int exp=0;
		for(String name:names)
		{
			for(DataSet ds:dataSets)
			{
				int localIdx=ds.getMasterTable().getExperimentNames().indexOf(name);
				if(localIdx < 0)
					continue;
				mapping.map(ds,localIdx,exp);
			}
			mapping.setGlobalName(exp,name);
			
			++exp;
		}
		return mapping;
	}
	
	public void removeGlobalExperiment(int g)
	{
		mapping.remove(g);
		globalExperimentNames.remove(g);
		cleanup();
	}
	
	public void removeMapping(int globalExperiment, DataSet ds)
	{
		mapping.get(globalExperiment).remove(ds);
	}
	
	private void cleanup()
	{
		List<Map<DataSet,Integer>> data=new ArrayList<Map<DataSet,Integer>>();
		for(int i: mapping.keySet())
		{
			data.add(mapping.get(i));
		}
		mapping.clear();
		for(int i=0; i!=data.size(); ++i)
		{
			mapping.put(i,data.get(i));
		}		
	}
	
	@Override
	public boolean isCollapsable() 
	{
		for(Integer i:mapping.keySet())
		{
			if(mapping.get(i).size()>1)
				return false;
		}
		return true;	
	}
	
	@Override
	public List<Integer> getCommonExperiments(DataSet ds) 
	{
		List<Integer> res=new ArrayList<Integer>();
		for(int i=0; i!=getNumberOfExperiments(); ++i)
		{
			if(getMapping(i).size()==dataSets.size())
			{
				res.add(getLocalExperiment(ds, i));
			}
		}		
		return res;
	}
	
	/**
	 * @param head
	 * @param dataSets
	 * @return
	 */
	public static ExperimentMapping createMapping(DataSet head, List<DataSet> dataSets)
	{
		List<DataSet> dss= new ArrayList<DataSet>();
		dss.add(head);
		dss.addAll(dataSets);
		ExperimentMapping mapping=new ExperimentMapping(dss);
		int exp=0;
		for(String name:head.getMasterTable().getExperimentNames())
		{
			for(DataSet ds:dataSets)
			{
				int localIdx=ds.getMasterTable().getExperimentNames().indexOf(name);
				if(localIdx < 0)
					continue;
				mapping.map(ds,localIdx,exp);
			}
			mapping.setGlobalName(exp,name);
			++exp;
		}
		return mapping;
	}

	public List<DataSet> getDataSets() {
		return dataSets;
	}

	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}
	
	public static ExperimentMapping createMappingTime(List<DataSet> dataSets)
	{
		Set<Double> timePoints=new TreeSet<Double>();
		for(DataSet ds: dataSets)
		{
			TimeseriesMIO mio=TimeseriesMIO.getForDataSet(ds, true, true);
			timePoints.addAll(mio.getValue());
		}
		
		ExperimentMapping mapping=new ExperimentMapping(dataSets);
		int exp=0;
		for(double d: timePoints)
		{
			for(DataSet ds:dataSets)
			{
				int localIdx=TimeseriesMIO.getForDataSet(ds, true, true).getValue().indexOf(d);
				if(localIdx < 0)
					continue;
				mapping.map(ds,localIdx,exp);
			}
			mapping.setGlobalName(exp, Double.toString(d));
			exp++;
			
		}
		
		return mapping;
		
	}
	
	public static ExperimentMapping createSingleDataSetMapping(DataSet ds)
	{
		List<DataSet> dss= new ArrayList<DataSet>();
		dss.add(ds);
		ExperimentMapping mapping=new ExperimentMapping(dss);
		int exp=0;
		for(String name:ds.getMasterTable().getExperimentNames())
		{
			mapping.map(ds,exp,exp);
			mapping.setGlobalName(exp,name);
			++exp;
		}
		return mapping;
	}
	
}
