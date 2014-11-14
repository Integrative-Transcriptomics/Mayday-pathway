package mayday.graphviewer.illumina;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import mayday.core.structures.maps.MultiHashMap;
import mayday.genetics.coordinatemodel.GBAtom;

@SuppressWarnings("serial")
public class GeneModelSelector extends JDialog 
{
	private JList modelList;
	private boolean ok=false;
	
	public GeneModelSelector(MultiHashMap<String, GBAtom> models) 
	{
		
		Vector<String> names=new Vector<String>(models.keySet());
		Collections.sort(names);
		System.out.println(names.size());
		modelList=new JList(names);
		
		setLayout(new BorderLayout());
		
		add(new JScrollPane(modelList),  BorderLayout.CENTER);
		add(new JButton(new OKAction()), BorderLayout.SOUTH);
		modelList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		setTitle("Select Gene Models");
		
		pack();

	}
	
	public List<String> getSelectedModels()
	{
		List<String> res=new ArrayList<String>();
		for(Object o: modelList.getSelectedValues())
		{
			res.add((String)o);
		}
		return res;
	}
	
	private class OKAction extends AbstractAction
	{
		public OKAction() 
		{
			super("Ok");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			ok=true;
			dispose();
		}
	}
	
	public boolean isOk() 
	{
		return ok;
	}
}
