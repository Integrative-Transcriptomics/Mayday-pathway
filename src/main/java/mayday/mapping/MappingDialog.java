package mayday.mapping;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayDialog;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.meta.types.StringMIO;
import mayday.pathway.biopax.parser.CycDatParser;
import mayday.pathway.biopax.parser.CycDatParserDialog;

@SuppressWarnings("serial")
public class MappingDialog extends MaydayDialog
{
	// data necessary for operation
	private MappingManager manager;
	private ProbeList probeList;
	
	// Displaying the mapping
	private JTable table;
	private DefaultTableModel model;
	
	// Components for MIO Group creation (referenced to by MIGroupAction.
	private JComboBox groupBox;
	private JTextField groupNameField;
	
	public MappingDialog(MappingManager manager, ProbeList probeList) 
	{
		this.probeList=probeList;
		groupBox=new JComboBox();
		setMapping(manager);		
		table=new JTable(model);
		setTitle("Probe Annotation Mapping");
		setLayout(new BorderLayout());
		add(new JScrollPane(table),BorderLayout.CENTER);

		groupNameField=new JTextField(25);
		groupBox.addActionListener(new GroupListener());
		JPanel addButtonPanel=new JPanel();
		JButton addMIOButton=new JButton(new MIOAction());
		JButton addFileButton=new JButton(new FileAction());
		JButton addCycButton=new JButton(new CycAction());
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(addMIOButton);
		buttonBox.add(Box.createHorizontalStrut(15));
		buttonBox.add(addFileButton);
		buttonBox.add(Box.createHorizontalStrut(15));
		buttonBox.add(addCycButton);
		buttonBox.add(Box.createHorizontalGlue());

		addButtonPanel.add(buttonBox);
		add(addButtonPanel,BorderLayout.NORTH);

		JButton addMIGroupButton=new JButton(new MIGroupAction());
		
		Box mioBox=Box.createHorizontalBox();
		mioBox.add(groupBox);
		mioBox.add(groupNameField);
		mioBox.add(addMIGroupButton);
		mioBox.add(Box.createHorizontalGlue());
		
		add(mioBox,BorderLayout.SOUTH);
		setSize(600, 800);
		pack();

	}

	public void setMapping(MappingManager map)
	{
		manager=map;
		model=new DefaultTableModel(probeList.getNumberOfProbes(),manager.groupCount());
		Vector<String> colNames=new Vector<String>();

		Map<Probe,Integer> probeMap=new HashMap<Probe, Integer>();
		int i=0;
		for(Probe p:probeList)
		{
			probeMap.put(p, i);
			++i;
		}


		int j=0;
		for(String grp:manager.getGroups().keySet())
		{

			for(String t:manager.getGroups().get(grp))
			{
				Probe p=manager.getProbeForIdenitfier(t);
				if(p==null) continue;
				model.setValueAt(t, probeMap.get(p), j);
			}
			colNames.add(grp);
			++j;
		}
		model.setColumnIdentifiers(colNames);
		System.out.println(model.getColumnCount()+"\t"+model.getRowCount());
		groupBox.removeAllItems();
		for(String g:manager.getGroups().keySet())
		{
			groupBox.addItem(g);
		}
	}
	
	private class GroupListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			groupNameField.setText((String)groupBox.getSelectedItem());			
		}
	}
	
	private class MIGroupAction extends AbstractAction
	{
		public MIGroupAction() 
		{
			super("Add as MIOGroup");
		}

		public void actionPerformed(ActionEvent e)
		{
			MIGroup mioGroup= probeList.getDataSet().getMIManager().newGroup("PAS.MIO.String", groupNameField.getText());	
			
			for(String s:manager.getGroups().get(groupBox.getSelectedItem()))
			{
				Probe p=manager.getProbeForIdenitfier(s);
				if(p!= null)
				{
					StringMIO mio=new StringMIO(s);
					mioGroup.add(p, mio);
				}
			}
		}

	}

	private class MIOAction extends AbstractAction
	{
		public MIOAction() 
		{
			super("Add MIO Groups...");
		}
		public void actionPerformed(ActionEvent e) 
		{
			MIGroupSelectionDialog msd=new MIGroupSelectionDialog(probeList.getDataSet().getMIManager(), "PAS.MIO.String");
			msd.setVisible(true);
			MIGroupSelection<MIType> selection= msd.getSelection();

			manager.addMapping(selection, probeList);
			setMapping(manager);
			table.setModel(model);
			manager.processHelper();
		}
	}

	private class FileAction extends AbstractAction
	{
		public FileAction() 
		{
			super("Add Annotation File...");
		}
		public void actionPerformed(ActionEvent e) 
		{
			JFileChooser chooser=new JFileChooser();
			chooser.setMultiSelectionEnabled(true);
			int r=chooser.showOpenDialog(MappingDialog.this);
			if(r==JFileChooser.APPROVE_OPTION)
			{
				try 
				{
					for(File f:chooser.getSelectedFiles())
					{
						HashMap<String, String> m=MappingFileParser.parse(f);
						manager.addMapping(m,f.getName());
					}
				} catch (IOException e1) 
				{
					JOptionPane.showMessageDialog(MappingDialog.this, "Error parsing file","Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				manager.processHelper();
				setMapping(manager);
				table.setModel(model);
			}			

		}
	}

	private class CycAction extends AbstractAction
	{
		public CycAction() 
		{
			super("Add BioCyc File...");
		}
		public void actionPerformed(ActionEvent e) 
		{
			JFileChooser chooser=new JFileChooser();
			//			chooser.setMultiSelectionEnabled(true);
			int r=chooser.showOpenDialog(MappingDialog.this);
			if(r==JFileChooser.APPROVE_OPTION)
			{
				try 
				{
					List<String> atts=CycDatParser.parseAttributes(chooser.getSelectedFile());
					CycDatParserDialog attsDialog=new CycDatParserDialog(atts);
					attsDialog.setVisible(true);
					if(attsDialog.isAccept())
					{
						Map<String,String> res= CycDatParser.parseMapping(chooser.getSelectedFile(), attsDialog.getSource(), attsDialog.getTarget());
						manager.addMapping(res,chooser.getSelectedFile().getName());
						manager.processHelper();
						setMapping(manager);
						table.setModel(model);
					}
				} catch (IOException e1) 
				{
					JOptionPane.showMessageDialog(MappingDialog.this, "Error parsing file","Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

			}			

		}
	}
}
