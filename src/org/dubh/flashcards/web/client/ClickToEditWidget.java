package org.dubh.flashcards.web.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class ClickToEditWidget extends Composite implements
    HasValueChangeHandlers<String>, HasKeyPressHandlers {
  private static final int LABEL = 0;
  private static final int FIELD = 1;

  private static ClickToEditWidget activeWidget;

  private final DeckPanel deckPanel = new DeckPanel();
  private final Label label = new Label();
  private final TextBox textBox = new TextBox();

  private boolean applyingChanges = false;

  public ClickToEditWidget() {
    initWidget(deckPanel);

    deckPanel.add(label);
    deckPanel.add(textBox);

    label.addStyleDependentName("ClickToEdit");

    label.addMouseOverHandler(new MouseOverHandler() {
      public void onMouseOver(MouseOverEvent event) {
        label.removeStyleDependentName("ClickToEdit");
        label.addStyleDependentName("ClickToEditHover");
      }
    });
    label.addMouseOutHandler(new MouseOutHandler() {
      public void onMouseOut(MouseOutEvent event) {
        label.removeStyleDependentName("ClickToEditHover");
        label.addStyleDependentName("ClickToEdit");
      }
    });

    label.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        setEditing(true);
      }
    });

    textBox.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == 27) { // ESC
          textBox.setText(label.getText());
          setEditing(false);
        }
      }
    });

    textBox.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        applyChanges();
      }
    });

    setEditing(false);
  }

  public ClickToEditWidget(String text) {
    this();
    setText(text);
  }

  public void setText(String text) {
    label.setText(text);
    textBox.setText(text);
  }

  private void applyChanges() {
    if (applyingChanges) {
      return;
    }
    try {
      applyingChanges = true;
      System.out.println("Firing apply changes");
      ValueChangeEvent.fireIfNotEqual(this, label.getText(), textBox.getText());
      label.setText(textBox.getText());
      setEditingImpl(false);
    } finally {
      applyingChanges = false;
    }
  }

  public void setEditing(boolean editing) {
    if (editing && activeWidget != null) {
      activeWidget.applyChanges();
    }
    setEditingImpl(editing);
  }

  private void setEditingImpl(boolean editing) {
    deckPanel.showWidget(editing ? FIELD : LABEL);
    if (editing) {
      activeWidget = this;
      textBox.setFocus(true);
      textBox.selectAll();
    }
  }

  public HandlerRegistration addValueChangeHandler(
      ValueChangeHandler<String> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
    return textBox.addKeyPressHandler(handler);
  }
}
