import java.util.Iterator;
import java.util.LinkedList;
import java.util.*;
/*
 * @author Alvin Mayers
 * Data Structures UA-102
 */
public class Folder{
	private String name;
	private String path;
	private Folder parent;
	private LinkedList<Folder>children=new LinkedList<Folder>();
	private Folder nextSibling;
	private Folder firstChild;
	private int count=1;

	public Folder(String name){
		this.name=name;
	}
	//observer methods
	public Folder getParent(){ 
		return parent;
	}
	public LinkedList<Folder> getChildren(){
		return children;
	}
	public String getPathName(){
		return path;
	}
	public String getName(){
		return name;
	}
	public Folder getNextSibling(){
		return nextSibling;
	}
	public int getFolderCount(){
		return count;
	}
	public void addSubFolder(String title){
			Folder folder=new Folder(title);
			if(findSubFolder(title)==null){
				folder.setParent(this);
				folder.setPath();
				children.add(folder);
				folder.subTreeLevel();
			}
			else{
				FolderViewer.printStream("duplicate name folder can't exist in same path",true);
			}
	}
	public void setParent(Folder parent){
		this.parent=parent;
	}
	private void setFirstChild(){
		if(getChildren().get(0)==null){
			firstChild=getChildren().get(0);
		}
	}
	//method not really needed, but used in development process for creating recursive methods
	private void setSiblings(){
		Folder node=getChildren().get(0);
		for(int i=0;i<getChildren().size()-1;i++){
			if(node.getNextSibling()==null){
				node.nextSibling=getChildren().get(i+1);
			}
		}	
	}
	public void listSubFolders(){
		orderList();
		Iterator search=children.iterator();
		while(search.hasNext()){
			Folder sub=(Folder)search.next();
			FolderViewer.printStream(sub.getName()+",",false);
		}
	}
	public void setPath(){
		if(getName().equals("root")){
			path="root";
		}
		else{
			path=getParent().getPathName()+"/"+getName();
		}
	}
	public Folder findSubFolder(String label){
		Iterator find=children.iterator();
		while(find.hasNext()){
			Folder found=(Folder)find.next();
			if(found.getName().equals(label)){
				return found;
			}
		}
		return null;
	}
	public void removeSubFolder(String label){
		Iterator find=children.iterator();
		while(find.hasNext()){
			Folder found=(Folder)find.next();
			if(found.getName().equals(label)){
				find.remove();
			}
		}
	}
	private void subTreeLevel(){
		int c=this.getFolderCount();
		Folder node=this.getParent();
		while(node!=null){
			node.count+=c;
			if(node.getParent()!=null){
				node=node.getParent();
				node.subTreeLevel();
			}
			else{
				break;
			}
		}
	}
	private void orderList(){
		Collections.sort(children,new SortSubFolders());
	}
	class SortSubFolders implements Comparator<Folder>{
		public int compare(Folder a, Folder b){
			if(a.getName().compareTo(b.getName())<0){
				return -1;
			}
			if(a.getName().compareTo(b.getName())>0){
				return 1;
			}
			else{
				return 0;
			}
		}
	}
	public Folder globalSearch(String label,Folder node){
		Folder found=null;
		if(node.getName().equals(label)){
			return node;
		}
		Folder [] children=new Folder[node.getChildren().size()];	
		node.getChildren().toArray(children);
		for(int i=0;found==null && i<children.length;i++){
			found=globalSearch(label,children[i]);
		}
		return found;
	}
}
