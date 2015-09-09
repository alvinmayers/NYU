/**
 * @author Alvin Mayers
 * Course: Data Structures UA-102
 * Programming assignment #2 
 */
public class NodePointer{
	private Person firstmember;
	private Person secondmember;
	
	public NodePointer(Person a, Person b){
		linkMembers(a,b);
	}
	public void linkMembers(Person a, Person b){
		a.addFriends(b);
		//sets data fields equal to people through their linkedLists
		//casting necessary due to return type of specified method in API
		//casting is also used in Person class and MicroFB2 class
		//firstmember=(Person)b.getFriendList().get(b.getFriendList().indexOf(b.getFriendInList(a)));
		//secondmember=(Person)a.getFriendList().get(a.getFriendList().indexOf(a.getFriendInList(b)));
		firstmember=a;
		secondmember=b;
	}
	public void unLinkMembers(){
		firstmember.removeFriend(secondmember);
		firstmember=null;
		secondmember=null;
	}
}
