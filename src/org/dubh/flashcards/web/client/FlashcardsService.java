package org.dubh.flashcards.web.client;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("fcs")
public interface FlashcardsService extends RemoteService {
  Collection<String> getAllDeckNames();

  String createNewDeck();

  void renameDeck(String oldName, String newName);

  List<Card> getCards(String deckName);

  Card createNewCard(String deckName);

  void updateCard(Card card);

  void removeCard(String deckName, Card card);
}
