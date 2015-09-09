/**
 * @author Alvin Mayers
 * Course: Data Structures UA-102
 * Programming assignment #2 
 */
import java.util.Iterator;
import java.util.LinkedList;

public class Person{
	private String name;
	private int numFriends;
	private LinkedList<Person> friends=new LinkedList<Person>();
	//thread checks value before proceeding
	private boolean removed=false; 

	public Person(String name){
		this.name=name;
	}
	//non static inner class
	//searches list in forward direction and removes target element
	class ForwardRemove implements Runnable{
		Iterator<Person> forwardIterate=getFriendList().iterator();
		Person forward;
		public ForwardRemove(Person annihalate){
			forward=annihalate;
		}
		public void run(){
			removed=false;
			while(!removed){
				while(forwardIterate.hasNext()){
					if(forward==forwardIterate.next()){
						forwardIterate.remove();
						removed=true;
											}
				}
			}
		}
	}
	//non static inner class
	//searches list in backward direction and removes target element
	class BackwardRemove implements Runnable{
		Iterator<Person> backwardIterate=getFriendList().descendingIterator();
		Person backward;
		public BackwardRemove(Person annihalate){
			backward=annihalate;
		}
		public void run(){
			while(!removed){
				while(backwardIterate.hasNext()){
					if(backward==backwardIterate.next()){
							backwardIterate.remove();
							removed=true;
					}
				}
			}
		}
	}
	//returns person's name
	public String getName(){
		return name;
	}
	//returns first element of LinkedList
	public LinkedList<Person> getFriendList(){
		return friends;
	}
	//formats list of friends from linkedList of friends
	//then returns it to listBuddies method in HashMicroFB
	public String listFriends(){
		String ofNames="";
		Iterator<Person> findFriends=getFriendList().iterator();
			while(findFriends.hasNext()){
				Person friend=(Person)findFriends.next();
				ofNames+=String.format("%s\n",friend.getName());
			}
		return ofNames;
	}
	//add friend to both this and other person linkedList of friends
	public void addFriends(Person q){
		this.getFriendList().addFirst(q);
		q.getFriendList().addFirst(this);
	}
	//checks if this and the other person by iterating through linkedList
	public boolean FriendsOrNot(Person q){
		Iterator<Person> check=getFriendList().iterator();
		while(check.hasNext()){
			Person inQuestion=check.next();
			if(inQuestion==q){
				return true;
			}
		}
		return false;
	}
	public int getFriendInList(Person q){
		return this.getFriendList().indexOf(q);
	}
	//instantiates two threads
	//once which runs an instantation of the innerclass BackwardRemove
	//and the other which runs the instantiation of the innerclass ForwardRemove
	public void removeFriend(Person q){
			//remove q from this linked list of friends
			// inner class by default can access members of outer class
			//create inner class objects from Person class
			ForwardRemove fsearch=new ForwardRemove(q);
			BackwardRemove bsearch=new BackwardRemove(q);
			//put each in there own thread to run concurrently
			//forward search and remove
			Thread fThisRemove=new Thread(fsearch);
			fThisRemove.start();
			//backwards search and remove
			Thread bThisRemove=new Thread(bsearch);
			//delay so both won't reach middle element at the same time
			bThisRemove.start();
			//remove this from q linked List of Friends
			ForwardRemove qfsearch=q.new ForwardRemove(this);
			BackwardRemove qbsearch=q.new BackwardRemove(this);
			//create new threads
			Thread fQRemove=new Thread(qfsearch);
			Thread bQRemove=new Thread(qbsearch);
			//start simultaneously
			fQRemove.start();
			bQRemove.start();
	}	
}
