package mayday.graphviewer.crossViz3.probes;

import java.util.Collection;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;

public interface IProbeMapping extends Iterable<IProbeUnit>
{
	/**
	 * Return the mapping as a collection of IProbeUnits 
	 * @return
	 */
	public Collection<IProbeUnit> getUnits();
	
	/**
	 * Return all the units that contain a specific probe
	 * @param p
	 * @return
	 */
	public List<IProbeUnit> getUnits(Probe p);
	
	/**
	 * Get the unit with a specific name. 
	 * @param unitName
	 * @return
	 */
	public IProbeUnit getUnit(String unitName);
		
	/**
	 * @return the number of datasets involved in this probe mapping
	 */
	public int getNumberOfDataSets();
	
	/**
	 * @return the datasets involved in this probe mapping
	 */
	public List<DataSet> getDataSets();

	
	/**
	 * @return the number of units present in this mapping. 
	 */
	public int getNumberOfUnits();
	
	/**
	 * Add a new unit to the mapping. 
	 * @param unit
	 */
	public void addUnit(IProbeUnit unit);

	/**
	 * remove a unit from the mapping. 
	 * @param unit
	 */
	public void removeUnit(IProbeUnit unit);

	
	
}
