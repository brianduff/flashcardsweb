package org.dubh.flashcards.web.client;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * A composite that views a card.
 * 
 * @author bduff
 */
public class CardViewer extends Composite {
  private final DockPanel dockPanel = new DockPanel();
  private final Label questionLabel = new Label();
  private final Label answerOneLabel = new Label();
  private final Label answerTwoLabel = new Label();

  CardViewer() {
    initWidget(dockPanel);

    dockPanel.addStyleDependentName("Flashcard");
    dockPanel.add(questionLabel, DockPanel.CENTER);
    dockPanel.setCellWidth(questionLabel, "100%");
    dockPanel.setCellHorizontalAlignment(questionLabel, DockPanel.ALIGN_CENTER);
    dockPanel.setCellVerticalAlignment(questionLabel, DockPanel.ALIGN_MIDDLE);

    DockPanel south = new DockPanel();
    south.setWidth("100%");
    south.add(answerOneLabel, DockPanel.WEST);
    south.setCellWidth(answerOneLabel, "50%");
    south.add(answerTwoLabel, DockPanel.EAST);
    south.setCellWidth(answerTwoLabel, "50%");

    south.setCellHorizontalAlignment(answerTwoLabel, DockPanel.ALIGN_RIGHT);

    dockPanel.add(south, DockPanel.SOUTH);
    dockPanel.setCellWidth(south, "100%");

    questionLabel.addStyleDependentName("CardQuestion");
    answerOneLabel.addStyleDependentName("CardAnswer");
    answerTwoLabel.addStyleDependentName("CardAnswer");
    setAnswerVisible(false);
  }

  public void setCard(Category questionCategory, Card card) {
    questionLabel.setText(questionCategory.getQuestion(card));
    List<String> answers = questionCategory.getAnswers(card);
    answerOneLabel.setText(answers.get(0));
    answerTwoLabel.setText(answers.get(1));
  }

  public void setAnswerVisible(boolean answerVisible) {
    answerOneLabel.setVisible(answerVisible);
    answerTwoLabel.setVisible(answerVisible);
  }
}
