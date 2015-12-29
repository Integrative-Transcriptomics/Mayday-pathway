package mayday.pathway.keggview.kegg;

/**
 * Interface for different strategies of parsing KEGG files.
 * @author Stephan Symons
 *
 */
public interface ParsingStrategy 
{
	public KEGGObject processItem(GenericKEGGDataItem item);
}
