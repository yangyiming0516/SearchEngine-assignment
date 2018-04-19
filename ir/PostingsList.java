/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.util.LinkedList;
import java.io.Serializable;
import java.io.FileWriter;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;

/**
 *   A list of postings for a given word.
 */
public class PostingsList implements Serializable {
    
    /** The postings list as a linked list. */
    public LinkedList<PostingsEntry> list = new LinkedList<PostingsEntry>();
	public int total;
	public int docs;
	public double idf;


    /**  Number of postings in this list  */
    public int size() {
	return list.size();
    }

    /**  Returns the ith posting */
    public PostingsEntry get( int i ) {
	return list.get( i );
    }

    //
    //  YOUR CODE HERE
    //
	/*
    public void insert (int docID, int offset){
		if (list.size()>0 && list.getLast().docID==docID){
			list.getLast().position.add(offset);
		}
		else{
			PostingsEntry newList = new PostingsEntry();
			newList.docID = docID;
			newList.position.add(offset);
			list.add(newList);
			docs++;
		}
		total++;
		//System.err.println(list.size());
    }
	*/
	public void insert2 (int docID){
		if (list.size()>0 && list.getLast().docID==docID){
			list.getLast().tf++;
			return;
		}
		else{
			PostingsEntry newList = new PostingsEntry();
			newList.docID = docID;
			newList.tf = 1;
			list.add(newList);
			docs++;
		}
		total++;
		//System.err.println(list.size());
    }
	
	public PostingsEntry getFirst() {
		return list.getFirst();
	}
	
	public PostingsEntry getLast(){
		return list.getLast();
	}
	
	
	public void removeFirst() {
		list.removeFirst();
	}
	
	public void add( PostingsEntry i ) {
		list.add( i );
    }
	
	public boolean isEmpty(){
		return size()==0;
	}
	
	public void clear() {
		list.clear();
	}
	
	public PostingsList clone(){
		PostingsList nlist = new PostingsList();
		nlist.list = (LinkedList<PostingsEntry>) list.clone();
		return nlist;
	}
	
	class MyComparator implements Comparator<PostingsEntry> {
    @Override
    public int compare(PostingsEntry u, PostingsEntry v) {
        if (u.score<v.score) return 1;
		if (u.score>v.score) return -1;
		return 0;
		}
	}

	
	public void sort(){

		Comparator<PostingsEntry> cmp = new MyComparator();
		Collections.sort(list,cmp);

	}
	
	
}
	

			   
