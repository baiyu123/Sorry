package mainPackage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CardDeck implements Serializable{
	public static final long serialVersionUID = 1;
	private Map<Integer,Integer> myDeck;
	private int numOfCard;
	CardDeck(){
		myDeck = new HashMap<Integer,Integer>();
		reShuffle();
	}
	private void reShuffle(){
		myDeck.clear();
		for(int i = 1; i < 14; i++){
			if(i !=6 && i != 9){
				myDeck.put(i,4);
			}
		}
		numOfCard = 44;
	}
	
	public int drawCard(){
		Random rand = new Random();
		int randomInt = 0;
		randomInt = 1 + rand.nextInt(13);
		while(randomInt == 6 || randomInt == 9){
			randomInt = 1+rand.nextInt(13);
		}
		if(numOfCard == 0){
			reShuffle();
		}
		int temp = myDeck.get(randomInt);
		if(temp !=0){
			myDeck.put(randomInt, temp-1);
			numOfCard--;
			return randomInt;
		}
		else{
			return drawCard();
		}
	}
}
