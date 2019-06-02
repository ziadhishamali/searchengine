package eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eg.edu.alexu.csd.filestructure.btree.ISearchEngine;
import eg.edu.alexu.csd.filestructure.btree.ISearchResult;

public class SearchEngine implements ISearchEngine {
	
	private File directoryFile;
	private boolean found = false;
	private String path = "";


	@Override
	public void indexWebPage(String filePath) {
		
		
		try {
			
			if (filePath == null || filePath == "") {
				throw new RuntimeErrorException(null);
			}
			
			if (findFile(filePath, this.directoryFile)) {
				
				File fout = new File("parsing.txt");
				FileOutputStream fos = new FileOutputStream(fout);
			 
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

				File inputFile = new File(this.path);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();

				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				bw.write("Root element :" + doc.getDocumentElement().getNodeName());
				bw.newLine();
						
				NodeList nList = doc.getElementsByTagName("doc");
						
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
						
						//System.out.println("Page url : " + eElement.getAttribute("url"));
						bw.write("Page url : " + eElement.getAttribute("url"));
						bw.newLine();
						
						//System.out.println("Page title : " + eElement.getAttribute("title"));
						bw.write("Page title : " + eElement.getAttribute("title"));
						bw.newLine();
						
						//System.out.println("String : " + eElement.getTextContent());
						bw.write("String : " + eElement.getTextContent());
						bw.newLine();

						
					}
				}
				bw.close();
			} else {
				throw new RuntimeErrorException(null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean findFile(String name, File file) {
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
	 }
	
	private void listFiles(File[] arr,int index,int level)  { 
        // terminate condition 
        if(index == arr.length) 
            return; 
          
        // tabs for internal levels 
        for (int i = 0; i < level; i++) 
            System.out.print("\t"); 
          
        // for files 
        if(arr[index].isFile()) 
            System.out.println(arr[index].getName()); 
          
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
			
			this.directoryFile = directoryFile;

            File arr[] = directoryFile.listFiles();  //Files listed in array Of Files
              
            System.out.println("Files from main directory : " + directoryFile); 
            System.out.println("**********************************************");
            
              
            // Calling recursive method 
            listFiles(arr,0,0);  
       } 
		
	}

	@Override
	public void deleteWebPage(String filePath) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ISearchResult> searchByWordWithRanking(String word) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

}
