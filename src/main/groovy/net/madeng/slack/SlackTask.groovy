package net.madeng.slack

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import net.madeng.slack.api.Message
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting

import static org.apache.http.HttpStatus.SC_OK

class SlackTask extends DefaultTask {

  private static objectMapper = new ObjectMapper()
  static {
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
  }

  @Deprecated
  @Internal
  Map slackResponse

  @TaskAction
  void publishToSlack() {
    Message message = project.extensions.slack.message
    Boolean failOnError = project.extensions.slack.failOnError
    String webhookUrl = project.extensions.slack.webhookUrl
    String oauthToken = project.extensions.slack.oauthToken
    if (!webhookUrl && !oauthToken) {
      throw new GradleException('One of \'webhookUrl\' and \'oauthToken\' should set')
    }
    if (!message) {
      throw new GradleException('Message should set')
    }
    logger.lifecycle('Sending message to slack')
    Map response = oauthToken ? apiPost(oauthToken, message) : webhookPost(webhookUrl, message)
    slackResponse = response.responseObject as Map
    if (SC_OK != response.statusCode) {
      logger.error('Error while publishing to slack: {}, {}', response.statusCode, response.responseText)
      if (failOnError) {
        throw new GradleException("Error while posting to slack: ${response.statusLine}, ${response.responseText}")
      }
    }
  }

  @Internal
  Map webhookPost(String url, Message message) {
    assert url, 'missing webhook URL'
    assert message, 'missing message'
    def response = postMessage(message, url, null)
    if (SC_OK != response.statusCode) {
      response.responseObject = [error: response.responseText, statusCode: response.statusCode, responseText: response.responseText]
    } else {
      response.responseObject = [ok: 'true']
    }
    return response
  }

  @Internal
  Map apiPost(String oauthToken, Message message) {
    assert oauthToken, 'missing oauth token'
    assert message, 'missing message'
    def apiUrl = 'https://slack.com/api/chat.postMessage'
    def response = postMessage(message, apiUrl, oauthToken)
    if (SC_OK != response.statusCode) {
      response.responseObject = [error: response.responseText, statusCode: response.statusCode, responseText: response.responseText]
    } else {
      response.responseObject = objectMapper.readValue(response.responseText as String, Map.class)
    }
    return response
  }

  @Internal
  Map postMessage(Message message, String url, String oauthToken) {
    def postResponse = [:]
    CloseableHttpClient httpClient = HttpClients.createDefault()
    HttpPost httpPost = new HttpPost(url)
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())
    if (oauthToken) {
      httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer ${oauthToken}")
    }
    httpPost.setEntity(new StringEntity(messageJson(message)))
    httpClient.withCloseable {
      CloseableHttpResponse response = httpClient.execute(httpPost)
      int statusCode = response.getStatusLine().getStatusCode()
      def responseText = EntityUtils.toString(response.entity)
      postResponse.statusCode = statusCode
      postResponse.responseText = responseText
    }
    return postResponse
  }

  @VisibleForTesting
  static String messageJson(Message message) {
    return objectMapper.writeValueAsString(message)
  }
}
