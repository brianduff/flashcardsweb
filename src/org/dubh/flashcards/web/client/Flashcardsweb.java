package org.dubh.flashcards.web.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Flashcardsweb implements EntryPoint {
  FlashcardsServiceAsync service = GWT.create(FlashcardsService.class);

  private HorizontalPanel mainPanel;
  private FlowPanel navigator;
  private Hyperlink createNewDeck;
  private List<String> sortedDeckNames;
  private TabPanel tabPanel;
  private Map<String, Hyperlink> hyperlinkNameToHyperlink = new HashMap<String, Hyperlink>();
  private Hyperlink currentlySelectedLink;
  private DeckEditor deckEditor;
  private Trainer trainer;
  private StatusIndicator statusIndicator;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    statusIndicator = new StatusIndicator("statusMessage");
    statusIndicator.startLoading();
    try {
      mainPanel = new HorizontalPanel();
      mainPanel.setVerticalAlignment(DockPanel.ALIGN_TOP);
      mainPanel.setWidth("100%");
      mainPanel.setSpacing(0);

      navigator = new FlowPanel();
      mainPanel.add(navigator);

      createNewDeck = new Hyperlink("New Deck", "createNew");
      createNewDeck.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          createNewDeck();
        }
      });
      updateNavigator();

      tabPanel = new TabPanel();

      trainer = new Trainer();
      tabPanel.add(trainer, "Train");
      tabPanel.setWidth("100%");
      deckEditor = new DeckEditor(this, service, statusIndicator);
      tabPanel.add(deckEditor, "Edit");
      mainPanel.add(tabPanel);

      mainPanel.setCellWidth(tabPanel, "100%");

      RootPanel.get().add(mainPanel);

      ValueChangeHandler<String> historyHandler = new ValueChangeHandler<String>() {
        public void onValueChange(ValueChangeEvent<String> event) {
          selectDeck(event.getValue());
        }

      };
      History.addValueChangeHandler(historyHandler);

      // Make the "create new" link the same height as the tab bar so that
      // the first deck link starts below the tabbar.
      createNewDeck.setHeight(tabPanel.getTabBar().getOffsetHeight() + "px");

      deckEditor.addValueChangeHandler(new ValueChangeHandler<List<Card>>() {
        public void onValueChange(ValueChangeEvent<List<Card>> event) {
          trainer.setCards(event.getValue());
        }
      });
    } finally {
      statusIndicator.stopLoading();
    }
  }

  void updateLinkText(Hyperlink link, String newText) {
    String oldName = link.getText();
    hyperlinkNameToHyperlink.remove(link.getText());
    link.setText(newText);
    link.setTargetHistoryToken(newText);
    hyperlinkNameToHyperlink.put(link.getText(), link);
    sortedDeckNames.remove(oldName);
    navigator.remove(link);
    insertLinkAtCorrectPosition(link);
  }

  private void selectDeck(String name) {
    statusIndicator.startLoading();
    if (currentlySelectedLink != null) {
      currentlySelectedLink.removeStyleDependentName("Selected");
    }

    Hyperlink link = hyperlinkNameToHyperlink.get(name);
    if (link != null) {
      tabPanel.setVisible(true);
      link.addStyleDependentName("Selected");
      currentlySelectedLink = link;
      deckEditor.deckSelected(name, link);
      service.getCards(name, new AsyncCallback<List<Card>>() {

        public void onFailure(Throwable caught) {
          statusIndicator.showErrorMessage("Failed to load card");
        }

        public void onSuccess(List<Card> result) {
          trainer.setCards(result);
          statusIndicator.stopLoading();
        }

      });
    } else {
      tabPanel.setVisible(false);
    }
  }

  private void updateNavigator() {
    statusIndicator.startLoading();
    navigator.clear();
    navigator.add(createNewDeck);
    service.getAllDeckNames(new AsyncCallback<Collection<String>>() {
      public void onFailure(Throwable caught) {
        statusIndicator.showErrorMessage("Failed to load");
      }

      public void onSuccess(Collection<String> deckNames) {
        sortedDeckNames = new ArrayList<String>();
        sortedDeckNames.addAll(deckNames);
        Collections.sort(sortedDeckNames);
        for (String deck : sortedDeckNames) {
          Hyperlink link = createDeckLink(deck);
          navigator.add(link);
          hyperlinkNameToHyperlink.put(deck, link);
        }
        selectDeck(History.getToken());
        statusIndicator.stopLoading();
      }
    });
  }

  private void createNewDeck() {
    statusIndicator.startLoading();
    service.createNewDeck(new AsyncCallback<String>() {
      public void onFailure(Throwable caught) {
        statusIndicator.showErrorMessage("Failed to create new deck");
      }

      public void onSuccess(String newDeckName) {
        Hyperlink newLink = createDeckLink(newDeckName);
        hyperlinkNameToHyperlink.put(newDeckName, newLink);
        insertLinkAtCorrectPosition(newLink);
        History.newItem(newDeckName);
        tabPanel.selectTab(1);
        statusIndicator.stopLoading();
      }
    });
  }

  private void insertLinkAtCorrectPosition(Hyperlink link) {
    // Find the right place to insert the new deck in the navigator.
    if (sortedDeckNames == null || sortedDeckNames.isEmpty()) {
      navigator.add(link);
      sortedDeckNames = new ArrayList<String>();
      sortedDeckNames.add(link.getText());
    } else {
      int position = Collections.binarySearch(sortedDeckNames, link.getText());
      if (position < 0) {
        position = -(position);
      }
      navigator.insert(link, position);
      sortedDeckNames.add(position - 1, link.getText());
    }
    DOM.scrollIntoView(link.getElement());
  }

  private Hyperlink createDeckLink(String deck) {
    Hyperlink link = new Hyperlink(deck, deck);
    link.addStyleDependentName("LeftNav");
    return link;
  }
}
