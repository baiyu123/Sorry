package mainPackage;

import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import mainPackage.Player.Pawn;

public class Game implements Serializable{
	public static final long serialVersionUID = 1;
	//0=red, 1=blue, 2=green, 3=yellow
	private int numOfPlayers;
	private int playerColor;
	private Map<String,Player> activePlayers;
	private Player human;//human player object
	private String humanColorStr; //color of human in string
	private CardDeck myDeck;//deck of card
	private Player currPlayer;
	private int selectedPawnIndex;
	boolean sorry;
	private Pawn needBumpPawn;
	private Player needBumpPlayer;
	public Vector<Point> highlightedPathVec;
	public String winner = "";
	private int originalX;
	private int originalY;
	
	public int getCurrCard(){
		return currPlayer.getCard();
	}
	public Game(int num,int color,Vector<Integer>clientsColor){
		highlightedPathVec = new Vector<Point>();
		numOfPlayers = num;
		playerColor = color;
		human = new Player(color);
		human.setHuman(true);
		myDeck = new CardDeck();
		activePlayers = new HashMap<String,Player>();
		
		switch(playerColor){
				case 0: activePlayers.put("Red", human);
						humanColorStr = "Red";
						/*activePlayers.put("Yellow",new Player(3));//yellow
						if(numOfPlayers > 2){
							activePlayers.put("Green",new Player(2));
						}
						if(numOfPlayers > 3){
							activePlayers.put("Blue",new Player(1));
						}*/
						break;
				case 1: activePlayers.put("Blue", human);
						humanColorStr = "Blue";
						/*activePlayers.put("Green",new Player(2));
						if(numOfPlayers > 2){
							activePlayers.put("Yellow",new Player(3));
						}
						if(numOfPlayers > 3){
							activePlayers.put("Red",new Player(0));
						}*/
						break;
				case 2: activePlayers.put("Green", human);
						humanColorStr = "Green";
						/*activePlayers.put("Blue",new Player(1));
						if(numOfPlayers > 2){
							activePlayers.put("Yellow",new Player(3));
						}
						if(numOfPlayers > 3){
							activePlayers.put("Red",new Player(0));
						}*/
						break;
				case 3: activePlayers.put("Yellow", human);
						humanColorStr = "Yellow";
						/*activePlayers.put("Red",new Player(0));
						if(numOfPlayers > 2){
							activePlayers.put("Green",new Player(2));
						}
						if(numOfPlayers > 3){
							activePlayers.put("Blue",new Player(1));
						}*/
			}
		
		for(int i = 0; i < clientsColor.size(); i++){
			int tempColor = clientsColor.get(i);
			switch(tempColor){
			case 0: activePlayers.put("Red", new Player(0));
					break;
			case 1: activePlayers.put("Blue", new Player(1));
					break;
			case 2: activePlayers.put("Green", new Player(2));
					break;
			case 3: activePlayers.put("Yellow", new Player(3));
					break;
			}
		}
		//set all to human
		for(Player value : activePlayers.values()){
			value.setHuman(true);
		}
		
	}
	public boolean checkSorry(){
		return sorry;
	}
	public int calculatePlayerScore(){
		int sum = 0;
		for(int i = 0; i < 4; i++){
			if(human.getPawn(i).checkHome()){
				sum += 5;
			}
		}
		for(Map.Entry<String, Player> entry: activePlayers.entrySet()){
			Player tempPlayer = entry.getValue();
			if(tempPlayer != human){
				for(int i = 0; i < 4; i++){
					Pawn tempPawn = tempPlayer.getPawn(i);
					if(!tempPawn.checkHome()){
						if(tempPawn.checkStart()){
							sum += 1;
						}
						else{
							sum += 3;
						}
					}
				}
			}
		}
		return sum;
		
	}
	
	
	public Map<String,Player> getActivePlayers(){
		return activePlayers;
	}
	
	public Player getHuman(){
		return human;
	}
	public void setBot(int color){
		String botColor = "";
		switch(color){
			case 0: botColor = "Red";
					break;
			case 1: botColor = "Blue";
					break;
			case 2: botColor = "Green";
					break;
			case 3: botColor = "Yellow";
					break;
		}
		activePlayers.get(botColor).setHuman(false);
	}
	//bot draw a card
	public void botDrawCard(){
		//iterate through each player
		for(Map.Entry<String, Player> entry: activePlayers.entrySet()){
			Player value = entry.getValue();
			if(!value.checkHuman()){
				int card = myDeck.drawCard();
				System.out.println(value.getColorStr()+card);
				value.updateCard(card);
				currPlayer = value;
				if(startingCondition()&&(card !=1 && card != 2)){//sorry
					continue;
				}
				else if(card == 1 || card == 2){
					for(int i = 0; i < 4; i++){
						if(currPlayer.getPawn(i).checkStart()){
							selectedPawnIndex = i;
							boolean valid = movePawn();
							if(!valid){
								currPlayer.getPawn(i).returnStart();
								currPlayer.updateStart();
								selectedPawnIndex--;
								movePawn();
							}
							break;
						}
					}
					
				}
				else if(card == 4){
					for(int i = 0; i < 4; i++){
						if(!startingCondition() && currPlayer.getPawn(i).checkStart()){
							selectedPawnIndex = i-1;
							movePawn();
							break;
						}
					}
				}
				//sorry
				else if(card == 13){
					int startIndex = -1;
					for(int i = 0; i < 4; i++){
						if(currPlayer.getPawn(i).checkStart()){
							startIndex = i;
							break;
						}
					}
					if(startIndex != -1){
						for(Map.Entry<String, Player> entry2: activePlayers.entrySet()){
							Player swapPlayer = entry2.getValue();
							if(swapPlayer != currPlayer){
								for(int i = 0; i < 4; i++){
									Pawn swapPawn = swapPlayer.getPawn(i);
									if(!swapPawn.checkHome()&&!swapPawn.checkSafeZone()&&!swapPawn.checkStart()){
										int x = swapPawn.getLoc()[0];
										int y = swapPawn.getLoc()[1];
										currPlayer.setAtStart(false, startIndex);
										currPlayer.setPawn(x, y, startIndex);
										swapPawn.returnStart();
										swapPlayer.updateStart();
										break;
									}
								}
							}
						}
					}
				}
				else{
					for(int i = 0; i < 4; i++){
						boolean valid = validPawnLoc(currPlayer.getPawn(i).getLoc()[0],currPlayer.getPawn(i).getLoc()[1]);
						if(valid){
							if(!currPlayer.getPawn(i).checkHome()){
								if(movePawn()){
									break;
								}
								else{
									continue;
								}
							}
						}
					}
			}
		}
		}
	}
	
	//human player draw a card
	public void humanDrawCard(){
		int card = myDeck.drawCard();
		human.updateCard(card);
		currPlayer = human;
	}
	public void updatePlayerCard(int card){
		currPlayer.updateCard(card);
	}
	public int checkEnd(){
		if(currPlayer.getNumOfHomePawn()==4){
			return currPlayer.getColor();
		}
		return -1;
	}
	//get the card of current player
	public int checkCard(){
		return currPlayer.getCard();
	}
	public void swap(int ex, int ey, int fx, int fy){
		if(needBumpPawn.checkHome()|| needBumpPawn.checkSafeZone()|| needBumpPawn.checkStart()) return;
		for(int i = 0; i < 4; i++){
			Pawn tempPawn = currPlayer.getPawn(i);
			if(tempPawn.getLoc()[0]==fx && tempPawn.getLoc()[1]==fy){
				if(tempPawn.checkHome()||tempPawn.checkSafeZone()||tempPawn.checkStart())return;
				currPlayer.setAtStart(false, i);
				currPlayer.setPawn(ex, ey, i);
				needBumpPawn.setLoc(fx, fy);
				break;
			}
		}
	}
	
	public void swapAndBump(int x, int y){//enemy
		if(needBumpPawn.checkHome()|| needBumpPawn.checkSafeZone()|| needBumpPawn.checkStart()) return;
		for(int i = 0; i < 4; i++){
			Pawn tempPawn = currPlayer.getPawn(i);
			if(tempPawn.checkStart()){
				currPlayer.setAtStart(false, i);
				currPlayer.setPawn(x, y, i);
				needBumpPawn.returnStart();
				needBumpPlayer.updateStart();
				break;
			}
		}
	}
	public boolean existEnemyPawnOnBoard(){
		for(Map.Entry<String, Player> entry: activePlayers.entrySet()){
			Player tempPlayer = entry.getValue();
			if(tempPlayer != currPlayer){
				for(int i = 0; i < 4; i++){
					Pawn tempPawn = tempPlayer.getPawn(i);
					if(!tempPawn.checkHome()&&!tempPawn.checkStart()&&!tempPawn.checkSafeZone()){
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean existHumanPawnOnBoard(){
		for(int i = 0; i < 4; i++){
			Pawn tempPawn = human.getPawn(i);
			if(!tempPawn.checkHome()&&!tempPawn.checkStart()){
				return true;
			}
		}
		return false;
	}
	public boolean HumanPawnHomeEmpty(){
		for(int i = 0; i < 4; i++){
			Pawn tempPawn = human.getPawn(i);
			if(tempPawn.checkHome()){
				return false;
			}
		}
		return true;
	}
	//check if the pawn player want to bump and replace is valid enemy pawn
	public boolean validEnemyPawnLoc(int x, int y){
		for(Map.Entry<String, Player> entry: activePlayers.entrySet()){
			Player tempPlayer = entry.getValue();
			if(tempPlayer != currPlayer){
				for(int i = 0; i < 4; i++){
					Pawn tempPawn = tempPlayer.getPawn(i);
					if(x == tempPawn.getLoc()[0] && y == tempPawn.getLoc()[1]){
						needBumpPawn = tempPawn;
						needBumpPlayer = tempPlayer;
						return true;
					}
				}
			}
		}
		return false;
	}
	//check if the pawn player want to move is a valid pawn
	public boolean validPawnLoc(int x, int y){
		
		for(int i = 0; i < 4; i++){
			Pawn tempPawn = currPlayer.getPawn(i);
			if(x == tempPawn.getLoc()[0] && y == tempPawn.getLoc()[1]){
				selectedPawnIndex = i;
				return true;
			}
		}
		return false;
	}
	private void calHighlight(int steps){
		highlightedPathVec.clear();
		int xi = currPlayer.getPawn(selectedPawnIndex).getLoc()[0];//initial position for undo if destination is not valid
		int yi = currPlayer.getPawn(selectedPawnIndex).getLoc()[1];
		int homePawn = currPlayer.getNumOfHomePawn();
		boolean atSafeZone = currPlayer.getPawn(selectedPawnIndex).checkSafeZone();
		boolean atHome = currPlayer.getPawn(selectedPawnIndex).checkHome();
		int counter = 0;
		for(int i =1; i <= steps; i++){
			if(steps == 4){
				currPlayer.movePawnBackward(1, selectedPawnIndex);
			}
			else if(steps == 14){
				currPlayer.movePawnBackward(1, selectedPawnIndex);
				break;
			}
			else if (steps == 16){
				counter++;
				currPlayer.movePawnForward(1, selectedPawnIndex);
				if(counter == 4)break;
			}
			else{
				currPlayer.movePawnForward(1, selectedPawnIndex);
			}
			int x = currPlayer.getPawn(selectedPawnIndex).getLoc()[0];
			int y = currPlayer.getPawn(selectedPawnIndex).getLoc()[1];
			highlightedPathVec.add(new Point(x,y));
		}
		//undo changes
		currPlayer.getPawn(selectedPawnIndex).setHome(atHome);
		currPlayer.setNumOfHomePawn(homePawn);
		currPlayer.setPawn(xi,yi,selectedPawnIndex);
		currPlayer.setSafeZone(selectedPawnIndex, atSafeZone);
	}
	public Vector<Point> getHightlightedPath(){
		return highlightedPathVec;
	}
	
	public boolean checkValidMove(int xin, int yin){
		int steps = currPlayer.getCard();
		boolean PawnAtStart = currPlayer.checkPawnStart(selectedPawnIndex);
		boolean PawnAtHome = currPlayer.checkPawnHome(selectedPawnIndex);
		boolean validPawn = false;
		int x = currPlayer.getPawn(selectedPawnIndex).getLoc()[0];//initial position for undo if destination is not valid
		int y = currPlayer.getPawn(selectedPawnIndex).getLoc()[1];
		int homePawn = currPlayer.getNumOfHomePawn();
		boolean atSafeZone = currPlayer.getPawn(selectedPawnIndex).checkSafeZone();
		boolean atStartBefore = currPlayer.getPawn(selectedPawnIndex).checkStart();
		boolean atHome = currPlayer.getPawn(selectedPawnIndex).checkHome();
		switch(steps){
			case 0: validPawn = true;
					break;
			case 1: validPawn = currPlayer.movePawnForward(1,selectedPawnIndex);
					break;
			case 2: validPawn = currPlayer.movePawnForward(2,selectedPawnIndex);
					break;
			case 3: validPawn = currPlayer.movePawnForward(3,selectedPawnIndex);
					break;
			case 4: validPawn = currPlayer.movePawnBackward(4, selectedPawnIndex);
					break;
			case 5: validPawn = currPlayer.movePawnForward(5, selectedPawnIndex);
					break;
			case 6: validPawn = currPlayer.movePawnForward(6, selectedPawnIndex);
					break;
			case 7: validPawn = currPlayer.movePawnForward(7, selectedPawnIndex);
					break;
			case 8: validPawn = currPlayer.movePawnForward(8, selectedPawnIndex);
					break;
			case 10: validPawn = currPlayer.movePawnForward(10, selectedPawnIndex);
					break;
			case 11: validPawn = currPlayer.movePawnForward(11, selectedPawnIndex);
					break;
			case 12: validPawn = currPlayer.movePawnForward(12, selectedPawnIndex);
					break;
			case 13:if(PawnAtStart){
						sorry = true;
						validPawn = true; 
					}
					break;
			case 14:if(!PawnAtStart&&!PawnAtHome){
					validPawn = currPlayer.movePawnBackward(1, selectedPawnIndex);
					}
					break;
			case 15:if(!PawnAtStart&&!PawnAtHome){
					validPawn = true;
					}
					break;
			case 16:validPawn = currPlayer.movePawnForward(4, selectedPawnIndex);
					validPawn = true;
					break;
			}
		boolean totalValid = false;
		if(validPawn){
			int xout = currPlayer.getPawn(selectedPawnIndex).getLoc()[0];
			int yout = currPlayer.getPawn(selectedPawnIndex).getLoc()[1];
			if(xin == xout && yin == yout)totalValid = true;
		}
		//undo changes
		currPlayer.getPawn(selectedPawnIndex).setHome(atHome);
		currPlayer.setPawn(x,y,selectedPawnIndex);
		currPlayer.setAtStart(atStartBefore, selectedPawnIndex);
		currPlayer.setSafeZone(selectedPawnIndex, atSafeZone);
		currPlayer.setNumOfHomePawn(homePawn);
		//currPlayer.setSafeZone(selectedPawnIndex, atSafeZone);
		if(validPawn) calHighlight(steps);
		return totalValid;
	}
	public void animationMove(){
		int steps = currPlayer.getCard();
		if(steps == 4||steps == 14){
			 currPlayer.movePawnBackward(1,selectedPawnIndex);
		}
		else{
			 currPlayer.movePawnForward(1,selectedPawnIndex);
		}
	}
	public Pawn getCurrPawn(){
		return currPlayer.getPawn(selectedPawnIndex);
	}
	public boolean movePawn(){
		int steps = currPlayer.getCard();
		boolean PawnAtStart = currPlayer.checkPawnStart(selectedPawnIndex);
		boolean PawnAtHome = currPlayer.checkPawnHome(selectedPawnIndex);
		boolean validPawn = false;
		int x = currPlayer.getPawn(selectedPawnIndex).getLoc()[0];//initial position for undo if destination is not valid
		int y = currPlayer.getPawn(selectedPawnIndex).getLoc()[1];
		boolean atStartBefore = currPlayer.getPawn(selectedPawnIndex).checkStart();
		switch(steps){
			case 0: validPawn = true;
					break;
			case 1: if(PawnAtStart){// pawn at start
					moveOutStartPawn();
					validPawn = true;
				}
				else if(!PawnAtHome){
					validPawn = currPlayer.movePawnForward(1,selectedPawnIndex);
				}
				break;
			case 2: if(PawnAtStart){
						moveOutStartPawn();
						validPawn = true;
					}
					else if(!PawnAtHome){
						validPawn = currPlayer.movePawnForward(2,selectedPawnIndex);
					}
				break;
			case 3: if(!PawnAtStart&&!PawnAtHome){
						validPawn = currPlayer.movePawnForward(3,selectedPawnIndex);
					}
					break;
			case 4: if(!PawnAtStart&&!PawnAtHome){
						validPawn = currPlayer.movePawnBackward(4, selectedPawnIndex);
					}
					break;
			case 5: if(!PawnAtStart&&!PawnAtHome){
						validPawn = currPlayer.movePawnForward(5, selectedPawnIndex);
					}
					break;
			case 6: if(!PawnAtStart&&!PawnAtHome){
						validPawn = currPlayer.movePawnForward(6, selectedPawnIndex);
					}
					break;
			case 7: if(!PawnAtStart&&!PawnAtHome){
						validPawn = currPlayer.movePawnForward(7, selectedPawnIndex);
					}
					break;
			case 8: if(!PawnAtStart&&!PawnAtHome){
						validPawn = currPlayer.movePawnForward(8, selectedPawnIndex);
					}
					break;
			case 10: if(!PawnAtStart&&!PawnAtHome){
						validPawn = currPlayer.movePawnForward(10, selectedPawnIndex);
					}
					break;
			case 11: if(!PawnAtStart&&!PawnAtHome){
						validPawn = currPlayer.movePawnForward(11, selectedPawnIndex);
					}
					break;
			case 12: if(!PawnAtStart&&!PawnAtHome){
						validPawn = currPlayer.movePawnForward(12, selectedPawnIndex);
					}
					break;
			case 13:if(PawnAtStart){
						sorry = true;
						validPawn = true; 
					}
					break;
			case 14:if(!PawnAtStart&&!PawnAtHome){
					validPawn = currPlayer.movePawnBackward(1, selectedPawnIndex);
					}
					break;
			case 15:if(!PawnAtStart&&!PawnAtHome){
					validPawn = true;
					}
					break;
			case 16:if(!PawnAtStart&&!PawnAtHome){//move forward 4 steps
					validPawn = currPlayer.movePawnForward(4, selectedPawnIndex);
					validPawn = true;
					}
					break;
			
		}
		if(validPawn){
			checkSlide();
			validPawn = checkDestination();//check overlap with 
		}
		if(validPawn){
			bumping();
		}
		System.out.println("step"+steps);
		System.out.println("at start"+PawnAtStart);
		System.out.println("valid"+validPawn);
		if(validPawn&&(!(steps == 1&&PawnAtStart)&&!(PawnAtStart&&steps==2))){//not valid move undo changes
			currPlayer.setPawn(x,y,selectedPawnIndex);
			currPlayer.setAtStart(atStartBefore, selectedPawnIndex);
		}
		activePlayers.put(currPlayer.getColorStr(), currPlayer);
		return validPawn;
	}
	private void bumping(){
		int x = currPlayer.getPawn(selectedPawnIndex).getLoc()[0];
		int y = currPlayer.getPawn(selectedPawnIndex).getLoc()[1];
		for(Map.Entry<String, Player>entry:activePlayers.entrySet()){
			String key = entry.getKey();
			Player value = entry.getValue();
			if(currPlayer.getColorStr()!=key){
				int lapNum = value.checkOverlap(x, y);
				if(lapNum != -1){//bump a pawn
					value.setAtStart(true,lapNum);
					value.setLocAtStart(lapNum);
				}
			}
		}
	}
	private boolean checkDestination(){
		int x = currPlayer.getPawn(selectedPawnIndex).getLoc()[0];
		int y = currPlayer.getPawn(selectedPawnIndex).getLoc()[1];
		return !currPlayer.checkOverlap(x,y,selectedPawnIndex);
	}
	private void checkSlide(){
		int []temp = currPlayer.getSlide();
		int x = currPlayer.getPawn(selectedPawnIndex).getLoc()[0];
		int y = currPlayer.getPawn(selectedPawnIndex).getLoc()[1];
		if(x == temp[0]&&y == temp[1]){
			currPlayer.movePawnForward(3, selectedPawnIndex);
		}
		else if(x == temp[4]&&y == temp[5]){
			currPlayer.movePawnForward(4, selectedPawnIndex);
		}
		
	}
	private void moveOutStartPawn(){
		switch(currPlayer.getColor()){
		case 0: currPlayer.movePawnDown(selectedPawnIndex);
				break;
		case 1: currPlayer.movePawnLeft(selectedPawnIndex);
				break;
		case 2: currPlayer.movePawnRight(selectedPawnIndex);
				break;
		case 3: currPlayer.movePawnUp(selectedPawnIndex);
				break;
		}
	}
	public void setHuman(int hcolor){
		//if(currPlayer != null) currPlayer.setHuman(false);
		switch(hcolor){
			case 0: currPlayer = activePlayers.get("Red");
					human = currPlayer;
					break;
			case 1: currPlayer = activePlayers.get("Blue");
					human = currPlayer;
					break;
			case 2: currPlayer = activePlayers.get("Green");
					human = currPlayer;
					break;
			case 3: currPlayer = activePlayers.get("Yellow");
					human = currPlayer;
					break;
		}
		human.setHuman(true);
	}
	//all pawn at start for curr player
	public boolean startingCondition(){
		int start = currPlayer.getNumOfStartPawn();
		int home = currPlayer.getNumOfHomePawn();
		return start+home == 4;
	}
	
}
