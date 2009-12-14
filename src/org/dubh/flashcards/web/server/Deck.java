package org.dubh.flashcards.web.server;

import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.dubh.flashcards.web.client.Card;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Deck {
   
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
  private String key;
  
  @Persistent
  private String name;
  
  @Persistent
  private List<Card> cards;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Card> getCards() {
    return cards;
  }

  public void setCards(List<Card> cards) {
    this.cards = cards;
  }
}
