import java.util.ArrayList;
import java.util.*;



class State {

	Deck 				cardsPlayed;		// Note: This keeps track of the cards *not* played as well
	ArrayList<Card> 	currentRound;
	ArrayList<Integer> 	playerScores;
	boolean 			hasHeartsBroken; 	// Keep track of whether hears has broken or not
	Random 				rng = new Random();
	int 				playerIndex;		// To help remember which player # this is

	// Remember to make COPIES of what is passed in!
	State (Deck deck, ArrayList<Card> round, ArrayList<Integer> scores, boolean hearts, int index) {
		cardsPlayed     = new Deck (deck);
		currentRound    = new ArrayList<Card>(round);
		playerScores    = new ArrayList<Integer>(scores);
		hasHeartsBroken = hearts;
		playerIndex 	= index;
	}

	// Copy constructor to spawn off duplicate State objects
	State (State secondCopy) {
		cardsPlayed 	= new Deck(secondCopy.cardsPlayed);
		currentRound 	= new ArrayList<Card>(secondCopy.currentRound);
		playerScores 	= new ArrayList<Integer>(secondCopy.playerScores);
		hasHeartsBroken = secondCopy.hasHeartsBroken;
		playerIndex 	= secondCopy.playerIndex;
	}


	int getRoundNumber() { return cardsPlayed.size()/4 + 1; }
	
	
	boolean isGameValid() { return cardsPlayed.size() < 52; }

	
	boolean validRound() { return currentRound.size() < 4; }

	
	boolean firstMove() { return cardsPlayed.allCards.size()==0; }

	
	boolean firstInRound() { return currentRound.size()==0; }

	
	int getScore() { return playerScores.get(playerIndex); }

	
	boolean isInMyHand(Card c, ArrayList<Card> playoutHand) {
		for (Card d : playoutHand) if (c.equals(d)) return true;
		return false;
	}


	void playCard(Card c) { 
		for (Card d : cardsPlayed.invertDeck) {
			if (c.equals(d)) {
				cardsPlayed.allCards.add(d);
				cardsPlayed.invertDeck.remove(d);
				currentRound.add(d);
				break;
			}
		}
	}

	
	boolean hasAllHearts(ArrayList<Card> hand) {
		boolean flag = true;
		for (Card c : hand) { if (c.getSuit() != Suit.HEARTS) flag = false; }
		return flag;
	}

	
	boolean checkRound(Card c, ArrayList<Card> playoutHand) {
		
		Card twoClubs = new Card(Suit.CLUBS, Value.TWO);
		if (firstMove() && !c.equals(twoClubs)) {
			System.out.println("Simulation issue: Must play two of clubs to start the game.");
			return false;
		}
		
		if (firstInRound()) {
			if (c.getSuit() == Suit.HEARTS && !hasHeartsBroken && !hasAllHearts(playoutHand)) 
				return false;
			return true;
		} else {
			
			Suit firstSuit = currentRound.get(0).getSuit();
			
			if (firstSuit != c.getSuit()) {
				boolean flag = false;
				for (Card d: playoutHand) { if (d.getSuit() == firstSuit) flag = true; }
				if (flag) return false;
			}
			// If suit is appropriate, check if hearts
			if (c.getSuit() == Suit.HEARTS) {
				hasHeartsBroken = true;
			}
		}
		return true;
	}

	// Go through the cards from the currentRound and calculate their point values
	int calculatePoints() {
		int points = 0;
		for (Card c : currentRound) {
			if (c.getSuit() == Suit.HEARTS) points++;
			if (c.getValue() == Value.QUEEN && c.getSuit() == Suit.SPADES) points += 13;
		}
		return points;
	}


	
	int findTaker (int firstPlayer) {
		Suit firstSuit = currentRound.get(0).getSuit();
		Value largestValue = currentRound.get(0).getValue();
		int taker = firstPlayer;

		
		for (int i = 0; i < playerScores.size(); i++) {
			
			int index = (firstPlayer+i) % playerScores.size();
			
			if (currentRound.get(i).getSuit() == firstSuit) {
				
				if (largestValue.compareTo(currentRound.get(i).getValue()) < 0) {
					taker = index;
					largestValue = currentRound.get(i).getValue();
				}
			}
		}

		return taker % playerScores.size();
	}

	

	
	int advance(Card c, ArrayList<Card> playoutHand) {
		if (!checkRound(c,playoutHand)) return -1;

		int playTurn = currentRound.size();		// keep track of which player this is
		
		playCard(c);


		while (validRound()) {

		
			int index = rng.nextInt(cardsPlayed.invertDeck.size());
			// Takes time equivalent to hand size!
			while (isInMyHand(cardsPlayed.invertDeck.get(index), playoutHand)) {
				index = rng.nextInt(cardsPlayed.invertDeck.size());
			}
			// Use the play card method to put take the card out of the invert deck, into the played deck, and also onto the table
			playCard(cardsPlayed.invertDeck.get(index));

		}

		// Round has ended -- check what points have gone where and determine who goes next (use playerScores)
		int firstPlayer = (playerIndex - playTurn + playerScores.size()) % playerScores.size();
		int points = calculatePoints();
		int taker = findTaker(firstPlayer);
		playerScores.set(taker, playerScores.get(taker)+points);

		// Check what points to return from this function
		int returnpoints = 0;
		if (taker == playerIndex) returnpoints = points;

		// Clear the cards on the table (don't worry, pointers to them are tracked in the cardsPlayed deck)
		currentRound.clear();

		
		if (isGameValid()) {
			// repeat until taker = playerIndex
			while (taker != playerIndex) {
				
				int index = rng.nextInt(cardsPlayed.invertDeck.size());
				// Takes time equivalent to hand size!
				while (isInMyHand(cardsPlayed.invertDeck.get(index), playoutHand)) {
					index = rng.nextInt(cardsPlayed.invertDeck.size());
				}
				
				playCard(cardsPlayed.invertDeck.get(index));
				taker = (taker+1) % playerScores.size();
			}
		}

		
		return returnpoints;
	}

}
