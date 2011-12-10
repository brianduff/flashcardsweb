package org.dubh.flashcards.web.client;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Random;

/**
 * @author bduff@google.com (Brian Duff)
 */
public enum Category {
  HANZI("Hanzi") {
    @Override
    String getQuestion(Card card) {
      return card.getHanzi();
    }

    @Override
    List<String> getAnswers(Card card) {
      return Arrays.asList(card.getEnglish(), card.getPinyin());
    }
  },
  PINYIN("Pinyin") {
    @Override
    String getQuestion(Card card) {
      return card.getPinyin();
    }

    @Override
    List<String> getAnswers(Card card) {
      return Arrays.asList(card.getEnglish(), card.getHanzi());
    }
  },
  ENGLISH("English") {
    @Override
    String getQuestion(Card card) {
      return card.getEnglish();
    }

    @Override
    List<String> getAnswers(Card card) {
      return Arrays.asList(card.getPinyin(), card.getHanzi());
    }
  };

  private final String name;

  Category(String name) {
    this.name = name;
  }

  abstract String getQuestion(Card card);

  abstract List<String> getAnswers(Card card);

  String getName() {
    return name;
  }

  public static Category random() {
    return Category.values()[Random.nextInt(3)];
  }
}
