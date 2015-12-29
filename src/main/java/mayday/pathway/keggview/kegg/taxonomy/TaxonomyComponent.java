package mayday.pathway.keggview.kegg.taxonomy;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TaxonomyComponent extends JPanel
{
	private TaxonomyItem item;
	
	private JLabel name;
	private JLabel id;
	private JLabel shortcut;
	
	public TaxonomyComponent()
	{
		super();
		this.item=new TaxonomyItem();
		name=new JLabel("                                                  ");
		id=new JLabel("       ");
		shortcut=new JLabel("     ");
		init();
	}
	
	public TaxonomyComponent(TaxonomyItem item)
	{
		super();
		this.item=item;
		
		name=new JLabel(item.getName());
		id=new JLabel(item.getId());
		shortcut=new JLabel(item.getShortcut());
	
		init();
	}
	
	private void init()
	{		
		setLayout(new GridLayout(1,3));
		add(name);
		add(shortcut);
		add(id);
		
		setPreferredSize(new Dimension(100,25));
		
	}
	
	public void setItem(TaxonomyItem it)
	{
		item=it;
		name.setText(item.getName());
		id.setText(item.getId());
		shortcut.setText(item.getShortcut());
		
		repaint();
		System.out.println("!");
	}
}
