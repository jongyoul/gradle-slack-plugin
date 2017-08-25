package net.madeng.gradle.plugin.slack.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class Field {

  @JsonProperty("short")
  boolean shortValue;
  String title;
  String value;
}
