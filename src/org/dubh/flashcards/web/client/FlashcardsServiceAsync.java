package org.dubh.flashcards.web.client;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FlashcardsServiceAsync {
  void getAllDeckNames(AsyncCallback<Collection<String>> callback);

  void createNewDeck(AsyncCallback<String> callback);

  void renameDeck(String oldName, String newName, AsyncCallback<Void> callback);

  void getCards(String deckName, AsyncCallback<List<Card>> callback);

  void createNewCard(String deckName, AsyncCallback<Card> callback);

  void updateCard(Card card, AsyncCallback<Void> callback);

  void removeCard(String deckName, Card card, AsyncCallback<Void> callback);
}
