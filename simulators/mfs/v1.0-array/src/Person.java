/*
*@author Alvin Mayers
*/
public class Person{
	private static int maxFriends=10;
	private String name;
	private int numFriends;
	private Person [] friends=new Person[maxFriends];

	public Person(String n){setName(n);}
	public String getName(){ return name;}
	public int getNumFriends(){ return numFriends;}
	public Person [] getFriends(){ return friends;}

	public void setName(String name){
		this.name=name;
	}
	public void addFriend(Person q){//add friend to this
		if(this!=null & q!=null){
			friends[numFriends]=q;
			numFriends++;
			int qNumFriends=q.getNumFriends();
			q.getFriends()[qNumFriends]=this;//check q's friends for myself(this)
			q.numFriends++;//increment q's n# of friends counter
		}
	}
	public boolean checkNumFriends(){
		if(numFriends<maxFriends){
			return numFriends<maxFriends;
		}
		else{
			System.out.printf("%s has the maximum number of friends already\n",this.getName());
			return false;
		}
	}	
	public boolean checkFriend(Person q){ //check if this and Person q are friends
	boolean a=false;
		try{
			for(Person friend: friends){ //iterate through this.friends
				if(friend==q){	
					for(Person qfriend : q.getFriends()){
						if(qfriend==this){
							a=true;//evaluate to true if Person q is found
						}
					}
				}
			}
		}catch(NullPointerException npe){}
		return a; //return boolean value
	}
	public void unFriend(Person q){//unFriend this and q
		for(int i=0;i<friends.length;i++){
			if(friends[i]==q){
				for(int j=0;j<q.getFriends().length;j++){
					if(q.getFriends()[j]==this){ //find this in q friend array
						q.getFriends()[j]=null;//set this in array to null
						q.getFriends()[j]=q.getFriends()[j+1];//slide elements down array
						break; //leave for loop to save time and memory
					}
				}
				friends[i]=null;//set q in this array to null
				friends[i]=friends[i+1]; //slide elements down the array	
				break;
			}
		}
	}
}
