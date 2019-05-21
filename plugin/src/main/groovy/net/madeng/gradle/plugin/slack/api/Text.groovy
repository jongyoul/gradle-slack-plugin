package net.madeng.gradle.plugin.slack.api

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class Text {

  String type
  String text
}
