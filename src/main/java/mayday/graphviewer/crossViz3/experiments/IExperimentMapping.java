package mayday.graphviewer.crossViz3.experiments;

import java.util.List;
import java.util.Map;

import mayday.core.DataSet;

public interface IExperimentMapping 
{
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
	public void map(DataSet ds, int localExperiment, int globalExperiment);
	
	
	/**
	 * Get the map of datasets to local experiments for a global experiment.
	 * @param globalExperiment
	 * @return
	 */
	public Map<DataSet, Integer> getMapping(int globalExperiment);
	
	
	/**
	 * Set the name of a global experiment
	 * @param exp The index of that experiment
	 * @param name The name of that experiment
	 */
	public void setGlobalName(int exp, String name);
	
	/**
	 * Get the name of a global experiment.
	 * @param exp the experiment
	 * @return the global experiment name of the experiment
	 */
	public String getGlobalName(int exp);
	
	/**
	 * @return the number of global experiments
	 */
	public int getNumberOfExperiments();
	
	/**
	 * Get the local experiment index in the DataSet ds of the global experiment index globalExperiment
	 * @param ds The dataSet for which the local experiment equivalent is searched. 
	 * @param globalExperiment The global experiment to be matched to a local experiment
	 * @return The experiment index of the global experiment in dataset ds. 
	 * <b>Caution</b> If no such global experiment is available, this functiopn returns -1; 
	 */
	public int getLocalExperiment(DataSet ds, int globalExperiment);
		
	/**
	 * Convenience function for getting the name of a local experiment in a dataset for a global experiment.  
	 * @param ds The dataset for which the local experiment name is searched. 
	 * @param globalExperiment globalExperiment The global experiment to be matched to a local experiment
	 * @return The experiment name of the global experiment in dataset ds. 
	 * <b>Caution</b> This can return null, if the global experiment is not present in this dataset. 
	 */
	public String getLocalName(DataSet ds, int globalExperiment);
	
	/**
	 * Remove the global experiment from the mapping.
	 * @param g the index of the global experiment to remove. 
	 */
	public void removeGlobalExperiment(int g);

	
	/**
	 * Remove a mapping of  local experiment in dataset ds to a global experimemt
	 * @param globalExperiment
	 * @param ds
	 */
	public void removeMapping(int globalExperiment, DataSet ds);

	/**
	 * Returns true, iff for each global experiment, there is only one local experiment.
	 * @return
	 */
	public boolean isCollapsable();
	
	public List<DataSet> getDataSets();
	
	public int addGlobalExperiment(); 
	
	public List<Integer> getCommonExperiments(DataSet ds);
	
}
