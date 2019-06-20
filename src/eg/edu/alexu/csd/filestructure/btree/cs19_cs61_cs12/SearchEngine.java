package eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eg.edu.alexu.csd.filestructure.btree.IBTree;
import eg.edu.alexu.csd.filestructure.btree.ISearchEngine;
import eg.edu.alexu.csd.filestructure.btree.ISearchResult;

public class SearchEngine implements ISearchEngine {
	
	private IBTree<String, List<ISearchResult>> tree;
	
	public SearchEngine(int minDegree) {
		tree = new BTree<>(minDegree);
	}

	@Override
	public void indexWebPage(String filePath) {
		try {
			String[][] all = this.parseFile(filePath);
			
			File fout = new File("parsing2.txt");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			for (int i = 0; i < all.length; i++) {
				bw.newLine();
				bw.write("the id of doc: " + all[i][0]);
				bw.newLine();
				String content = all[i][3];
				String[] words = content.split("\\s+");
				for (int j = 0; j < words.length; j++) {
					String finalWord = words[j].trim().toLowerCase();
					if (!finalWord.equals("")) { // checks for empty word or spaces
						bw.write("word is: " + finalWord);
						bw.newLine();
						List<ISearchResult> res;
						try {
							res = tree.search(finalWord);
							if (res == null) { // the word isn't found in the tree
								res = new ArrayList<>();
								res.add(new SearchResult(all[i][0], 1));
								tree.insert(finalWord, res);
							} else {
								int k = 0;
								for (k = 0; k < res.size(); k++) {
									ISearchResult temp = res.get(k);
									if (temp.getId().equals(all[i][0])) { // found the same word in a certain doc
										temp.setRank(temp.getRank() + 1); // update the rank
										break;
									}
								}
								if (k == res.size()) {
									res.add(new SearchResult(all[i][0], 1)); // didn't find the same word in the doc
								}
							}
						} catch(RuntimeErrorException e) {
							res = new ArrayList<>();
							res.add(new SearchResult(all[i][0], 1));
							tree.insert(finalWord, res);
						}
					}
				}
			}
			bw.close();
		} catch(Exception e) {
			
		}
	}
	
	private String[][] parseFile(String filePath) {
		try {

			if (filePath == null || filePath == "") {
				throw new RuntimeErrorException(null);
			}
			
			File inputFile = new File(filePath);

			if (inputFile.exists()) {
				
				File fout = new File("parsing.txt");
				FileOutputStream fos = new FileOutputStream(fout);

				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();

				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				bw.write("Root element :" + doc.getDocumentElement().getNodeName());
				bw.newLine();
						
				NodeList nList = doc.getElementsByTagName("doc");
				
				String[][] all = new String[nList.getLength()][4];
						
				//System.out.println("----------------------------");

				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);

					//System.out.println("\nCurrent Element :" + nNode.getNodeName());
					bw.write("\nCurrent Element :" + nNode.getNodeName());
					bw.newLine();
							
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;

						//System.out.println("Page id : " + eElement.getAttribute("id"));
						bw.write("Page id : " + eElement.getAttribute("id"));
						bw.newLine();
						all[temp][0] = eElement.getAttribute("id");
						
						//System.out.println("Page url : " + eElement.getAttribute("url"));
						bw.write("Page url : " + eElement.getAttribute("url"));
						bw.newLine();
						all[temp][1] = eElement.getAttribute("url");
						
						//System.out.println("Page title : " + eElement.getAttribute("title"));
						bw.write("Page title : " + eElement.getAttribute("title"));
						bw.newLine();
						all[temp][2] = eElement.getAttribute("title");
						
						//System.out.println("String : " + eElement.getTextContent());
						bw.write("String : " + eElement.getTextContent());
						bw.newLine();
						all[temp][3] = eElement.getTextContent();

						
					}
				}
				bw.close();
				return all;
			} else {
				throw new RuntimeErrorException(null);
			}
			
		} catch (Exception e) {
			throw new RuntimeErrorException(null);
		}
	}
	
	/*private boolean findFile(String name, File file) {
		File[] list = file.listFiles();
		if(list != null) {
	        for (File fil : list) {
	            if (fil.isDirectory()) {
	                findFile(name, fil);
	            } else if (name.equalsIgnoreCase(fil.getName())) {
	            	this.path = fil.getPath();
	            	this.found = true;
	            	System.out.println(" ");
	            	System.out.println("WebPage Path : " + this.path);
	            }
	        }
	        if (found) {
				return true;
			} else {
				return false;
			}
	    }
	
		return false;	
	 }*/
	
	private void listFiles(File[] arr,int index,int level)  { 
        // terminate condition 
        if(index == arr.length) 
            return; 
          
        // tabs for internal levels 
        for (int i = 0; i < level; i++) 
            System.out.print("\t"); 
          
        // for files 
        if(arr[index].isFile()) {
            System.out.println(arr[index].getAbsolutePath());
        	this.indexWebPage(arr[index].getAbsolutePath()); // calls the index webpage to index this file
        }
        // for sub-directories 
        else if(arr[index].isDirectory()) { 
            System.out.println("[" + arr[index].getName() + "]"); 
              
            // recursion for sub-directories 
            listFiles(arr[index].listFiles(), 0, level + 1); 
        } 
           
        // recursion for main directory 
        listFiles(arr,++index, level); 
   } 

	@Override
	public void indexDirectory(String directoryPath) {
		
		File directoryFile = new File(directoryPath);
	
		if(directoryFile.exists() && directoryFile.isDirectory()) {

            File arr[] = directoryFile.listFiles();  //Files listed in array Of Files
              
            System.out.println("Files from main directory : " + directoryFile); 
            System.out.println("**********************************************");
            
              
            // Calling recursive method 
            listFiles(arr,0,0);  
       } 
		
	}

	@Override
	public void deleteWebPage(String filePath) {
		String[][] all = parseFile(filePath);

		for (int i = 0; i < all.length; i++) {
			String content = all[i][3];
			String id = all[i][0];
			String[] words = content.split("\\s+");
			for (int j = 0; j < words.length; j++) {
				String finalWord = words[j].trim().toLowerCase();
				if (!finalWord.equals("")) { // checks for empty word or spaces {
					List<ISearchResult> res;
					try {
						res = tree.search(finalWord);
						if (res == null) { // the word isn't found in the tree
							System.out.println("File has not been inserted");
							return;
						} else {
							for (int k = 0; k < res.size(); k++) {
								if (res.get(k).getId().equals(id)) {
									res.remove(k);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public List<ISearchResult> searchByWordWithRanking(String word) {

		if (word == null) {
			throw new RuntimeErrorException(null);
		}
		if (word.equals("")) {
			return new ArrayList<>();
		}
        String searchedWord = word.toLowerCase();

        List<ISearchResult> searchResultList = tree.search(searchedWord);
        if (searchResultList == null) {
        	return new ArrayList<>();
        }
        int size = searchResultList.size();

        for (int i = 0; i < size - 1; i++)   {
               for (int j = 0; j < size-i-1; j++)  {
                   if (searchResultList.get(j).getRank() > searchResultList.get(j+1).getRank()) {
                        int tempRank = searchResultList.get(j).getRank();
                        String tempId = searchResultList.get(j).getId();
                        searchResultList.get(j).setRank(searchResultList.get(j+1).getRank());
                        searchResultList.get(j).setId(searchResultList.get(j+1).getId());
                        searchResultList.get(j+1).setRank(tempRank);
                        searchResultList.get(j+1).setId(tempId);
                   }
               }
        }

        return searchResultList;

    }

	@Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {


        List<List<ISearchResult>> searchResultLists = new ArrayList();

        String searchedSentence = sentence.toLowerCase();
        String[] words = searchedSentence.split(" ");

        for (int i = 0; i < words.length; i++) {
            searchResultLists.add(tree.search(words[i]));
        }

        for (int i = 0; i < searchResultLists.size() - 1; i++) {  //AllWords
            List<ISearchResult> intersectionSearchResult = new ArrayList();

            for (int j = 0; j < searchResultLists.get(i).size(); j++) { //FirstWord

                String firstId = searchResultLists.get(i).get(j).getId();
                int firstRank = searchResultLists.get(i).get(j).getRank();


                for (int t = 0; t < searchResultLists.get(i+1).size(); t++) { //SecondWord

                    String secondId = searchResultLists.get(i+1).get(t).getId();
                    int secondRank = searchResultLists.get(i+1).get(t).getRank();

                    if (firstId.equals(secondId)) {
                        int rank = 0;
                        if (firstRank < secondRank) {
                            rank = firstRank;
                        } else {
                            rank = secondRank;
                        }
                        ISearchResult x = new SearchResult(firstId, rank);
                        intersectionSearchResult.add(x);

                    }
                }
            }

            searchResultLists.set(i + 1, intersectionSearchResult);

        }



        return searchResultLists.get(searchResultLists.size() - 1);

    }

}
