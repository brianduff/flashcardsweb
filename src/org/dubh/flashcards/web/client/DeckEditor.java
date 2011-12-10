package org.dubh.flashcards.web.client;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DeckEditor extends Composite implements
    HasValueChangeHandlers<List<Card>> {

  private final VerticalPanel mainPanel;
  private final TextBox nameBox;
  private boolean adjustingDeckName = false;
  private String currentDeckName;
  private Hyperlink currentDeckLink;
  private final FlashcardsServiceAsync service;
  private final Flashcardsweb main;
  private FlexTable cardsTable;
  private List<Card> cards;
  private final StatusIndicator statusIndicator;

  private static final Runnable doNothing = new Runnable() {

    public void run() {
      // TODO Auto-generated method stub

    }

  };

  DeckEditor(Flashcardsweb main, FlashcardsServiceAsync service,
      StatusIndicator statusIndicator) {
    this.main = main;
    this.service = service;
    this.statusIndicator = statusIndicator;
    mainPanel = new VerticalPanel();
    initWidget(mainPanel);

    HorizontalPanel namePanel = new HorizontalPanel();
    namePanel.add(new HTML("Deck Name:"));
    nameBox = new TextBox();
    namePanel.add(nameBox);

    mainPanel.add(namePanel);

    nameBox.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent e) {
        if (!adjustingDeckName) {
          applyNameChange(nameBox.getText());
        }
      }
    });

    Button createNew = new Button("New Card");
    mainPanel.add(createNew);

    createNew.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        createNewCard();
      }
    });

  }

  private void createNewCard() {
    createNewCard(doNothing);
  }

  private void createNewCard(final Runnable stuffToDoAfterSuccess) {
    statusIndicator.startLoading();
    service.createNewCard(currentDeckName, new AsyncCallback<Card>() {

      public void onFailure(Throwable caught) {
        statusIndicator.showErrorMessage("Failed to create card");
      }

      public void onSuccess(Card result) {
        int row = cardsTable.getRowCount();
        addCard(row, result);
        cards.add(result);
        ValueChangeEvent.fire(DeckEditor.this, cards);
        stuffToDoAfterSuccess.run();
        statusIndicator.stopLoading();
      }

    });
  }

  private void applyNameChange(final String newName) {
    if (currentDeckLink != null) {
      statusIndicator.startLoading();
      service.renameDeck(currentDeckName, newName, new AsyncCallback<Void>() {

        public void onFailure(Throwable caught) {
          nameBox.setText(currentDeckName);
          statusIndicator.showErrorMessage("Failed to rename deck");
        }

        public void onSuccess(Void result) {
          main.updateLinkText(currentDeckLink, newName);
          History.newItem(newName);
          statusIndicator.stopLoading();
        }

      });
    }
  }

  public void deckSelected(String deckName, Hyperlink deckLink) {
    try {
      statusIndicator.startLoading();
      adjustingDeckName = true;
      currentDeckName = deckName;
      currentDeckLink = deckLink;
      nameBox.setText(deckName);
      nameBox.setFocus(true);
      nameBox.selectAll();
      if (cardsTable != null) {
        mainPanel.remove(cardsTable);
      }
      cardsTable = new FlexTable();
      mainPanel.add(cardsTable);

      service.getCards(deckName, new AsyncCallback<List<Card>>() {

        public void onFailure(Throwable caught) {
          statusIndicator.showErrorMessage("Failed to load deck");
        }

        public void onSuccess(List<Card> result) {
          cards = result;
          int row = 0;
          for (Card card : result) {
            addCard(row, card);
            row++;
          }
          statusIndicator.stopLoading();
        }

      });

    } finally {
      adjustingDeckName = false;
    }
  }

  private void addCard(int row, final Card card) {
    Button delete = new Button("Remove");
    delete.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        statusIndicator.startLoading();
        service.removeCard(currentDeckName, card, new AsyncCallback<Void>() {

          public void onFailure(Throwable caught) {
            statusIndicator.showErrorMessage("Failed to add card");
          }

          public void onSuccess(Void result) {
            int index = cards.indexOf(card);
            cardsTable.removeRow(index);
            cards.remove(index);
            ValueChangeEvent.fire(DeckEditor.this, cards);
            statusIndicator.stopLoading();
          }
        });
      }
    });
    cardsTable.setWidget(row, 0, delete);

    final ClickToEditWidget hanzi = new ClickToEditWidget(card.getHanzi());
    final ClickToEditWidget pinyin = new ClickToEditWidget(card.getPinyin());
    final ClickToEditWidget english = new ClickToEditWidget(card.getEnglish());

    hanzi.addValueChangeHandler(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        card.setHanzi(event.getValue());
        updateCard(card);
        pinyin.setEditing(true);
      }
    });
    cardsTable.setWidget(row, 1, hanzi);
    pinyin.addValueChangeHandler(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        card.setPinyin(event.getValue());
        updateCard(card);
        english.setEditing(true);
      }
    });
    cardsTable.setWidget(row, 2, pinyin);
    english.addValueChangeHandler(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        card.setEnglish(event.getValue());
        updateCard(card);
        int index = cards.indexOf(card);
        if (index >= 0 && index < cardsTable.getRowCount() - 1) {
          ClickToEditWidget widget = (ClickToEditWidget) cardsTable.getWidget(
              index + 1, 1);
          widget.setEditing(true);
        } else {
          createNewCard(new Runnable() {
            public void run() {
              ClickToEditWidget widget = (ClickToEditWidget) cardsTable
                  .getWidget(cardsTable.getRowCount() - 1, 1);
              widget.setEditing(true);
            }
          });
        }
      }
    });
    cardsTable.setWidget(row, 3, english);
  }

  private void updateCard(Card card) {
    statusIndicator.startLoading();
    service.updateCard(card, new AsyncCallback<Void>() {

      public void onFailure(Throwable caught) {
        statusIndicator.showErrorMessage("Failed to update card");
      }

      public void onSuccess(Void result) {
        statusIndicator.stopLoading();
      }

    });
  }

  public HandlerRegistration addValueChangeHandler(
      ValueChangeHandler<List<Card>> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

}
