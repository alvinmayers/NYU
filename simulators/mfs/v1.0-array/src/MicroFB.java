/*
*@author Alvin Mayers
*@course Data Structures UA-102
*/
import java.util.Scanner;
public class MicroFB{
	protected static int maxPeople=100; 
	protected static Person [] allPeople=new Person[maxPeople];
	protected static int peopleCount=0; //# of current people
	
	public static Person findPerson(String name){//search global array for Person with name match user input 
		try{
			for(Person person : allPeople){ //for each loop iterate through global array
				if(name.equals(person.getName())){ //if Person with matching string is found 
					return person;
				}
			}
		}catch(NullPointerException npe){//avoid compiler error and print error message
			String methodCall=Thread.currentThread().getStackTrace()[2].getMethodName();//apply proper message based on method that called findPerson
			if(!methodCall.equals("pCommand")){
				System.out.printf("%s is not a member of MicroFB\n",name);
			}
			else{
				System.out.printf("%s has just been added to MicroFB\n",name);
			}
		}
		return null; 
	}
	public static boolean checkPeopleCount(){//check if total people is less than 100
		return peopleCount<maxPeople;
	}
	public static void pCommand(String p){ //create a new person
		if(checkPeopleCount()){
			if(findPerson(p)!=null){ //does not create user if name already in array
				System.out.println("user already a member of MicroFB");
			}
			else{
				Person person=new Person(p);//new instantiation of person object
				if(checkPeopleCount()){
					allPeople[peopleCount]=person;//add new person to global array
					peopleCount++;//increment peopleCount to reflect added non-null members
				}
			}
		}
		else{
			System.out.println("Sorry,MicroFB cannot accept new users at this time, as our maximum capacity has been reached\n");
		}
	}
	public static void fCommand(String friendName1,String friendName2){ //friend two people	
		if((findPerson(friendName1)!=null) & (findPerson(friendName2)!=null)){//check if both people are members
			Person a=findPerson(friendName1);//did not use findPerson in if statement to avoid printing duplicated messages
			Person b=findPerson(friendName2);
			if(a.getName().equals(b.getName())){
				System.out.printf("A user cannot friend themselves.\n");
			}
			else if(a.checkNumFriends() & b.checkNumFriends()){//check if both parties have less the ten friends
				findPerson(friendName1).addFriend(findPerson(friendName2));//pair people as friends.
			}
		}
	}
	public static void lCommand(String l)throws NullPointerException{//lists all friends of a person who name matches String argument 
		try{
			Person q=findPerson(l);
			for(int i=0;i<q.getNumFriends();i++){
				if(q.getFriends()[i]!=null){
					System.out.printf(" %s\n",q.getFriends()[i].getName());
				}
			}
		}catch(NullPointerException npe){
			System.out.println("You cannot list friends of person who is not a member of MicroFB");
		}
	}
	public static void qCommand(String firstPerson,String secondPerson){ //print status between two members
		if((findPerson(firstPerson)!=null) & (findPerson(secondPerson)!=null)){//check if both people are members
			if(findPerson(firstPerson).checkFriend(findPerson(secondPerson))){//evaluate true if two people are friends
				System.out.println("yes");
			}
			else{
				System.out.println("no");
			}
		}
	}
	public static void uCommand(String firstPerson, String secondPerson){ //unfriend two people
		int sIndex=0;
		int tIndex=0;
		for(int i=0;i<peopleCount;i++){
			if(allPeople[i]==findPerson(firstPerson)){
				sIndex=i;//index value in array locating person with String name of firstPerson
			}
			if(allPeople[i]==findPerson(secondPerson)){
				tIndex=i;

			}
		}
		allPeople[sIndex].unFriend(allPeople[tIndex]);//call unFriend method from person class through global array
	}	
	public static void main(String[] args){
		Scanner input=new Scanner(System.in);
		while(true){ //keep program running until "X" command is issued
			System.out.printf("Enter a command: ");
			String command=input.next();
			switch(command){//switch able to take String arguments in java 7
				case "P": 
					String pInput=input.next();//user input argument
					pCommand(pInput);
					break;
				case "F":
					String fInput=input.next();
					String f2Input=input.next();
					fCommand(fInput,f2Input);
					break;
				case "L":
					String lInput=input.next();
					lCommand(lInput);	
					break;
				case "Q":
					String qInput=input.next();
					String q2Input=input.next();
					qCommand(qInput,q2Input);
					break;
				case "U":
					String uInput=input.next();
					String u2Input=input.next();		
					uCommand(uInput,u2Input);
					break;
				case "X":
					System.exit(0);//exit program
				default:
					System.out.printf("invalid command, try again or type \"X\" to quit\n");
			}	
		}
	}
}				
