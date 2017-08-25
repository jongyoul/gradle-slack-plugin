package net.madeng.gradle.plugin.slack.api

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class Message {

  def attachments = []
  String channel
  String iconUrl
  String iconEmoji
  String text
  String username

  void attachment(Closure closure) {
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    Attachment attachment = new Attachment()
    closure.delegate = attachment
    attachments << attachment
    closure()
  }
}
