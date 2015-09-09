import java.util.Scanner;
/*
 * @author Alvin Mayers
 * Data Structures UA-102
 */
public class FolderViewer{
	private static Folder root=new Folder("root");
	private static Folder currentDirectory=root;

	public static void createSubDirectory(String name){	
		currentDirectory.addSubFolder(name);
	}
	public static void moveUpDirectory(){
		currentDirectory=currentDirectory.getParent();
	}
	public static void moveDownDirectory(String targetSub){
		Folder node=currentDirectory.findSubFolder(targetSub);	
		if(node!=null){
			currentDirectory=node;
		}
		else{
			printStream("cannot cd into nonexistant directory",true);
		}
	}
	public static void listSubDirectories(){
		currentDirectory.listSubFolders();	
	}
	public static void removeSubDirectory(String name){
		currentDirectory.removeSubFolder(name);
	}
	public static void displayPath(){
		printStream(currentDirectory.getPathName(),true);
	}
	public static void countSubDirectories(){
		printStream(Integer.toString(currentDirectory.getFolderCount()),true);
	}
	public static void printStream(String output,boolean nextLine){
		if(nextLine){
			System.out.println(output);
		}
		else{
			System.out.print(output);
		}
	}
	//for the A command
	public static void absolutePathSearch(String start,String path){
		//acquire the first "root" working folder by globalsearch
		Folder a=root.globalSearch(start,root);
		if(a!=null){
			currentDirectory=a;
		}
		//delimiting character in string to separate multiple arguments
		String separate="\\ ";
		String[] pathargs=path.split(separate);
		System.out.println(pathargs[0]);
		//iterate throw array of folder names to traverse through subdirectories
		for(String folder : pathargs){
			if(currentDirectory.findSubFolder(folder)!=null){
				currentDirectory=currentDirectory.findSubFolder(folder);
			}
			else{
				printStream("the parent directory selected does not contain target",true);
				break;
			}
		}
	}
	//for The G command
	public static void relativePathSearch(String folders){
		Folder b=root.globalSearch(folders,root);
		if(b!=null){
			currentDirectory=b;
		}
		if(currentDirectory.getParent()==null){
			printStream("The Up specifier cannot be used target directory does not have parent",true);
		}
		else if(currentDirectory.getParent()!=null){
			String separate="\\ ";
			String[] pathargs=folders.split(separate);
			for(int i=1;i<pathargs.length-1;i++){
				if(pathargs.length>1){
					if(currentDirectory.findSubFolder(pathargs[i])!=null){
						currentDirectory=currentDirectory.findSubFolder(pathargs[i]);
					}
					else{
						printStream("complete path not valid",true);
						printStream("use P command to verify current path",true);
					}
				}
			}
		}
	}
	public static boolean commandLimiter(String command){
		String [] proper={"A","C","D","G","U","R","L","P","N","X"};
		for(String arg: proper){
			if(command.equals(arg)){
				return true;
			}
		}
		return false;
	}
	public static boolean argumentLimiter(String command){
		String [] twoArgs={"C","D","R"};
		for(String arg: twoArgs){
			if(command.equals(arg)){
				return true;
			}
		}
		return false;
	}
	public static boolean doubleArgument(String command){
		String [] threeArgs={"A","G"};
		for(String arg: threeArgs){
			if(command.equals(arg)){
				return true;
			}
		}
		return false;
	}
	public static void main(String[] args){
		root.setPath();
		Scanner input=new Scanner(System.in);
		boolean running=true;
		while(running){
			printStream("\nEnter Command:___\b\b",false);
			String command=input.next().toUpperCase();
			String argument="";
			String argumentB="";
			if(!commandLimiter(command)){
				printStream("command not valid",false);
				continue;	
			}
			else{
				if(argumentLimiter(command)){
					printStream("Enter argument:______\b\b\b\b\b",false);
					argument=input.next();
				}
				if(doubleArgument(command)){
					printStream("Enter first argument________\b\b\b\b\b\b\b",false);
					argument=input.next();
					printStream("For other arguments separate by a space",true);
					printStream("Enter extra arguments______________\b\b\b\b\b\b\b\b\b\b\b\b\b",false);
					argumentB=input.next();
				}
			}
			switch(command){
				case "C":
					createSubDirectory(argument);
					break;
				case "D":
					moveDownDirectory(argument);
					break;
				case "U":
					moveUpDirectory();	
					break;
				case "R":
					removeSubDirectory(argument);
					break;
				case "L":
					listSubDirectories();
					break;
				case "P":
					displayPath();
					break;
				case "N":
					countSubDirectories();
					break;
				case "G":
					String s="up";
					String S=s.toUpperCase();
					if(!argument.equals(s) || !argument.equals(S)){
						break;
					}
					relativePathSearch(argumentB);
					break;
				case "A":
					absolutePathSearch(argument,argumentB);
					break;
				case "X":
					running=false;		
					break;
			}
		}
	}
}
