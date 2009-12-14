package org.dubh.flashcards.web.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A status indicator widget.
 * 
 * @author bduff
 */
public class StatusIndicator extends Composite {
  private final Label label = new Label();
  private final Timer timer = new Timer() {
    public void run() {
      timerTick();
    }
  };
  
  StatusIndicator(String divId) {
    initWidget(label);
    
    RootPanel panel = RootPanel.get(divId);
    panel.add(this);
    
    //label.getElement().getStyle().setProperty("white-space", "nowrap");
    label.setStylePrimaryName("statusLabel");
  }
  
  public void startLoading() {
    timer.cancel();
    label.setVisible(true);
    label.setText("Loading...");
    label.addStyleDependentName("loading");
  }
  
  public void stopLoading() {
    timer.cancel();
    label.setText("");
    label.setVisible(false);
    label.removeStyleDependentName("loading");
  }
  
  /**
   * Shows an error message for 7s.
   * 
   * @param errorMessage
   */
  public void showErrorMessage(String errorMessage) {
    timer.cancel();
    
    label.setText(errorMessage);
    label.addStyleDependentName("error");
    timer.schedule(7000);
  }
  
  private void timerTick() {
    label.setVisible(false);
    label.setText("");
    label.removeStyleDependentName("error");
    label.removeStyleDependentName("loading");
  }

}
