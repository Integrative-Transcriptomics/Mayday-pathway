package mayday.graphviewer.datasources.biopax2;


/**
 * Stores data as a bunch of triples (Left --Predicate--> Right). Use massive indexes to quickly find stuff. 
 * @author Stephan Symons
 * @version 0.3
 */
public class TripleStorage //implements Iterable<Triple>
{
//	// maps to efficiently store subjects and predicates
//	private BidirectionalHashMap<Integer, String>subjectName=new BidirectionalHashMap<Integer, String>();
//	private BidirectionalHashMap<Integer,String> predicateName=new BidirectionalHashMap<Integer, String>();
//	// the storage of internal triples
//	private List<Triple> triples=new ArrayList<Triple>();
//	// indexes over subjects and predicates
////	private MultiTreeMap<Integer, Integer> subjectIndex=new MultiTreeMap<Integer, Integer>(); 
////	private MultiTreeMap<Integer, Integer> predicateIndex=new MultiTreeMap<Integer, Integer>();
//	private MultiHashMap<Integer, Integer> subjectIndex=new MultiHashMap<Integer, Integer>(); 
//	private MultiHashMap<Integer, Integer> predicateIndex=new MultiHashMap<Integer, Integer>();
//	// index over objects that are also subjects
//	private MultiTreeMap<Integer, Integer> objectIndex; // created in createObjectIndex(); 
	
	public static final String OBJECT_TYPE = "OBJECT-TYPE";
	
//	public TripleStorage() 
//	{
//		
//	}
//	
//	/**
//	 * Adds a new triple to the storage. 
//	 * @param left
//	 * @param right
//	 * @param predicate
//	 */
//	public void add(String left, String right, String predicate)
//	{
//		int subject=-1;
//		if(subjectName.get(left)!=null)
//			subject=subjectName.get(left);
//		else
//		{
//			subject=subjectName.size();
//			subjectName.put(subject,left);
//		}
//		
//		int pred=-1;
//		if(predicateName.get(predicate)!=null)
//			pred=predicateName.get(predicate);
//		else
//		{
//			pred=predicateName.size();
//			predicateName.put(pred,predicate);
//		}
//		triples.add(new InternalTriple(subject, pred, right));
//		subjectIndex.put(subject, triples.size()-1);
//		predicateIndex.put(pred, triples.size()-1);
//	}
//	
//	public void add(Triple t)
//	{
//		add(t.getLeft(),t.getRight(),t.getPredicate());
//	}
//	
//	public List<Triple> querySubject(String subject)
//	{
//		List<Triple> res=new ArrayList<Triple>();
//		if(subjectName.get(subject)== null)
//			return res;
//		
//		for(int i:subjectIndex.get((Integer)subjectName.get(subject)))
//		{
//			res.add(triples.get(i));
//		}
//		return res;
//	}
//	
//	public List<Triple> querySubject(String subject, String pred)
//	{
//		List<Triple> res=new ArrayList<Triple>();
//		if(subjectName.get(subject)== null)
//			return res;
//		if(predicateName.get(pred)== null)
//			return res;
//		Set<Integer> s1=new TreeSet<Integer>(subjectIndex.get((Integer)subjectName.get(subject)));
//		s1.retainAll(predicateIndex.get((Integer)predicateName.get(pred)));
//		for(int i:s1)
//		{
//			res.add(triples.get(i));
//		}
//		return res;
//	}
//	
//	public Triple queryFirstSubject(String subject, String pred)
//	{
//		Triple res=null;
//		if(subjectName.get(subject)== null)
//			return res;
//		if(predicateName.get(pred)== null)
//			return res;
//		Set<Integer> s1=new TreeSet<Integer>(subjectIndex.get((Integer)subjectName.get(subject)));
//		s1.retainAll(predicateIndex.get((Integer)predicateName.get(pred)));
//		for(int i:s1)
//		{
//			return triples.get(i);
//		}
//		return res;
//	}
//		
//	public List<Triple> queryObject(String object)
//	{
//		List<Triple> res=new ArrayList<Triple>();
//		// are we querying an object that is also a subject?
//		if(subjectName.get(object)!=null && objectIndex!=null)
//		{
//			// use index: 
//			for(int i: objectIndex.get((Integer)subjectName.get(object)))
//			{
//				res.add(triples.get(i));
//			}
//		}else
//		{
//			// iterate over triples
//			for(Triple t:triples)
//				if(t.getRight().equals(object))
//					res.add(t);
//		}
//		return res;
//	}
//	
//	public Triple queryFirstObject(String object)
//	{
//		Triple res=null;
//		// are we querying an object that is also a subject?
//		if(subjectName.get(object)!=null && objectIndex!=null)
//		{
//			// use index: 
//			for(int i: objectIndex.get((Integer)subjectName.get(object)))
//			{
//				return (triples.get(i));
//			}
//		}else
//		{
//			// iterate over triples
//			for(Triple t:triples)
//				if(t.getRight().equals(object))
//					return(t);
//		}
//		return res;
//	}
//		
//	public List<Triple> queryObject(String object, String pred)
//	{
//		List<Triple> res=new ArrayList<Triple>();
//		// limit search to predicates:
//		if(predicateName.get(pred)== null)
//			return res;
//			
//		// use index over predicates to limit search.
//		Set<Integer> s1=new TreeSet<Integer>(predicateIndex.get((Integer)predicateName.get(pred)));
//		// are we querying an object that is also a subject and can we use the objectIndex?
//		if(subjectName.get(object)!=null && objectIndex!=null)
//		{
//			// use index: 
//			s1.retainAll(objectIndex.get((Integer)subjectName.get(object)));
//			for(int i: s1)
//			{
//				res.add(triples.get(i));
//			}
//		}else
//		{
//			// iterate over triples that have predicate, check for object
//			for(int i:s1)
//			{
//				if(triples.get(i).getRight().equals(object))
//					res.add(triples.get(i));
//			}
//		}
//		return res;
//	}
//	
//	public TripleStorage subsetBySubject(String subject)
//	{
//		TripleStorage res=new TripleStorage();
//		for(Triple t: querySubject(subject))
//			res.add(t);
//		return res;
//	}
//	
//	public TripleStorage subsetBySubjectPredicate(String subject, String pred)
//	{
//		TripleStorage res=new TripleStorage();
//		for(Triple t: querySubject(subject,pred))
//			res.add(t);
//		return res;
//	}
//	
//	public TripleStorage subsetByObjectPredicate(String object, String pred)
//	{
//		TripleStorage res=new TripleStorage();
//		for(Triple t: queryObject(object,pred))
//			res.add(t);
//		return res;
//	}
//	
//	public TripleStorage subsetByObjectPredicate(String object)
//	{
//		TripleStorage res=new TripleStorage();
//		for(Triple t: queryObject(object))
//			res.add(t);
//		return res;
//	}
//	
////	public TripleStorage join(TripleStorage other, TripleTarget on)
////	{
////		TripleStorage res=new TripleStorage();
////		
////		
////		return res;
////	}
//	
//	
//	public int size()
//	{
//		return triples.size();
//	}
//	
//	public void createObjectIndex()
//	{
//		objectIndex=new MultiTreeMap<Integer, Integer>();
//		int i=0;
//		for(Triple t:triples)
//		{
//			if(subjectName.get(t.getRight())!=null)
//			{
//				int subject=subjectName.get(t.getRight());
//				objectIndex.put(subject,i);
//			}
//			++i;	
//		}
//	}
//	
//	@Override
//	public Iterator<Triple> iterator() 
//	{
//		return triples.iterator();
//	}
//	
//	public class InternalTriple implements Triple
//	{
//		public int subject;
//		public int predicate;
//		public String object;
//		
//		public InternalTriple(int subject, int predicate, String object) 
//		{
//			super();
//			this.subject = subject;
//			this.predicate = predicate;
//			this.object = object;
//		}
//		
//		@Override
//		public String toString() 
//		{
//			return "<"+subjectName.get(subject)+","+predicateName.get(predicate)+","+object+">";
//		}
//		
//		public String getLeft()
//		{
//			return subjectName.get(subject);
//		}
//		
//		public String getPredicate()
//		{
//			return predicateName.get(predicate);
//		}
//		
//		public String getRight()
//		{
//			return object;
//		}
//	}
//	
//	public enum TripleTarget{
//		SUBJECT, PREDICATE, OBJECT;
//	}
//	
//	public String getObjectType(String subject)
//	{
//		List<Triple> t=querySubject(subject, OBJECT_TYPE);
//		if(t.size()!=1)
//			return null;
//		else
//		{
//			return t.get(0).getRight();
//		}
//	}
}
