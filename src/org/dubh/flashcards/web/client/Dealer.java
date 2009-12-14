package org.dubh.flashcards.web.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Random;

/**
 * Deals cards.
 * 
 * @author bduff
 */
public class Dealer {
  private final List<Card> shuffledDeck;
  private int dealtCount = 0;
  
  Dealer(List<Card> deck) {
    shuffledDeck = new ArrayList<Card>(deck);
    shuffle();
  }

  private void shuffle() {
    // O(n) Durstenfeld shuffle
    int n = shuffledDeck.size();
    while (n > 1) {
      n--;
      int k = Random.nextInt(n + 1);
      Card tmp = shuffledDeck.get(k);
      shuffledDeck.set(k, shuffledDeck.get(n));
      shuffledDeck.set(n, tmp);
    }
  }
  
  public Card deal() {
    if (dealtCount == shuffledDeck.size()) {
      shuffle();
      dealtCount = 0;
    }
    return shuffledDeck.get(dealtCount++);
  }
}
