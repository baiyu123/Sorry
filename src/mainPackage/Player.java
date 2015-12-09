package mainPackage;

import java.io.Serializable;
import java.util.Vector;


public class Player implements Serializable{
	public static final long serialVersionUID = 1;
	//0=red, 1=blue, 2=green, 3=yellow
	private int color;
	private int numOfStartPawn;
	private int numOfHomePawn;
	private int currentCard;
	private Pawn[] myPawns;
	private Vector<Pawn> activePawns;
	private int[] slide;//start: x1,y1,end:x2,y2 start: x3,y3 end:x4,y4
	private int[] safeZone;
	private String colorStr;
	private boolean human;
	
	public Player(int c){
		human = true;
		color = c;
		numOfStartPawn = 4;
		numOfHomePawn = 0;
		myPawns = new Pawn[4];
		for(int i =0; i < 4; i++){
			myPawns[i] = new Pawn(color);
		}
		//setup slide
		slide = new int[8];
		safeZone = new int[2];
		if(color == 0){
			colorStr = "Red";
			slide[0] = 14;
			slide[1] = 15;
			slide[2] = 11;
			slide[3] = 15;
			slide[4] = 6;
			slide[5] = 15;
			slide[6] = 2;
			slide[7] = 15;
			safeZone[0] = 13;
			safeZone[1] = 15;
		}
		else if(color == 1){
			colorStr = "Blue";
			slide[0] = 0;
			slide[1] = 14;
			slide[2] = 0;
			slide[3] = 11;
			slide[4] = 0;
			slide[5] = 6;
			slide[6] = 0;
			slide[7] = 2;
			safeZone[0] = 0;
			safeZone[1] = 13;
		}
		else if(color == 2){
			colorStr = "Green";
			slide[0] = 15;
			slide[1] = 1;
			slide[2] = 15;
			slide[3] = 4;
			slide[4] = 15;
			slide[5] = 9;
			slide[6] = 15;
			slide[7] = 13;
			safeZone[0] = 15;
			safeZone[1] = 2;
		}
		else{
			colorStr = "Yellow";
			slide[0] = 1;
			slide[1] = 0;
			slide[2] = 4;
			slide[3] = 0;
			slide[4] = 9;
			slide[5] = 0;
			slide[6] = 13;
			slide[7] = 0;
			safeZone[0] = 2;
			safeZone[1] = 0;
		}
	}
	public void setNumOfHomePawn(int val){
		numOfHomePawn = val;
	}
	public boolean checkHuman(){
		return human;
	}
	public void setHuman(boolean isHuman){
		human = isHuman;
		for(int i = 0; i < 4; i++){
			myPawns[i].setHuman(true);
		}
	}
	public String getColorStr(){
		return colorStr;
	}
	public void setSafeZone(int index, boolean inSafe){
		myPawns[index].setSafeZone(inSafe);
	}
	public int checkOverlap(int x, int y){//check if pawn is overlap with different user's pawn
		boolean overlap = false;
		int lapNum = -1;
		for(int i = 0; i < 4; i++){
			int pawnx = myPawns[i].getLoc()[0];
			int pawny = myPawns[i].getLoc()[1];
			if(x == pawnx && y == pawny){
				overlap = true;
				lapNum = i;
			}
		}
		return lapNum;
	}
	public boolean checkOverlap(int x, int y, int index){//check if pawn is overlap with same user's pawn
		boolean overlap = false;
		for(int i = 0; i < 4; i++){
			if(i == index)continue;
			if(myPawns[i].checkHome()) continue;
			int pawnx = myPawns[i].getLoc()[0];
			int pawny = myPawns[i].getLoc()[1];
			if(x == pawnx && y == pawny)overlap = true;
		}
		return overlap;
	}
	
	public int[] getSlide(){
		return slide;
	}
	public void setPawn(int x, int y, int index){
		myPawns[index].setLoc(x, y);
	}
	
	public int getColor(){
		return color;
	}
	public void setAtStart(boolean start, int index){
		if(!myPawns[index].atStart && start){//set start to true
			myPawns[index].setStart(true);
			numOfStartPawn++;
		}
		else if(myPawns[index].atStart&&!start){
			myPawns[index].setStart(false);
			numOfStartPawn--;
		}
	}
	public void setLocAtStart(int index){
			myPawns[index].returnStart();
	}
	public int getNumOfStartPawn(){
		return numOfStartPawn;
	}
	public int getNumOfHomePawn(){
		return numOfHomePawn;
	}
	public void updateCard(int card){
		currentCard = card;
	}
	public int getCard(){
		return currentCard;
	}
	public Pawn getPawn(int index){
		return myPawns[index];
	}
	public boolean checkPawnStart(int index){
		return myPawns[index].checkStart();
	}
	public boolean checkPawnHome(int index){
		return myPawns[index].checkHome();
	}
	public void movePawnRight(int index){
		int x = myPawns[index].getLoc()[0];
		int y = myPawns[index].getLoc()[1];
		myPawns[index].setLoc(++x, y);
		numOfStartPawn--;
		myPawns[index].setStart(false);
	}
	public void movePawnDown(int index){
		int x = myPawns[index].getLoc()[0];
		int y = myPawns[index].getLoc()[1];
		myPawns[index].setLoc(x, ++y);
		numOfStartPawn--;
		myPawns[index].setStart(false);
	}
	public void movePawnUp(int index){
		int x = myPawns[index].getLoc()[0];
		int y = myPawns[index].getLoc()[1];
		myPawns[index].setLoc(x, --y);
		numOfStartPawn--;
		myPawns[index].setStart(false);
	}
	public void movePawnLeft(int index){
		int x = myPawns[index].getLoc()[0];
		int y = myPawns[index].getLoc()[1];
		myPawns[index].setLoc(--x, y);
		numOfStartPawn--;
		myPawns[index].setStart(false);
	}
	public boolean movePawnForward(int steps,int index){
		int x = myPawns[index].getLoc()[0];
		int y = myPawns[index].getLoc()[1];
		if((x == 2 && y != 15 && y != 0)||(y == 2 && x != 0 && x != 15)||(y == 13 && x != 15 && x != 0)||(x==13 && y != 0 && y != 15) ){
			myPawns[index].setSafeZone(true);
		}
		else{
			myPawns[index].setSafeZone(false);
		}
		boolean home = false;
		int counter = steps;
		while(counter > 0){
			if(x == safeZone[0] && y == safeZone[1]){
				myPawns[index].setSafeZone(true);
			}
			if(myPawns[index].atSafeZone == true){
				if(color == 0){
					y--;
					if(y == 9){
						home = true;
						break;
					}
				}
				else if(color == 1){
					x++;
					if(x==6){
						home = true;
						break;
					}
				}
				else if(color == 2){
					x--;
					if(x == 9){
						home = true;
						break;
					}
				}
				else if(color == 3){
					y++;
					if(y == 6){
						home = true;
						break;
					}
				}
			}
			if(!myPawns[index].checkSafeZone()){
				if(y == 0 && x < 15){
					x++;
				}
				else if(x ==15 && y < 15){
					y++;
				}
				else if(y == 15 && x > 0){
					x--;
				}
				else if(x == 0 && y > 0){
					y--;
				}
			}
			counter--;
			boolean stuck = false;
			for(int i = 0; i < 4; i++){
				int x1 = myPawns[i].getLoc()[0];
				int y1 = myPawns[i].getLoc()[1];
				if(x==x1&&y==y1){
					stuck = true;
				}
			}
			if(stuck)return false;//not valid move
		}
		myPawns[index].setLoc(x, y);
		if(home){
			numOfHomePawn++;
			myPawns[index].setHome(true);
		}
		return true;
	}
	public boolean movePawnBackward(int steps,int index){
		int x = myPawns[index].getLoc()[0];
		int y = myPawns[index].getLoc()[1];
		int counter = steps;
		while(counter > 0){
			if(myPawns[index].atSafeZone == true){
				if(color == 0){
					y++;
					if(y == 15){
						myPawns[index].atSafeZone=false;
						break;
					}
				}
				else if(color == 1){
					x--;
					if(x == 0){
						myPawns[index].atSafeZone=false;
						break;
					}
				}
				else if(color == 2){
					x++;
					if(x == 15){
						myPawns[index].atSafeZone=false;
						break;
					}
				}
				else if(color == 3){
					y--;
					if(y == 0){
						myPawns[index].atSafeZone=false;
						break;
					}
				}
			}
			
			if(y == 0 && x > 0){
				x--;
			}
			else if(x ==15 && y > 0){
				y--;
			}
			else if(y == 15 && x < 15){
				x++;
			}
			else if(x == 0 && y < 15){
				y++;
			}
			counter--;
			boolean stuck = false;
			for(int i = 0; i < 4; i++){
				int x1 = myPawns[i].getLoc()[0];
				int y1 = myPawns[i].getLoc()[1];
				if(x==x1&&y==y1){
					stuck = true;
				}
			}
			if(stuck)return false;//not valid move
		}
		myPawns[index].setLoc(x, y);
		return true;
	}
	public void updateStart(){
		int counter = 0;
		for(int i = 0; i < 4; i++){
			if(myPawns[i].checkStart()) counter++;
		}
		numOfStartPawn = counter;
	}
	
	public class Pawn implements Serializable{
		public static final long serialVersionUID = 1;
		private int pColor;
		private int x;
		private int y;
		private boolean atStart;
		private boolean atSafeZone;
		private boolean atHome;
		private boolean human;
		Pawn(int PawnColor){
			human = false;
			pColor = PawnColor;
			switch(PawnColor){
				case 0: x = 11;
						y = 14;
						break;
				case 1: x = 1;
						y = 11;
						break;
				case 2: x = 14;
						y = 4;
						break;
				case 3: x = 4;
						y = 1;
						break;
			}
			atStart = true;
			atHome = false;
			atSafeZone = false;
		}
		public boolean checkStart(){
			return atStart;
		}
		public boolean checkHome(){
			return atHome;
		}
		public boolean checkSafeZone(){
			return atSafeZone;
		}
		public void setStart(boolean start){
			atStart = start;
		}
		public void setHome(boolean home){
			atHome = home;
		}
		public void setSafeZone(boolean safeZone){
			atSafeZone = safeZone;
		}
		
		public int[] getLoc(){
			int [] temp= {x,y};
			return temp;
		}
		public void setLoc(int newX,int newY){
			x = newX;
			y = newY;
		}
		public int getColor(){
			return pColor;
		}
		public void returnStart(){
			if(color == 0){
				setLoc(11, 14);
			}
			else if(color == 1){
				setLoc(1, 11);
			}
			else if(color == 2){
				setLoc(14, 4);
			}
			else{
				setLoc(4, 1);
			}
			atStart = true;
		}
		public void setHuman(boolean isMan){
			human = isMan;
		}
		public boolean checkHuman(){
			return human;
		}
	}
}
