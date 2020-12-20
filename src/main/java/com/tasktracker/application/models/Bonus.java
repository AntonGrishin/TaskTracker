package com.tasktracker.application.models;

import javax.validation.constraints.NotBlank;
import javax.persistence.*;

@Entity
@Table(name = "bonuses")
public class Bonus {

  @Id
  @Column // To be remove ?
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long bonus_id;

  @NotBlank
  @Column(name = "userId")
  private String userId;


  @NotBlank
  @Column(name = "comment")
  private String comment;

  @NotBlank
  @Column(name = "month")
  private String month;

  @NotBlank
  @Column(name = "year")
  private String year;

  @NotBlank
  @Column(name = "amount")
  private String amount;

  public Bonus() {}

  public Bonus(@NotBlank String userId, @NotBlank String comment, @NotBlank String month, @NotBlank String year, @NotBlank String amount) {
      this.userId = userId;
      this.comment = comment;
      this.month = month;
      this.year = year;
      this.amount = amount;
  }

  public Long getBonus_id() {
      return bonus_id;
  }

  public void setBonus_id(Long bonus_id) {
      this.bonus_id = bonus_id;
  }

  public String getUserId() {
      return userId;
  }

  public void setUserId(String userId) {
      this.userId = userId;
  }

  public String getComment() {
      return comment;
  }

  public void setComment(String comment) {
      this.comment = comment;
  }

  public String getMonth() {
      return month;
  }

  public void setMonth(String month) {
      this.month = month;
  }

  public String getAmount() {
      return amount;
  }

  public void setAmount(String amount) {
      this.amount = amount;
  }

  @Override
  public String toString() {
      return "Bonus [amount=" + amount + ", bonus_id=" + bonus_id + ", comment=" + comment + ", month=" + month
              + ", userId=" + userId + "]";
  }

  public String getYear() {
      return year;
  }

  public void setYear(String year) {
      this.year = year;
  }

  


}
