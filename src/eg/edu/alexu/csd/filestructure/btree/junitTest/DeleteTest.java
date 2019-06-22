package eg.edu.alexu.csd.filestructure.btree.junitTest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.management.RuntimeErrorException;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;

import eg.edu.alexu.csd.filestructure.btree.IBTree;
import eg.edu.alexu.csd.filestructure.btree.IBTreeNode;
import eg.edu.alexu.csd.filestructure.btree.ISearchEngine;
import eg.edu.alexu.csd.filestructure.btree.ISearchResult;
import eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12.SearchResult;


public class DeleteTest {
	private final boolean debug = false;

	/**
	 * Test delete web page with null or empty parameter or not found.
	 */
	@Test
	public void testDeleteWebPageNullorEmptyorNotFoundParamter() {

		ISearchEngine searchEngine = (ISearchEngine) TestRunner.getImplementationInstanceForInterface(ISearchEngine.class, new Object[]{100});

		try {
			searchEngine.deleteWebPage(null);
			Assert.fail();
		} catch (RuntimeErrorException ex) {
			try {
				searchEngine.deleteWebPage("");
				Assert.fail();
			} catch (RuntimeErrorException ex1) {
				try {
					searchEngine.deleteWebPage("koko");
				} catch (RuntimeErrorException ex2) {
				}
			}
		}
		catch (Throwable e) {
			TestRunner.fail("Fail to delete web page", e);
		}
	}

	/**
	 * Test delete web page complex.
	 */
	@Test
	public void testDeleteWebPageSimple() {

		ISearchEngine searchEngine = (ISearchEngine) TestRunner.getImplementationInstanceForInterface(ISearchEngine.class, new Object[]{100});
		/**
		 * This test should be modified according to the testing directory and the search query.
		 * You should make sure that the test can support multiple file in the same directory.
		 * You should test your implementation against cases including:
		 * 1- word that does not exist in tree.
		 * 2- word exists.
		 * 3- lower case, upper case, mix btw lower and upper, e.g.. THE, the, ThE, tHE....
		 * According to each change you should modify the expected variable to have the expected outcome.
		 */
		try {
			searchEngine.indexDirectory("Directory2");
			searchEngine.deleteWebPage("Directory2\\wiki_00");
			List<ISearchResult> expected = Arrays.asList(new SearchResult[]{ new SearchResult("7702785", 2), new SearchResult("7708226", 21),
					new SearchResult("7702780", 34), new SearchResult("7708196", 36)});
			List<ISearchResult> actual = searchEngine.searchByWordWithRanking("tHE");
			for (ISearchResult searchRes : actual) {
				System.out.println(searchRes.toString());
			}
			Collections.sort(actual, new Comparator<ISearchResult>() {
				@Override
				public int compare(ISearchResult o1, ISearchResult o2) {
					return o1.getRank() - o2.getRank();
				}
			});
			for (int i = 0; i < expected.size(); i++) {
				Assert.assertEquals(expected.get(i).getId(), actual.get(i).getId());
				Assert.assertEquals(expected.get(i).getRank(), actual.get(i).getRank());
			}
		} catch (Throwable e) {
			TestRunner.fail("Fail to delete web page", e);
		}
	}
	/**
	 * Test delete web page complex.
	 */
	@Test
	public void testDeleteWebPageSimple2() {

		ISearchEngine searchEngine = (ISearchEngine) TestRunner.getImplementationInstanceForInterface(ISearchEngine.class, new Object[]{100});
		/**
		 * This test should be modified according to the testing directory and the search query.
		 * You should make sure that the test can support multiple file in the same directory.
		 * You should test your implementation against cases including:
		 * 1- word that does not exist in tree.
		 * 2- word exists.
		 * 3- lower case, upper case, mix btw lower and upper, e.g.. THE, the, ThE, tHE....
		 * According to each change you should modify the expected variable to have the expected outcome.
		 */
		try {
			searchEngine.indexDirectory("Directory2");
			searchEngine.deleteWebPage("Directory2\\wiki_00");
			List<ISearchResult> expected = Arrays.asList(new SearchResult[] {});
			List<ISearchResult> actual = searchEngine.searchByWordWithRanking("5555");
			for (ISearchResult searchRes : actual) {
				System.out.println(searchRes.toString());
			}
			Collections.sort(actual, new Comparator<ISearchResult>() {
				@Override
				public int compare(ISearchResult o1, ISearchResult o2) {
					return o1.getRank() - o2.getRank();
				}
			});
			for (int i = 0; i < expected.size(); i++) {
				Assert.assertEquals(expected.get(i).getId(), actual.get(i).getId());
				Assert.assertEquals(expected.get(i).getRank(), actual.get(i).getRank());
			}
		} catch (Throwable e) {
			TestRunner.fail("Fail to delete web page", e);
		}
	}
	/**
	 * Test delete unindexed web page.
	 */
	@Test
	public void testDeleteWebPageUnIndexedWebPage() {

		ISearchEngine searchEngine = (ISearchEngine) TestRunner.getImplementationInstanceForInterface(ISearchEngine.class, new Object[]{100});
		/**
		 * This test should be modified according to the testing directory and the search query.
		 * You should make sure that the test can support multiple file in the same directory.
		 * You should test your implementation against cases including:
		 * 1- word that does not exist in tree.
		 * 2- word exists.
		 * 3- lower case, upper case, mix btw lower and upper, e.g.. THE, the, ThE, tHE....
		 * According to each change you should modify the expected variable to have the expected outcome.
		 */
		try {
			searchEngine.indexWebPage("Directory2\\wiki_01");
			searchEngine.indexWebPage("Directory2\\subfolder\\wiki_02");
			searchEngine.deleteWebPage("Directory2\\wiki_00");
			List<ISearchResult> expected = Arrays.asList(new SearchResult[]{ new SearchResult("7702785", 2), new SearchResult("7708226", 21),
					new SearchResult("7702780", 34), new SearchResult("7708196", 36)});
			List<ISearchResult> actual = searchEngine.searchByWordWithRanking("ThE");
			for (ISearchResult searchRes : actual) {
				System.out.println(searchRes.toString());
			}
			Collections.sort(actual, new Comparator<ISearchResult>() {
				@Override
				public int compare(ISearchResult o1, ISearchResult o2) {
					return o1.getRank() - o2.getRank();
				}
			});
			for (int i = 0; i < expected.size(); i++) {
				Assert.assertEquals(expected.get(i).getId(), actual.get(i).getId());
				Assert.assertEquals(expected.get(i).getRank(), actual.get(i).getRank());
			}
		} catch (Throwable e) {
			TestRunner.fail("Fail to delete web page", e);
		}
	}

	/**
	 * Test delete empty all the indexed web page.
	 */
	@Test
	public void testDeleteAllIndexedWebPage() {

		ISearchEngine searchEngine = (ISearchEngine) TestRunner.getImplementationInstanceForInterface(ISearchEngine.class, new Object[]{100});

		try {
			searchEngine.indexWebPage("Directory2\\wiki_00");
			searchEngine.indexWebPage("Directory2\\wiki_01");
			searchEngine.indexWebPage("Directory2\\subfolder\\wiki_02");
			searchEngine.deleteWebPage("Directory2\\wiki_01");
			searchEngine.deleteWebPage("Directory2\\subfolder\\wiki_02");
			searchEngine.deleteWebPage("Directory2\\wiki_00");

			List<ISearchResult> actual = searchEngine.searchByWordWithRanking("ThE");
			Assert.assertEquals(0, actual.size());
		} catch (Throwable e) {
			TestRunner.fail("Fail to delete web page", e);
		}
	}

	private int getHeight (IBTreeNode<?, ?> node) {
		if (node.isLeaf()) return 0;

		return node.getNumOfKeys() > 0 ? 1 + getHeight(node.getChildren().get(0)) : 0;
	}

	private boolean verifyBTree (IBTreeNode<?, ?> node, int lvl, int height, int t, IBTreeNode<?, ?> root) {
		if (!node.equals(root)) 
			if (node.getNumOfKeys() < t - 1 || node.getNumOfKeys() > 2 * t - 1)
				return false;
		boolean ans = true;
		if (!node.isLeaf()) {
			for (int i = 0; i <= node.getNumOfKeys(); i++) {
				ans = ans && verifyBTree(node.getChildren().get(i), lvl + 1, height, t, root);
				if (!ans) break;
			}

		}else {
			ans = ans && (lvl == height);
		}
		return ans;
	} 

	private void traverseTreeInorder(IBTreeNode<Integer, String> node, List<Integer> keys, List<String> vals) {
		int i; 
		for (i = 0; i < node.getNumOfKeys(); i++) 
		{ 

			if (!node.isLeaf()) 
				traverseTreeInorder(node.getChildren().get(i), keys, vals);
			keys.add(node.getKeys().get(i));
			vals.add(node.getValues().get(i));
		} 
		if (!node.isLeaf()) 
			traverseTreeInorder(node.getChildren().get(i), keys, vals);
	}

	private void traverseBtreePreOrder(IBTreeNode<?, ?> node, int level, List<List<List<?>>> keys) {
		if (level >= keys.size())
			keys.add(new ArrayList<>());
		keys.get(level).add(node.getKeys());
		if (!node.isLeaf())
			for (int j = 0; j <= node.getNumOfKeys(); j++)
				traverseBtreePreOrder(node.getChildren().get(j), level + 1, keys);
	}


}
