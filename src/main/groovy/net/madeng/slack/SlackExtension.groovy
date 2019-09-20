package net.madeng.slack

import net.madeng.slack.api.Message

class SlackExtension {

  Boolean failOnError
  String webhookUrl
  String oauthToken
  Message message = new Message()

  def message(Closure closure) {
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.delegate = message
    closure()
  }
}
