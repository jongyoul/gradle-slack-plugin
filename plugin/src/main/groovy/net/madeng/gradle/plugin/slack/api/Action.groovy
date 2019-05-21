package net.madeng.gradle.plugin.slack.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class Action {

  Confirm confirm
  String name;
  String text;
  String style;
  String type;
  String value;

  def confirm(Closure closure) {
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    confirm = new Confirm()
    closure.delegate = confirm
    closure()
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  class Confirm {
    String title
    String text
    String okText
    String dismissText
  }

}

