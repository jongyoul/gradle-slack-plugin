package net.madeng.gradle.plugin.slack.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class Accessory {

  String type
  String actionId
  String initialDate
  String imageUrl
  String altText
  Text  placeholder

  void placeholder(Closure closure) {
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    placeholder = new Text()
    closure.delegate = placeholder
    closure()
  }

}
