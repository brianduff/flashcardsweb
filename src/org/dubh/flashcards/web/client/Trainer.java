package org.dubh.flashcards.web.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Trainer extends Composite {
  private final FocusPanel focusPanel = new FocusPanel();
  private final VerticalPanel mainPanel = new VerticalPanel();
  private final CardViewer cardViewer = new CardViewer();
  private final Button previous = new Button("Previous");
  private final Button next = new Button("Next");
  private final Button reveal = new Button("Reveal Answer");
  private final Label cardCounter = new Label();
    
  private List<Card> cards;
  private List<CardAndCategory> history;
  private Category questionCategory;
  private Dealer dealer;
  private int index = -1;
  
  Trainer() {
    initWidget(focusPanel);
    focusPanel.add(mainPanel);
    
    HorizontalPanel buttonBar = new HorizontalPanel();
    buttonBar.add(reveal);
    HTML gap = new HTML("<div />");
    gap.setWidth("20px");
    buttonBar.add(gap);
    buttonBar.add(previous);
    buttonBar.add(next);
    buttonBar.add(cardCounter);
    
    cardCounter.addStyleDependentName("CardCounter");

    reveal.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        cardViewer.setAnswerVisible(true);
      }
    });
    
    mainPanel.add(buttonBar);
    
    focusPanel.addKeyPressHandler(new KeyPressHandler() {
      public void onKeyPress(KeyPressEvent event) {
        switch (event.getCharCode()) {
        case ' ':
          cardViewer.setAnswerVisible(true);
          break;
        case 'j': 
          previousCard();
          break;
        case 'k':
          nextCard();
          break;
        }
      }      
    });
    
    questionCategory = null;
    
    HorizontalPanel questionCategoryPanel = new HorizontalPanel();
    questionCategoryPanel.add(new Label("Question category:"));
    RadioButton randomButton = new RadioButton("questionCategory", "Random");
    randomButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        questionCategory = null;
      }
    });
    questionCategoryPanel.add(randomButton);
    
    for (final Category category : Category.values()) {
      final RadioButton button = new RadioButton("questionCategory", category.getName());
      button.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          if (button.getValue()) {
            questionCategory = category;
          }
        }
      });
      questionCategoryPanel.add(button);
    }
    
    DisclosurePanel options = new DisclosurePanel("Options");
    options.setAnimationEnabled(true);
    options.setContent(questionCategoryPanel);
    
    mainPanel.add(options);
    mainPanel.add(cardViewer);
    next.setEnabled(false);
    previous.setEnabled(false);
    
    previous.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        previousCard();
      }
    });
    next.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        nextCard();
      }
    });
  }
  
  public void setCards(List<Card> cards) {
    this.cards = cards;
    dealer = new Dealer(cards);
    history = new ArrayList<CardAndCategory>();
    cardViewer.setVisible(!cards.isEmpty());
    reveal.setEnabled(!cards.isEmpty());
    next.setEnabled(!cards.isEmpty());
    index = -1;
    if (!cards.isEmpty()) {
      nextCard();
    } else {
      cardCounter.setText("No cards in this deck. Add some using the Edit tab");
    }
  }
  
  private void nextCard() {
    CardAndCategory card;
    if (index == history.size() - 1) {
      card = new CardAndCategory(dealer.deal(), questionCategory == null ? Category.random() : questionCategory);
      history.add(card);
    } else {
      card = history.get(index + 1);
    }
    index++;
    cardViewer.setAnswerVisible(false);
    cardViewer.setCard(card.category, card.card);
    previous.setEnabled(index > 0);
    updateCardCounter(card.category);
  }

  private void updateCardCounter(Category category) {
    cardCounter.setText(((index % cards.size()) + 1) + "/" + cards.size() + " - " + 
        category.getName());
  }
  
  private void previousCard() {
    if (index > 0) {
      CardAndCategory newCard = history.get(--index);
      cardViewer.setAnswerVisible(false);
      cardViewer.setCard(newCard.category, newCard.card);
      previous.setEnabled(index > 0);
      updateCardCounter(newCard.category);
    }
  }

  private class CardAndCategory {
    final Card card;
    final Category category;

    CardAndCategory(Card card, Category category) {
      this.card = card;
      this.category = category;
    }
  }
}
