package eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12;

import java.util.List;

import eg.edu.alexu.csd.filestructure.btree.ISearchEngine;
import eg.edu.alexu.csd.filestructure.btree.ISearchResult;

public class SearchEnginMain {

	public static void main(String[] args) {
		
		
		ISearchEngine searchEngine = new SearchEngine(2);

		//searchEngine.indexDirectory("Directory");
		searchEngine.indexDirectory("Directory/Sub");
		List<ISearchResult> res = searchEngine.searchByWordWithRanking("4rmt");
		
		for (int i = 0; i < res.size(); i++) {
			System.out.println("id: " + res.get(i).getId());
			System.out.println("rank: " + res.get(i).getRank());
			System.out.println();
			System.out.println();
		}
	}

}
