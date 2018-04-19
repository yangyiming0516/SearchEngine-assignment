/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Hedvig Kjellström, 2012
 */  

package ir;

import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.HashMap;

public class Query {
    
    public LinkedList<String> terms = new LinkedList<String>();
    public LinkedList<Double> weights = new LinkedList<Double>();
	public double alpha=1;
	public double beta=0.8;

    /**
     *  Creates a new empty Query 
     */
    public Query() {
    }
	
    /**
     *  Creates a new Query from a string of words
     */
    public Query( String queryString  ) {
	StringTokenizer tok = new StringTokenizer( queryString );
	while ( tok.hasMoreTokens() ) {
	    terms.add( tok.nextToken() );
	    weights.add( new Double(1) );
	}    
    }
    
    /**
     *  Returns the number of terms
     */
    public int size() {
	return terms.size();
    }
    
    /**
     *  Returns a shallow copy of the Query
     */
    public Query copy() {
	Query queryCopy = new Query();
	queryCopy.terms = (LinkedList<String>) terms.clone();
	queryCopy.weights = (LinkedList<Double>) weights.clone();
	return queryCopy;
    }
    
    /**
     *  Expands the Query using Relevance Feedback
     */
    public void relevanceFeedback( PostingsList results, boolean[] docIsRelevant, Indexer indexer ) {
	// results contain the ranked list from the current search
	// docIsRelevant contains the users feedback on which of the 10 first hits are relevant
	
	//
	//  YOUR CODE HERE
	//
	/*  considering while vectors of the documents */
		int[] chosen = new int[10];
		int cnt=0;
		for (int i=0;i<10;i++)
			if (docIsRelevant[i]){
				chosen[cnt]=results.list.get(i).docID;
				cnt++;
			}
		HashMap<String,Double> dict = new HashMap<String,Double>();
		for (Entry<String,PostingsList> e: indexer.index.getSet()){
			double sum=0;
			for (PostingsEntry L: e.getValue().list){
				for (int i=0;i<cnt;i++)
					if (L.docID==chosen[i]){
						sum+=L.score;
					}
			}
			dict.put(e.getKey(),sum*beta/cnt);
		}
		
		for (int i=0;i<size();i++){
			dict.put(terms.get(i),dict.get(terms.get(i))+alpha*weights.get(i));
		}
		terms.clear();
		weights.clear();
		for (Entry<String,Double> e: dict.entrySet()){
			if (e.getValue()>0){
				terms.add(e.getKey());
				weights.add(e.getValue());
			}
		}
		
		/* only consider already existed terms (only adjust the order of the results, no new results)
		int[] chosen = new int[10];
		int cnt=0;
		for (int i=0;i<10;i++)
			if (docIsRelevant[i]){
				chosen[cnt]=results.list.get(i).docID;
				cnt++;
			}
		for (int i=0;i<size();i++){
			double sum=0;
			for (PostingsEntry L: indexer.index.getPostings(terms.get(i)).list){
				for (int k=0;k<cnt;k++)
					if (L.docID==chosen[k]){
						sum+=L.score;
					}
			}
			//System.err.println(sum);
			//System.err.println(weights.get(i));
			weights.set(i,weights.get(i)*alpha + beta*sum);
		}
		*/
    }
	
}

    
