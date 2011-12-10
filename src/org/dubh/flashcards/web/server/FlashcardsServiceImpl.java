package org.dubh.flashcards.web.server;

import java.util.Collection;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.dubh.flashcards.web.client.Card;
import org.dubh.flashcards.web.client.FlashcardsService;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class FlashcardsServiceImpl extends RemoteServiceServlet implements
    FlashcardsService {

  public String createNewDeck() {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Query query = pm
          .newQuery("SELECT FROM org.dubh.flashcards.web.server.Deck "
              + "WHERE name.startsWith(\"Untitled\")");
      List<?> results = (List<?>) query.execute();
      int highestNumber = 1;
      for (Object result : results) {
        Deck deck = (Deck) result;
        String deckName = deck.getName();
        if (deckName.length() > "Untitled".length()) {
          String suffix = deckName.substring("Untitled".length());
          try {
            int value = Integer.parseInt(suffix.trim());
            highestNumber = Math.max(highestNumber, value);
          } catch (NumberFormatException e) {
            // ignore
          }
        }
      }
      String deckName = "Untitled" + (highestNumber + 1);
      Deck newDeck = new Deck();
      newDeck.setName(deckName);
      pm.makePersistent(newDeck);

      return deckName;
    } finally {
      pm.close();
    }
  }

  public Collection<String> getAllDeckNames() {

    List<String> deckNames = Lists.newArrayList();
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      for (Deck deck : pm.getExtent(Deck.class, true)) {
        deckNames.add(deck.getName());
      }
    } finally {
      pm.close();
    }
    return deckNames;
  }

  public void renameDeck(String oldName, String newName) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Deck deck = findDeck(pm, oldName);
      if (deck == null) {
        throw new IllegalArgumentException("Deck not found: " + oldName);
      }
      deck.setName(newName);
      pm.makePersistent(deck);
    } finally {
      pm.close();
    }
  }

  private Deck findDeck(PersistenceManager pm, String deckName) {
    // TODO(bduff) possibly vulnerable to sql injection...
    Query findOld = pm
        .newQuery("SELECT FROM org.dubh.flashcards.web.server.Deck "
            + "WHERE name == \"" + deckName + "\"");
    List<?> results = (List<?>) findOld.execute();
    if (results.size() == 1) {
      Deck deck = (Deck) results.get(0);
      return deck;
    }
    return null;
  }

  public Card createNewCard(String deckName) {
    Card card = new Card();
    card.setEnglish("English");
    card.setPinyin("p\u012Bny\u012Bn");
    card.setHanzi("\u6C49\u5B57");

    System.out.println("Created card " + card);

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Deck deck = findDeck(pm, deckName);
      if (deck == null) {
        throw new IllegalArgumentException("Deck not found: " + deckName);
      }
      deck.getCards().add(card);
      pm.makePersistent(deck);
      return card;
    } finally {
      pm.close();
    }
  }

  public List<Card> getCards(String deckName) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Deck deck = findDeck(pm, deckName);
      if (deck == null) {
        throw new IllegalArgumentException("Deck not found " + deckName);
      }
      return Lists.newArrayList(deck.getCards());
    } finally {
      pm.close();
    }
  }

  public void updateCard(Card card) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Key key = KeyFactory.stringToKey(card.getKey());
      Card theCard = pm.getObjectById(Card.class, key);
      theCard.setEnglish(card.getEnglish());
      theCard.setHanzi(card.getHanzi());
      theCard.setPinyin(card.getPinyin());
      pm.makePersistent(theCard);
    } finally {
      pm.close();
    }
  }

  public void removeCard(String deckName, Card card) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Deck deck = findDeck(pm, deckName);
      if (deck == null) {
        throw new IllegalArgumentException("Deck not found " + deckName);
      }
      Key key = KeyFactory.stringToKey(card.getKey());
      Card theCard = pm.getObjectById(Card.class, key);
      deck.getCards().remove(theCard);
      pm.makePersistent(deck);
    } finally {
      pm.close();
    }
  }

}
