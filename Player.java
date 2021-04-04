import java.util.ArrayList;
import java.util.*;

abstract class Player {

	String name;
	int points;
	ArrayList<Card> hand = new ArrayList<Card>();

	Player (String id) { name = id; points = 0; }

	// use this class to keep track of suit ranges
	// [startIndex, endIndex) or startIndex = -1 if no suit
	class SuitRange {
		int startIndex;
		int endIndex;
		SuitRange() { startIndex = -1; endIndex = -1; }
		// Returns how many cards of that suit exist
		int getRange() { return endIndex-startIndex; }
	}


	// Draw 
	void addToHand (Card newCard) { hand.add(newCard); }

	// Sorts 
	void sortHand () { Collections.sort(hand); }

	// Clears the hand
	void clearHand () { hand.clear(); }

	//suit check
	boolean checkSuit(Suit check) {
		boolean flag = false;
		if (check == null) return false;
		for (Card c: hand) { if (c.getSuit() == check) flag = true; }
		return flag;
	}

	// starting player to determiner
	boolean hasTwoOfClubs () { 
		if (hand.size() == 0) return false;
		Card holder = new Card(Suit.CLUBS, Value.TWO);
		return holder.equals(hand.get(0));
	}

	// Used to check if all the cards in this player's hand is hearts
	boolean hasAllHearts() {
		boolean flag = true;
		for (Card c : hand) { if (c.getSuit() != Suit.HEARTS) flag = false; }
		return flag;
	}

	// determines led suit
	Suit getFirstSuit(ArrayList<Card> currentRound) {
		if (currentRound.size() == 0) return null;
		return currentRound.get(0).getSuit();
	}

	// Given a suit, check the range of indices where that suit exists
	SuitRange getSuitRange(Suit check, ArrayList<Card> currentHand) {
		SuitRange range = new SuitRange();
		if (check == null) return range;
		for (int i = 0; i < currentHand.size(); i++) { 
			if (range.startIndex == -1 && currentHand.get(i).getSuit() == check) range.startIndex = i;
			if (range.startIndex != -1 && currentHand.get(i).getSuit() != check) { range.endIndex = i; break; }
		}
		if (range.startIndex != -1 && range.endIndex == -1) range.endIndex = currentHand.size();
		return range;
	}

	// prints current hand
	void printHand () {
		System.out.print("\n" + name + "`s hand ("+hand.size()+" card");
		if (hand.size() > 1) System.out.print("s");
		System.out.print("):\n|");
		for (int i = 0; i < hand.size(); i++) { System.out.format("%3d|", i); }
		System.out.print("\n|");
		for (int i = 0; i < hand.size(); i++) { 
			// we can either use printCard() or printCardShort()
			System.out.format("%3s|", hand.get(i).printCardShort()); 
		}
		System.out.println("");
	}

	
	// Return the name of the player
	String getName () { return name; }

	// Add points to this player
	void addPoints (int pnts) { points += pnts; }

	// Return the amount of points this player has
	int getPoints () { return points; }

	// Clear the cards in the hand and clear all points
	void clearPlayer() { clearHand(); points = 0; }

	
	abstract boolean setDebug();

	
	abstract Card performAction (State masterCopy);

}
