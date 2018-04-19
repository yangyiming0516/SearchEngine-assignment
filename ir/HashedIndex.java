/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 *   Additions: Hedvig Kjellström, 2012-14
 */  


package ir;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.lang.Math;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.io.File;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;


/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
 
 
public class HashedIndex implements Index {


    /** The index as a hashtable. */
	private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();
	private HashMap<String,HashMap<String, PostingsList> > BIindex = new HashMap();
	
    //private HashMap<String,PostingsListNode> index = new HashMap<String,PostingsListNode>();
	//public HashMap<String,String>  docIDs = new HashMap<String,String>();
	//public HashMap<String,Integer>  docLengths = new HashMap<String,Integer>();

    /**
     *  Inserts this token in the index.
     */
    public void insert( String token, int docID, int offset ) {
	//
	//  YOUR CODE HERE
	//
		//System.err.println("inserting "+token+" docID");
        if (index.containsKey(token.intern())){
            index.get(token.intern()).insert2(docID);	
		}
        else
        {
            PostingsList tmp = new PostingsList();
			//PostingsListNode tmp = new PostingsListNode();
			//tmp.create(String.valueOf(index.size()));
			tmp.total=0;
			tmp.docs=0;
            tmp.insert2(docID);
            index.put(token.intern(),tmp);
			//if (docID > 0 && docID % 100 ==0) update();
        }
        //System.err.println("inserted "+token+" docID");
		//System.err.println(docID);
		
    }
	
	public void insertBI( String token1, String token2, int docID, int offset ) {
		//System.err.println("inserting "+token+" docID");
		
		if (BIindex.containsKey(token1)){
			if (BIindex.get(token1).containsKey(token2)){
				BIindex.get(token1).get(token2).insert2(docID);
			}
			else{
				PostingsList tmp = new PostingsList();
				tmp.total=0;
				tmp.docs=0;
				tmp.insert2(docID);
				BIindex.get(token1).put(token2.intern(),tmp);
			}
		}
		else{
			HashMap<String,PostingsList> t = new HashMap<String,PostingsList>();
			PostingsList tmp = new PostingsList();
			//PostingsListNode tmp = new PostingsListNode();
			//tmp.create(String.valueOf(index.size()));
			tmp.total=0;
			tmp.docs=0;
            tmp.insert2(docID);
			t.put(token2.intern(),tmp);
			BIindex.put(token1.intern(),t);
		}
		/*
        if (BIindex.containsKey(token)){
            BIindex.get(token).insert(docID,offset);	
		}
        else
        {
            PostingsList tmp = new PostingsList();
			//PostingsListNode tmp = new PostingsListNode();
			//tmp.create(String.valueOf(index.size()));
			tmp.total=0;
			tmp.docs=0;
            tmp.insert(docID,offset);
            BIindex.put(token,tmp);
			//if (docID > 0 && docID % 100 ==0) update();
        }
		*/
        //System.err.println("inserted "+token+" docID");
		//System.err.println(docID);
    }


    /**
     *  Returns all the words in the index.
     */
	 
    public Iterator<String> getDictionary() {
	// 
	//  REPLACE THE STATEMENT BELOW WITH YOUR CODE
	//
        return index.keySet().iterator();
    }
	
	public Set<Map.Entry<String,PostingsList>> getSet(){
		return index.entrySet();
	}
	
	
	
    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
	
    public PostingsList getPostings( String token ) {
	
	// 
	//  REPLACE THE STATEMENT BELOW WITH YOUR CODE
	
        if (index.containsKey(token))
            return index.get(token);
			
	return null;
    }
	
	public PostingsList getPostingsBI( String token1, String token2 ) {
	
	// 
	//  REPLACE THE STATEMENT BELOW WITH YOUR CODE
	
        if (BIindex.containsKey(token1))
			if (BIindex.get(token1).containsKey(token2))
				return BIindex.get(token1).get(token2);
			
	return null;
    }
	

    /**
     *  Searches the index for postings matching the query.
     */
	 
    public PostingsList search( Query query, int queryType, int rankingType, int structureType ) {
	// 
	//  REPLACE THE STATEMENT BELOW WITH YOUR CODE
	//
		double start = System.nanoTime();
		double threshold = 0;
		
		if (query.size()==0) return null;
		PostingsList result = null;
		
		if (queryType == INTERSECTION_QUERY){
			PostingsList tmp1 = new PostingsList();
			PostingsList tmp2 = new PostingsList();
			if (getPostings(query.terms.getFirst())==null) result = new PostingsList();
			else result = getPostings(query.terms.getFirst()).clone();
			for (int i=1;i<query.size();i++){
				if (result.isEmpty()) tmp1 = new PostingsList();
				else tmp1 = result.clone();
				if (getPostings(query.terms.get(i))==null) tmp2 = new PostingsList();
				else tmp2 = getPostings(query.terms.get(i)).clone();
				result.clear();
				while (!(tmp1.isEmpty()||tmp2.isEmpty())){
					if (tmp1.getFirst().docID==tmp2.getFirst().docID){
						result.add(tmp1.getFirst());
						tmp1.removeFirst();
						tmp2.removeFirst();
					}
					else
						if (tmp1.getFirst().docID < tmp2.getFirst().docID)
							tmp1.removeFirst();
						else
							tmp2.removeFirst();
				}
			}
		}
		
		
		if (queryType == RANKED_QUERY){
			if (structureType==UNIGRAM || query.size()==1){
			double normal = 0;
			for (int i=0;i<query.size();i++)
				normal += query.weights.get(i)*query.weights.get(i);
			normal = Math.sqrt(normal);
			PostingsList tmp1 = new PostingsList();
			PostingsList tmp2 = new PostingsList();
			result = new PostingsList();
			int p=0;
			while (p<query.size()){
				if (getPostings(query.terms.get(p))==null || getPostings(query.terms.get(p)).idf<threshold){
					p++;
					continue;
				}
				if (getPostings(query.terms.get(p)) == null) result = new PostingsList();
				else 
					for (PostingsEntry i:getPostings(query.terms.get(p)).list)
					{
								PostingsEntry newList = new PostingsEntry();
								newList.docID = i.docID;
								newList.score = i.score*query.weights.getFirst();
								//newList.pagerank = i.pagerank;
								result.add(newList);
					}
				break;
			}
			for (int i=p+1;i<query.size();i++){
				if (getPostings(query.terms.get(i))==null || getPostings(query.terms.get(i)).idf<threshold){
					continue;
				}
				if (result.isEmpty()) tmp1 = new PostingsList();
				else tmp1 = result.clone();
				if (getPostings(query.terms.get(i))==null) tmp2 = new PostingsList();
				else tmp2 = getPostings(query.terms.get(i)).clone();
				result.clear();
				while (!(tmp1.isEmpty()&&tmp2.isEmpty())){
					if ((!tmp1.isEmpty()) && (!tmp2.isEmpty()) &&tmp1.getFirst().docID==tmp2.getFirst().docID){
						PostingsEntry newList = new PostingsEntry();
						newList.docID=tmp1.getFirst().docID;
						newList.score = tmp1.getFirst().score+tmp2.getFirst().score*query.weights.get(i);
						//newList.pagerank = tmp1.getFirst().pagerank;
						result.add(newList);
						tmp1.removeFirst();
						tmp2.removeFirst();
					}
					else
						if (tmp2.isEmpty() || (!tmp1.isEmpty() && tmp1.getFirst().docID < tmp2.getFirst().docID)){
							PostingsEntry newList = new PostingsEntry();
							newList.docID = tmp1.getFirst().docID;
							newList.score = tmp1.getFirst().score;
							//newList.pagerank = tmp1.getFirst().pagerank;
							result.add(newList);
							tmp1.removeFirst();
							}
						else
						{
							PostingsEntry newList = new PostingsEntry();
							newList.docID = tmp2.getFirst().docID;
							newList.score = tmp2.getFirst().score*query.weights.get(i);
							//newList.pagerank = tmp2.getFirst().pagerank;
							result.add(newList);
							tmp2.removeFirst();
						}
				}
				
			}
			
			for (int i=0;i<result.size();i++)
					result.get(i).score = result.get(i).score/normal;
					
			//System.err.println(rankingType);
			result.sort();
			}
			
			if (structureType==BIGRAM){
			double normal = 0;
			normal += query.size()-1;
			normal = Math.sqrt(normal);
			PostingsList tmp1 = new PostingsList();
			PostingsList tmp2 = new PostingsList();
			result = new PostingsList();
			/*
			int p=0;
			while (p<query.size()){
				if (getPostingsBI(query.terms.get(p)).idf<threshold){
					p++;
					continue;
				}
				if (getPostings(query.terms.get(p))==null) result = new PostingsList();
				else 
					for (PostingsEntry i:getPostings(query.terms.get(p)).list)
					{
								PostingsEntry newList = new PostingsEntry();
								newList.docID = i.docID;
								newList.score = i.score*query.weights.getFirst();
								result.add(newList);
					}
				break;
			}
			*/
			for (int i=1;i<query.size();i++){
				if (getPostingsBI(query.terms.get(i-1),query.terms.get(i))==null){
					continue;
				}
				else
					tmp2=getPostingsBI(query.terms.get(i-1),query.terms.get(i)).clone();
				if (result.isEmpty()) tmp1 = new PostingsList();
				else tmp1 = result.clone();
				result.clear();
				while (!(tmp1.isEmpty()&&tmp2.isEmpty())){
					if ((!tmp1.isEmpty()) && (!tmp2.isEmpty()) && tmp1.getFirst().docID==tmp2.getFirst().docID){
						PostingsEntry newList = new PostingsEntry();
						newList.docID=tmp1.getFirst().docID;
						newList.score = tmp1.getFirst().score+tmp2.getFirst().score;
						result.add(newList);
						tmp1.removeFirst();
						tmp2.removeFirst();
					}
					else
						if (tmp2.isEmpty() || (!tmp1.isEmpty() && tmp1.getFirst().docID < tmp2.getFirst().docID)){
							PostingsEntry newList = new PostingsEntry();
							newList.docID = tmp1.getFirst().docID;
							newList.score = tmp1.getFirst().score;
							result.add(newList);
							tmp1.removeFirst();
							}
						else
						{
							PostingsEntry newList = new PostingsEntry();
							newList.docID = tmp2.getFirst().docID;
							newList.score = tmp2.getFirst().score;
							result.add(newList);
							tmp2.removeFirst();
						}
				}
				
			}
			
			for (int i=0;i<result.size();i++){
					result.get(i).score = result.get(i).score/normal;
					//System.err.println(result.get(i).score);
			}
			//System.err.println(rankingType);
			result.sort();
			}
			
			if (structureType==SUBPHRASE){
				double normal = 0;
				normal += query.size()-1;
				normal = Math.sqrt(normal);
				PostingsList tmp1 = new PostingsList();
				PostingsList tmp2 = new PostingsList();
				result = new PostingsList();

				for (int i=1;i<query.size();i++){
					if (getPostingsBI(query.terms.get(i-1),query.terms.get(i))==null){
						continue;
					}
					else
						tmp2=getPostingsBI(query.terms.get(i-1),query.terms.get(i)).clone();
					if (result.isEmpty()) tmp1 = new PostingsList();
					else tmp1 = result.clone();
					result.clear();
					while (!(tmp1.isEmpty()&&tmp2.isEmpty())){
						if ((!tmp1.isEmpty()) && (!tmp2.isEmpty()) && tmp1.getFirst().docID==tmp2.getFirst().docID){
							PostingsEntry newList = new PostingsEntry();
							newList.docID=tmp1.getFirst().docID;
							newList.score = tmp1.getFirst().score+tmp2.getFirst().score;
							result.add(newList);
							tmp1.removeFirst();
							tmp2.removeFirst();
						}
						else
							if (tmp2.isEmpty() || (!tmp1.isEmpty() && tmp1.getFirst().docID < tmp2.getFirst().docID)){
								PostingsEntry newList = new PostingsEntry();
								newList.docID = tmp1.getFirst().docID;
								newList.score = tmp1.getFirst().score;
								result.add(newList);
								tmp1.removeFirst();
								}
							else
							{
								PostingsEntry newList = new PostingsEntry();
								newList.docID = tmp2.getFirst().docID;
								newList.score = tmp2.getFirst().score;
								result.add(newList);
								tmp2.removeFirst();
							}
					}
					
				}
				
				for (int i=0;i<result.size();i++){
						result.get(i).score = result.get(i).score/normal;
						//System.err.println(result.get(i).score);
				}
				//System.err.println(rankingType);
				result.sort();
			}
			
		}
		
		double end = System.nanoTime();
		System.err.println("The searching took " + (end-start)/1000000 +"ms");
		return result;

		
    }
	
	

    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
	
	public void process(){
	
		System.err.println("calculating tf-idf for unigram,wait!");
		for (Entry<String,PostingsList> e: index.entrySet()){
			double idf = Math.log(docIDs.size()/e.getValue().docs);
			e.getValue().idf = idf;
			
			for (PostingsEntry L: e.getValue().list){
				double tf =  L.tf;
				if (tf>0) tf = 1 + Math.log(tf);
				double tmp = idf * tf;
				 L.score = tmp;
				
				if (normal.containsKey(""+ L.docID))
					normal.put(""+ L.docID,normal.get(""+ L.docID)+tmp*tmp);
				else normal.put(""+ L.docID,tmp*tmp);
				
				//System.err.print(key+" in "+index.get(key).list.get(i).docID+":");
				//System.err.println(index.get(key).list.get(i).score);
			}
			
			
		}
		
		for (String key: normal.keySet()){
				normal.put(key,Math.sqrt(normal.get(key)));
				//System.err.println(normal.get(key));
		}
		
		
		
		
		
		for (Entry<String,PostingsList> e: index.entrySet()){
			for (PostingsEntry L: e.getValue().list)
				 L.score =  L.score / normal.get(""+ L.docID);
			
		}
		
		System.err.println("calculating tf-idf for bigram,wait!");
		for (Entry<String,HashMap<String, PostingsList> > ee: BIindex.entrySet())	
		for (Entry<String, PostingsList> e: ee.getValue().entrySet())
		{
			double idf = Math.log(docIDs.size()/e.getValue().docs);
			e.getValue().idf = idf;
			
			for (PostingsEntry L: e.getValue().list){
				double tf =  L.tf;
				if (tf>0) tf = 1 + Math.log(tf);
				double tmp = idf * tf;
				 L.score = tmp;
				
				if (normal.containsKey(""+ L.docID))
					normal.put(""+ L.docID,normal.get(""+ L.docID)+tmp*tmp);
				else normal.put(""+ L.docID,tmp*tmp);
				
				//System.err.print(key+" in "+index.get(key).list.get(i).docID+":");
				//System.err.println(index.get(key).list.get(i).score);
			}
			
			
		}
		
		for (String key: normal.keySet()){
				normal.put(key,Math.sqrt(normal.get(key)));
				//System.err.println(normal.get(key));
		}
		
		
		
		
		
		for (Entry<String,PostingsList> e: index.entrySet()){
			for (PostingsEntry L: e.getValue().list)
				 L.score =  L.score / normal.get(""+ L.docID);
			
		}
		
		System.err.println("process done!");
	}

}
