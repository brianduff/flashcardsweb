package org.dubh.flashcards.web.client;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


/**
 * Represents a single flashcard, which consists of english, hanzi and pinyin
 * text.
 * @author bduff
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Card implements Serializable {
  
  private static final long serialVersionUID = -8505739952332378078L;

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
  private String key;
  
  @Persistent
  private String hanzi;
  
  public Card() {
    
  }
  
  public Card(String english, String pinyin, String hanzi) {
    setEnglish(english);
    setPinyin(pinyin);
    setHanzi(hanzi);
  }
  
  @Override
  public String toString() {
    return "Card [english=" + english + ", hanzi=" + hanzi + ", pinyin="
        + pinyin + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((english == null) ? 0 : english.hashCode());
    result = prime * result + ((hanzi == null) ? 0 : hanzi.hashCode());
    result = prime * result + ((pinyin == null) ? 0 : pinyin.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Card other = (Card) obj;
    if (english == null) {
      if (other.english != null)
        return false;
    } else if (!english.equals(other.english))
      return false;
    if (hanzi == null) {
      if (other.hanzi != null)
        return false;
    } else if (!hanzi.equals(other.hanzi))
      return false;
    if (pinyin == null) {
      if (other.pinyin != null)
        return false;
    } else if (!pinyin.equals(other.pinyin))
      return false;
    return true;
  }

  @Persistent
  private String english;
  
  @Persistent
  private String pinyin;

  @Persistent
  private int englishRight;
  
  @Persistent
  private int englishWrong;
  
  @Persistent
  private int pinyinRight;
  
  @Persistent
  private int pinyinWrong;
  
  @Persistent
  private int hanziRight;
  
  @Persistent
  private int hanziWrong;
  
  public String getHanzi() {
    return hanzi;
  }

  public void setHanzi(String hanzi) {
    this.hanzi = hanzi;
  }

  public String getEnglish() {
    return english;
  }

  public void setEnglish(String english) {
    this.english = english;
  }

  public String getPinyin() {
    return pinyin;
  }

  public void setPinyin(String pinyin) {
    this.pinyin = pinyin;
  }
  
  public String getKey() {
    return key;
  }
  
  public void guessedRight(Category category) {
    switch (category) {
    case HANZI:
      hanziRight++;
      break;
    case PINYIN:
      pinyinRight++;
      break;
    case ENGLISH:
      englishRight++;
      break;
    }
  }

  public void guessedWrong(Category category) {
    switch (category) {
    case HANZI:
      hanziWrong++;
      break;
    case PINYIN:
      pinyinWrong++;
      break;
    case ENGLISH:
      englishWrong++;
      break;
    }
  }
  
  

}
