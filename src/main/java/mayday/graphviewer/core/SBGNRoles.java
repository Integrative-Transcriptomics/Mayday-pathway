package mayday.graphviewer.core;

import javax.swing.JMenu;

import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.actions.RoleAction;
import mayday.vis3.graph.components.NodeComponent;

public class SBGNRoles extends ProcessDiagram{
	
	public static JMenu roleSelectionMenu(NodeComponent component)
	{
		JMenu menu=new JMenu("SBGN");
		
		menu.add(new RoleAction(MACROMOLECULE_ROLE, component));
		menu.add(new RoleAction(SIMPLE_CHEMICAL_ROLE, component));
		menu.add(new RoleAction(NUCLEIC_ACID_FEATURE_ROLE, component));
		menu.addSeparator();	
		menu.add(new RoleAction(SOURCE_ROLE, component));
		menu.add(new RoleAction(SINK_ROLE, component));
		menu.add(new RoleAction(TAG_ROLE, component));
		
		menu.add(new RoleAction(PHENOTYPE_ROLE, component));
		menu.add(new RoleAction(PERTUBING_AGENT_ROLE, component));
		
//		menu.add(new RoleAction(OBSERVALBE_ROLE, component));
//		menu.add(new RoleAction(PERTUBATION_ROLE, component));
		
		JMenu mcMenu=new JMenu("Multimers and Complexes");
		mcMenu.add(new RoleAction(COMPLEX_ROLE, component));
		mcMenu.add(new RoleAction(MULTIMER_COMPLEX_ROLE, component));
		mcMenu.add(new RoleAction(MULTIMER_NUCLEIC_ACID_FEATURE_ROLE, component));
		mcMenu.add(new RoleAction(MULTIMER_MACROMOLECULE_ROLE, component));
		mcMenu.add(new RoleAction(MULTIMER_SIMPLE_CHEMICAL_ROLE,component));
		menu.add(mcMenu);

		JMenu lMenu=new JMenu("Logic");
		lMenu.add(new RoleAction(LOGIC_AND_ROLE, component));
		lMenu.add(new RoleAction(LOGIC_OR_ROLE, component));
		lMenu.add(new RoleAction(LOGIC_NOT_ROLE, component));		
		menu.add(lMenu);

		JMenu tMenu=new JMenu("Transitions");
		tMenu.add(new RoleAction(ASSOCIATION_ROLE, component));
		tMenu.add(new RoleAction(UNCERTAIN_PROCESS_ROLE, component));
		tMenu.add(new RoleAction(PROCESS_ROLE, component));
		tMenu.add(new RoleAction(OMITTED_PROCESS_ROLE, component));
		tMenu.add(new RoleAction(DISSOCIATION_ROLE, component));
		menu.add(tMenu);
		
		return menu;
	}
}
