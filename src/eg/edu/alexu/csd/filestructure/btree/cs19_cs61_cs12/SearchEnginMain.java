package eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12;

import eg.edu.alexu.csd.filestructure.btree.ISearchEngine;

public class SearchEnginMain {

	public static void main(String[] args) {
		
		
		ISearchEngine searchEngine = new SearchEngine();
		
		searchEngine.indexDirectory("Directory");
		searchEngine.indexWebPage("wiki_10");

	}

}
