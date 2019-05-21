package net.madeng.gradle.plugin.slack.api

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class Attachment {

  String authorName
  String authorLink
  String authorIcon
  String color
  String fallback
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  def fields = []
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  def blocks = []
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  def actions = []
  String footer
  String footerIcon
  String imageUrl
  String pretext
  String text
  String thumbUrl
  String title
  String titleLink
  Long ts = System.currentTimeSeconds()

  def field(Closure closure) {
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    Field field = new Field()
    closure.delegate = field
    fields << field
    closure()
  }

  def action(Closure closure) {
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    Action action = new Action()
    closure.delegate = action
    actions << action
    closure()
  }

  void block(Closure closure) {
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    Block block = new Block()
    closure.delegate = block
    blocks << block
    closure()
  }

}
