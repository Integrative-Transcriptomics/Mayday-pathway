package mayday.graphviewer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.gui.MaydayDialog;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;

@SuppressWarnings("serial")
public class AddMultipleNodesDialog extends MaydayDialog
{

	private JTable table;
	private NodeTableModel model;
	private boolean cancelled;

	public boolean isCancelled() {
		return cancelled;
	}

	public AddMultipleNodesDialog() 
	{
		super(null, "Add Nodes");
		model=new NodeTableModel();
		table=new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(400, 200));

		Box addBox=Box.createHorizontalBox();
		addBox.add(new JButton(new LoadTableAction()));
		addBox.add(Box.createHorizontalStrut(20));
		addBox.add(new JButton(new AddNodeAction(Nodes.Roles.NODE_ROLE)));
		addBox.add(new JButton(new AddNodeAction(Nodes.Roles.PROBE_ROLE)));
		addBox.add(new JButton(new AddNodeAction(Nodes.Roles.NOTE_ROLE)));
		addBox.add(new JButton(new AddNodeAction(ProcessDiagram.SIMPLE_CHEMICAL_ROLE)));
		addBox.add(new JButton(new AddNodeAction(ProcessDiagram.MACROMOLECULE_ROLE)));
		addBox.add(new JButton(new AddNodeAction(ProcessDiagram.NUCLEIC_ACID_FEATURE_ROLE)));
		addBox.add(new JButton(new AddNodeAction(ProcessDiagram.TAG_ROLE)));
		addBox.add(new JButton(new AddNodeAction(ProcessDiagram.PROCESS_ROLE)));
		addBox.add(new JButton(new AddNodeAction(ProcessDiagram.DISSOCIATION_ROLE)));
		addBox.add(new JButton(new AddNodeAction(ProcessDiagram.ASSOCIATION_ROLE)));
		Box okCancelBox=Box.createHorizontalBox();
		okCancelBox.add(new JButton(new RemoveAction()));
		okCancelBox.add(Box.createHorizontalGlue());
		okCancelBox.add(new JButton(new OkCancelAction(false)));
		okCancelBox.add(new JButton(new OkCancelAction(true)));
		JScrollPane scroller=new JScrollPane(table);		
		setLayout(new BorderLayout());
		add(scroller,BorderLayout.CENTER);
		add(addBox,BorderLayout.NORTH);
		add(okCancelBox,BorderLayout.SOUTH);
		pack();
	}

	public List<MultiProbeNode> getNodes(Graph g, DataSet ds)
	{
		List<MultiProbeNode> result=new ArrayList<MultiProbeNode>();
		for(int i=0; i!= model.getRowCount(); ++i)
		{
			MultiProbeNode node=new MultiProbeNode(g);
			node.setName(model.getValueAt(i, 0).toString().trim());
			node.setRole(model.getValueAt(i, 1).toString().trim());
			// handle attributes:			
			String a=model.getValueAt(i,3).toString().trim();
			if(!a.isEmpty())
			{
				Map<String, String> attributes= new HashMap<String, String>();
				// split by "," to find tag value pairs
				String[] tokA=a.split(",");
				for(String t:tokA)
				{
					// split each token by "="
					String[] tokB=t.split("=");
					if(tokB.length==1)
						attributes.put(tokB[0], "true");
					else
					{
						attributes.put(tokB[0], tokB[1]);
					}						
				}
				node.setProperties(attributes);
			}			
			// handling probes is easier: 
			String p=model.getValueAt(i,2).toString().trim();
			if(!p.isEmpty())
			{
				String[] pTok=p.split(",");
				for(String t:pTok)
				{
					Probe probe=ds.getMasterTable().getProbe(t);
					if(probe==null)
					{
						for(Probe pr:ds.getMasterTable().getProbes().values())
						{
							if(pr.getDisplayName().equals(t))
								probe=pr;
						}
					}
					if(probe!=null)
						node.addProbe(probe);
				}
			}
			result.add(node);
		}
		return result;
	}

	private class AddNodeAction extends AbstractAction
	{
		private String role=Nodes.Roles.NODE_ROLE;

		public AddNodeAction(String r) 
		{
			super("Add "+r);
			role=r;
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			model.addRow("New "+role,role);
		}		
	}
	
	private class LoadTableAction extends AbstractAction
	{
		public LoadTableAction(){
			super("Load..." );
		}

		@Override
		public void actionPerformed(ActionEvent e){
			JFileChooser fileChooser=new JFileChooser();
			int res=fileChooser.showOpenDialog(AddMultipleNodesDialog.this);
			if(res==JFileChooser.APPROVE_OPTION)
			{
				try 
				{
					BufferedReader r=new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
					String line=r.readLine();
					while(line!=null)
					{
						String[] tok=line.split("\t");
						if(tok.length<=4)
						{
							String[] nrw={"","","",""};
							for(int i=0; i!= tok.length; ++i)
							{
								nrw[i]=tok[i];
							}								
							model.addRow(nrw);
						}
						line=r.readLine();
					}
					r.close();
				} catch (Exception e1) 
				{
					e1.printStackTrace();
				}				
			}
		}		
	}


	private class NodeTableModel implements TableModel
	{
		List<TableModelListener> listeners=new ArrayList<TableModelListener>();		
		List<String[] > rows=new ArrayList<String[] >();

		@Override
		public Class<?> getColumnClass(int columnIndex) 
		{
			//			if(columnIndex==3)
			//				return Map.class;
			return String.class;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
		{
			rows.get(rowIndex)[columnIndex]=aValue.toString();	
			TableModelEvent e=new TableModelEvent(this,rowIndex,rowIndex,columnIndex,TableModelEvent.UPDATE);
			for(TableModelListener l:listeners)
			{			
				l.tableChanged(e);
			}

		}

		@Override
		public void removeTableModelListener(TableModelListener l) 
		{
			listeners.remove(l);			
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) 
		{
			return rows.get(rowIndex)[columnIndex];
		}

		@Override
		public int getColumnCount() 
		{
			return 4;
		}

		@Override
		public String getColumnName(int columnIndex) 
		{
			switch (columnIndex) 
			{
			case 0: return "Name";
			case 1: return "Role";
			case 2: return "Probes";
			case 3: return "Attributes";
			default: return "";
			}
		}

		@Override
		public int getRowCount() 
		{
			return rows.size();
		}

		@Override
		public void addTableModelListener(TableModelListener l) 
		{
			listeners.add(l);			
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) 
		{
			return true;
		}

		public void addRow(String name, String role)
		{
			String[] row={name,role,"",""};
			rows.add(row);
			TableModelEvent e=new TableModelEvent(this,rows.size()-2,rows.size()-1,TableModelEvent.ALL_COLUMNS,TableModelEvent.INSERT);
			for(TableModelListener l:listeners)
			{			
				l.tableChanged(e);
			}
		}
		
		public void addRow(String[] row)
		{
			rows.add(row);
			TableModelEvent e=new TableModelEvent(this,rows.size()-2,rows.size()-1,TableModelEvent.ALL_COLUMNS,TableModelEvent.INSERT);
			for(TableModelListener l:listeners)
			{			
				l.tableChanged(e);
			}
		}
		
		public void removeRow(int r)
		{
			rows.remove(r);
			TableModelEvent e=new TableModelEvent(this,r,r,TableModelEvent.ALL_COLUMNS,TableModelEvent.DELETE);
			for(TableModelListener l:listeners)
			{			
				l.tableChanged(e);
			}
		}
	}

	private class OkCancelAction extends AbstractAction
	{
		boolean ok;

		public OkCancelAction(boolean ok) 
		{
			super(ok?"Ok":"Cancel");
			this.ok=ok;

		}
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(ok)
			{
				cancelled=false;
				dispose();

			}else
			{
				cancelled=true;
				dispose();
			}

		}
	}
	

	private class RemoveAction extends AbstractAction
	{
		public RemoveAction() 
		{
			super("remove");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			model.removeRow(table.getSelectedRow());
			
		}
	}



}
