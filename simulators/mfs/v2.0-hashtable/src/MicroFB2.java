/**
 * @author Alvin Mayers
 * Course: Data Structures UA-102
 * Programming assignment #2 
 */
//explicit imports easier for debugging
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Scanner;

public class MicroFB2{
	private static Hashtable<String,Person>AllPeople=new Hashtable<String,Person>();	
	//Hashtable as required for normal assignment
	private static Hashtable<String,Boolean>AllFriends=new Hashtable<String,Boolean>();
	//Hashtable as required for honors exercise
	private static Hashtable<String,NodePointer>HonorAllFriends=new Hashtable<String,NodePointer>();

	//getter methods return Map.Entry which contains the key and value accessible by getKey() and getValue
	public static Set<Map.Entry<String,Person>> getHashTable(){
		return AllPeople.entrySet();
	}
	public static Set<Map.Entry<String,Boolean>> getAllFriends(){
		return AllFriends.entrySet();
	}
	public static Set<Map.Entry<String,NodePointer>> getHonorAllFriends(){
		return HonorAllFriends.entrySet();
	}
	public static Person findPerson(String name){
		Iterator<Map.Entry<String,Person>>search=getHashTable().iterator();
		//search is the iterator
		while(search.hasNext()){
			//entry is within iterator
			//extract entry which consists of key-value pair
			Entry entry=(Entry) search.next();
			if(entry.getKey().equals(name)){
				return (Person)entry.getValue();
			}
		}
		return null;
	}
	public static void addPerson(String name){
		if(findPerson(name)==null){
			Person member=new Person(name);
			AllPeople.put(name,member);//add person to AllPeople Hashtable
			String status=String.format("%s added to MicroFB2",name);
			RouteMessage.displayStatus(status);
		}
		else if(findPerson(name)!=null){
			RouteMessage.displayWarning("cannot add duplicate person");
		}
	}
	//checks if two person arguments for corresponding commands are valid
	//relays notification to user
	public static boolean partyValidator(Person a, Person b){
		String warning;
		String oneparty="First party is not a member of MicroFB2";
		String secondparty="Second party is not a member of MicroFB2";
		String bothparties="Both parties are not members of MicroFB2";
		if(a==null & b==null){
			warning=bothparties;
		}
		else if(b==null){
			warning=secondparty;
		}
		else{
			warning=(a==null) ? oneparty : "";
		}
		if(warning.equals("")){
			return true;
		}
		else{
			RouteMessage.displayWarning(warning);
		}
		return false;
	}
	public static void friendPeople(String a, String b){
		Person A=findPerson(a);
		Person B=findPerson(b);
		if(A==B){
			String friendMyself=String.format("you cannot friend yourself");
			RouteMessage.displayWarning(friendMyself);
		}
		else if(!A.FriendsOrNot(B)){
			if(A!=null & B!=null){
				//modifies AllFriends Hashtable accordingly
				TraverseAllFriends.addFriendPair(a,b);
				//modifies HonorsAllFriends Hashtable
				HonorsHashUpdate(A,B);
				String status=String.format("%s and %s are now friends",a,b);
				RouteMessage.displayStatus(status);
			}
			else{
				partyValidator(A,B);
			}
		}
		else if(!A.FriendsOrNot(B)){
			String alreadyfriends=String.format("%s,%s are already friends",A.getName(),B.getName());
			RouteMessage.displayWarning(alreadyfriends);
		}
		
	}
	//inner class specifically meant to service AllFriends
	//some methods are also called again for HonorsAllFriend
	static class TraverseAllFriends{
		//add key and value
		static void addFriendPair(String a, String b){
			AllFriends.put(createKey(a,b),true);	
		}
		//remove key and value
		static void deleteFriendPair(String a, String b){
			Iterator<Map.Entry<String,Boolean>>kill=getAllFriends().iterator();	
			while(kill.hasNext()){
				Entry pair=(Entry)kill.next();
				if(readKey((String)pair.getKey(),a,b)){
					kill.remove();
				}
				if(readKey((String)pair.getKey(),b,a)){
					kill.remove();
				}
			}
		}
		//create and return key in specified format
		static String createKey(String a, String b){
			String generatedKey=String.format("%s*%s",a,b);
			return generatedKey;
		}
		//extract names from key and returns boolean if names
		//are found in string passed through argument
		static boolean readKey(String read, String a, String b){
			String firstSegment;
			String secondSegment;
			firstSegment=read.substring(0,read.indexOf('*'));
			secondSegment=read.substring(read.indexOf('*')+1,read.length());
			return (firstSegment.equals(a)) & (secondSegment.equals(b));
		}
		//returns true if two names are found in string
		static boolean lookUpKey(String a, String b){
			boolean keyfound=false;
			Iterator<Map.Entry<String,Boolean>>search=getAllFriends().iterator();	
			while(search.hasNext()){
				Entry pair=(Entry)search.next();
				//both if statements take into account order of names
				//in the AllFriends Hashtable
				if(readKey((String)pair.getKey(),a,b)){
					keyfound=true;
				}
				if(readKey((String)pair.getKey(),b,a)){
					keyfound=true;
				}
			}
			return keyfound;
		}
	}
	/*through this method it calls HonorsAllFriends Hashtable
	then it uses the NodePointer object to call related methods in Person class
	to complete commands*/
	public static void HonorsHashUpdate(Person a, Person b){
		//checks method that calls this one in order to choose the right
		//course of action
		String method=Thread.currentThread().getStackTrace()[2].getMethodName();
		/*creates new Node, generates a key and then puts String name (key) and NodePointer(value)
		into Hashtable*/
		if(method.equals("friendPeople")){
			/*the NodePointer constructor calls the method
			linkMembers in NodePointer, which in turn calls
			the addFriends method from the Person class*/
			NodePointer newlink=new NodePointer(a,b);
			HonorAllFriends.put((TraverseAllFriends.createKey(a.getName(),b.getName())),newlink);

		}
		else if(method.equals("unFriend")){
			Iterator<Map.Entry<String,NodePointer>> find=getHonorAllFriends().iterator();
			while(find.hasNext()){
				Entry honorfriend=(Entry)find.next();
				String honorkey=(String)honorfriend.getKey();
				//again this if statement accounts for key string order
				if(TraverseAllFriends.readKey(honorkey,a.getName(),b.getName())
					| TraverseAllFriends.readKey(honorkey,b.getName(),a.getName())){
					NodePointer value=(NodePointer)honorfriend.getValue();
					//NodePointer method unlinkMembers method calls removeFriend method
					//in the Person class
					value.unLinkMembers();
					find.remove();
				}
			}
		}
	}
	public static void listBuddies(String x){
		Person person=findPerson(x);
		if(person!=null){
			RouteMessage.listNames(person.listFriends());
		}
		else{
			RouteMessage.displayWarning("Person does not exist");
		}
	}
	/*checkFriends method searches through Hashtable AllFriends
	 * to verify if two people are friends
	 */
	public static void checkFriends(String a, String b){
		Person person=findPerson(a);
		Person friend=findPerson(b);
		if(partyValidator(person,friend)){
			String yes="Yes they are friends";
			String no="No, they are not friends";
			if(TraverseAllFriends.lookUpKey(a,b)){
				RouteMessage.friendConfirmation(yes);
			}
			else{
				String answer=(person.FriendsOrNot(friend))? yes : no;
				String failure="searching Hashtable yielded negative results reverted to linkList search\n";
				RouteMessage.friendConfirmation(answer);
				RouteMessage.displayWarning(failure);
			}
		}
	}
	public static void unFriend(String a, String b){
		Person A=findPerson(a);
		Person B=findPerson(b);
		if(A!=null & B!=null){
			if(A.FriendsOrNot(B)){
				//refer to methods for description
				TraverseAllFriends.deleteFriendPair(a,b);
				HonorsHashUpdate(A,B);
				String status=String.format("%s and %s are no longer friends",a,b);
				RouteMessage.displayStatus(status);
			}
			else{
				RouteMessage.displayWarning("cannot unfriend people who are not friends");
			}
		}
		else{
			partyValidator(A,B);
		}
	}
	//inner class takes care of all messages displayed in terminal
	static class RouteMessage{
		//each method displays a String
		//just easier to identify the purpose of the message in code
		static void displayWarning(String warning){
			System.out.printf("%s\n",warning);
		
		}
		static void listNames(String message){
			System.out.printf("%s\n",message);
		}
		
		static void friendConfirmation(String booleanAnswer){
			System.out.printf("%s\n",booleanAnswer);
		}
		static void displayStatus(String status){
			System.out.printf("%s\n",status);
		}
	}
	public static void main(String [] args){
	//similar code taken from my MicroFB class of first programming assignment
	Scanner input=new Scanner(System.in);
		while(true){ //keep program running until "X" command is issued
			System.out.printf("Enter a command: ");
			String command=input.next();
			switch(command){//switch able to take String arguments in java 7
				case "P": 
					String pInput=input.next();//user input argument
					addPerson(pInput);
					break;
				case "F":
					String fInput=input.next();
					String f2Input=input.next();
					friendPeople(fInput,f2Input);
					break;
				case "L":
					String lInput=input.next();
					listBuddies(lInput);	
					break;
				case "Q":
					String qInput=input.next();
					String q2Input=input.next();
					checkFriends(qInput,q2Input);
					break;
				case "U":
					String uInput=input.next();
					String u2Input=input.next();		
					unFriend(uInput,u2Input);
					break;
				case "X":
					System.exit(0);//exit program
				default:
					System.out.printf("invalid command, try again or type \"X\" to quit\n");
			}	
		}
	}
	
}

