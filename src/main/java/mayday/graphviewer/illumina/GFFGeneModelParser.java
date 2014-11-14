package mayday.graphviewer.illumina;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.structures.maps.MultiHashMap;
import mayday.genetics.basic.Strand;
import mayday.genetics.coordinatemodel.GBAtom;

public class GFFGeneModelParser implements GFFGeneModelConstants
{
	boolean useUTR=false; 

	private List<GBAtom> genes;
	private Map<GBAtom, String> geneToID;
	
	private Map<String, String> exonToChromosome;

	private MultiHashMap<String, String> geneTomRNAs;
	private MultiHashMap<String, GBAtom> exons; 

	public void parse(File file) throws IOException
	{
		BufferedReader r=new BufferedReader(new FileReader(file));
		String line=r.readLine();
		while(line.startsWith("#")) // skip any comment. 
		{
			line=r.readLine();
		}
		// 1st useful line
		exons=new MultiHashMap<String, GBAtom>();
		exonToChromosome=new HashMap<String, String>();
		// count occurrences
		int i=0;
		genes=new ArrayList<GBAtom>();
		geneToID=new HashMap<GBAtom, String>();
		geneTomRNAs=new MultiHashMap<String, String>();
		System.out.println("parsing genes and exons");
		while(line!=null)
		{
			if(line.startsWith("###"))
				break; // the end of the file is reached. only in some gff files. 
			String[] toks=line.split("\t");
			if(toks[2].equals(EXON) || // we havean exon
					(useUTR && (toks[2].equals(FIVE_PRIME_UTR) || toks[2].equals(THREE_PRIME_UTR)))) // or a UTR we can not ignore.
			{
				Strand.fromChar(toks[6].charAt(0));
				String[] attTok=toks[8].split(";");
				GBAtom exon=new GBAtom(Long.valueOf(toks[3]), Long.valueOf(toks[4]), Strand.fromChar(toks[6].charAt(0))); 
				String p=null;
				boolean added=false;
				for(String s: attTok)
				{
					if(s.startsWith(PARENT_ATTRIBUTE))
					{
						p=s.substring(7);	
						added=true;
						break;
					}
				}
				if(!added)
				{
					// look for locus_tag instead. 
					for(String s: attTok)
					{
						if(s.startsWith(LOCUS_TAG))
						{
							p=s.substring(10);	
							added=true;
							break;
						}
					}
				}
				exons.put(p, exon);
				exonToChromosome.put(p,toks[0]);
			}
			if(toks[2].equals(GENE))
			{
				// we will take this one anyways.
				Strand.fromChar(toks[6].charAt(0));
				String[] attTok=toks[8].split(";");
				GBAtom gene=new GBAtom(Long.valueOf(toks[3]), Long.valueOf(toks[4]), Strand.fromChar(toks[6].charAt(0)));
				String p=null;
				boolean added=false;
				for(String s: attTok){
					if(s.startsWith(ID_ATTRIBUTE)){
						p=s.substring(3);	
						added=true;
						break;
					}
				}
				if(!added){
					// look for locus_tag instead. 
					for(String s: attTok){
						if(s.startsWith(LOCUS_TAG))
						{
							p=s.substring(10);	
							added=true;
							break;
						}
					}
				}
				genes.add(gene);
				geneToID.put(gene, p);
			}
			if(toks[2].equals(MRNA))
			{
//				ID=AT1G44318.1;Parent=AT1G44318;Name=AT1G44318.1
				Strand.fromChar(toks[6].charAt(0));
				String[] attTok=toks[8].split(";");
				String name=null;
				boolean named=false;
				for(String s: attTok){
					if(s.startsWith(ID_ATTRIBUTE)){
						name=s.substring(3);	
						named=true;
						break;
					}
				}
				if(!named){
					// look for locus_tag instead. 
					for(String s: attTok){
						if(s.startsWith(NAME))
						{
							name=s.substring(10);	
							named=true;
							break;
						}
					}
				}
				
				String p=null;
				boolean added=false;
				for(String s: attTok)
				{
					if(s.startsWith(PARENT_ATTRIBUTE))
					{
						p=s.substring(7);	
						added=true;
						break;
					}
				}
				if(!added)
				{
					// look for locus_tag instead. 
					for(String s: attTok)
					{
						if(s.startsWith(LOCUS_TAG))
						{
							p=s.substring(10);	
							added=true;
							break;
						}
					}
				}
				
				geneTomRNAs.put(p, name);
			}
			line=r.readLine();
			++i;
		}
		System.out.println("Parsed "+genes.size()+" genes and "+exons.size()+" exons");		
	}

	public List<GBAtom> getGenes() {
		return genes;
	}

	public Map<GBAtom, String> getGeneToID() {
		return geneToID;
	}
	
	public MultiHashMap<String, GBAtom> getExons() {
		return exons;
	}
	
	public Map<String, String> getExonToChromosome() {
		return exonToChromosome;
	}
	
	public MultiHashMap<String, String> getGeneTomRNAs() {
		return geneTomRNAs;
	}

	public static void analyze(String f) throws IOException
	{
		GFFGeneModelParser parser=new GFFGeneModelParser();	
		parser.parse(new File(f));
		MultiHashMap<String, GBAtom> exons = parser.getExons();
		List<GBAtom> genes=parser.getGenes();
		Map<GBAtom, String> geneToId=parser.getGeneToID();
		for(GBAtom g: genes)
			System.out.println(exons.get(geneToId.get(g)).size());
		return; 
	}
	
//	public static void main(String[] args) throws Exception
//	{
//		GFFGeneModelParser parser=new GFFGeneModelParser();
////		parser.parse(new File("/home/symons/Illumina/TAIR9_GFF3_genes.gff"));
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001135.gff"));
////		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001224.gff"));
//		Graph g= new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		System.out.println(g.nodeCount());
//		
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001136.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//	
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001137.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001138.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001139.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001140.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//	
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001141.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001142.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001143.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001144.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//	
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001145.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001146.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		
//		parser.parse(new File("/home/symons/Anaconda/yeast/genome/NC_001147.gff"));
//		new GeneModelGraphFactory().buildOverallGraph(parser, parser.getGenes(), new MultiHashMap<GBAtom, Probe>(), GeneModelStyle.COMPRESSED);
//		
//		
//		GraphCanvas canvaS=new GraphCanvas(new DefaultGraphModel(g));
//		canvaS.setLayouter(new SnakeLayout());
//		
//		
//		
////		JFrame frame=new JFrame("Zorp!");
////		frame.setLayout(new BorderLayout());
////		JTable table=new JTable(new GeneModelTableModel(parser));
////		frame.add(new JScrollPane(canvaS));
//////		frame.add(new JScrollPane(table));
////		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////		frame.pack();
////		
////		frame.setVisible(true);
//	}

}
