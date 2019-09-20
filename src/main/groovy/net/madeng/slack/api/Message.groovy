package net.madeng.slack.api

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class Message {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  def attachments = []
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  def blocks = []
  String channel
  String iconUrl
  String iconEmoji
  String text
  String username
  Boolean mrkdwn
  Boolean asUser
  Boolean linkNames
  String parse
  Boolean unfurlLinks
  Boolean unfurlMedia
  String threadTs
  Boolean replyBroadcast

  void attachment(Closure closure) {
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    Attachment attachment = new Attachment()
    closure.delegate = attachment
    attachments << attachment
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
