package mayday.pathway.keggview.kegg.taxonomy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;

public class TaxonomyParser 
{
	private String fileName;
	
	public TaxonomyParser(String fileName)
	{
		this.fileName=fileName;
	}
	
	public DefaultMutableTreeNode parse() throws IOException
	{
		DefaultMutableTreeNode root=new DefaultMutableTreeNode("Root");
		
		BufferedReader r=new BufferedReader(new FileReader(fileName));
		String line=r.readLine();
		DefaultMutableTreeNode parent=root;
		
		DefaultMutableTreeNode level1=null;
		DefaultMutableTreeNode level2=null;
		DefaultMutableTreeNode level3=null;
		DefaultMutableTreeNode level4=null;
		
		while(line!=null)
		{
			//System.out.println("->"+line+"<-");
			if(line.equals(""))
			{
				line=r.readLine();
				continue;
			}
			//System.out.println(line);
			if(line.startsWith("#"))
			{
				if(line.startsWith("# "))
				{
					//superkingdom
					level1=new DefaultMutableTreeNode(line.substring(2));
					root.add(level1);
					parent=level1;
					level2=null;
					level3=null;
					level4=null;
				}
				if(line.startsWith("## "))
				{
					//kingdom
					level2=new DefaultMutableTreeNode(line.substring(3));
					level1.add(level2);
					parent=level2;
					
				}
				if(line.startsWith("### "))
				{
					//
					level3=new DefaultMutableTreeNode(line.substring(4));
					level2.add(level3);
					parent=level3;
				}
				if(line.startsWith("#### "))
				{
					//class
					level4=new DefaultMutableTreeNode(line.substring(5));
					level3.add(level4);
					parent=level4;
				}
				
				//handle classes
			}else
			{
				//handle taxa
				TaxonomyItem item=new TaxonomyItem();
				item.parse(line);
				DefaultMutableTreeNode node=new DefaultMutableTreeNode(item);
				parent.add(node);
				
				
			}
			line=r.readLine();
		}
		
		return root;
	}
}
