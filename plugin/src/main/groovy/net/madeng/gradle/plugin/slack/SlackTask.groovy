package net.madeng.gradle.plugin.slack

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import net.madeng.gradle.plugin.slack.api.Message
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

import static org.apache.http.HttpStatus.SC_OK

class SlackTask extends DefaultTask {

  private static objectMapper = new ObjectMapper()
  private static logger = LoggerFactory.getLogger(SlackTask.class)

  SlackTask() {
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
  }

  @TaskAction
  void publishToSlack() {
    String webhookUrl = project.extensions.slack.webhookUrl
    Message message = project.extensions.slack.message
    if (null == webhookUrl || null == message) {
      logger.error('You need to set url: [{}] and message: [{}]', webhookUrl, message)
    } else {
      String stringMessage = objectMapper.writeValueAsString(project.extensions.slack.message)
      logger.debug('message: {}', stringMessage)

      CloseableHttpClient httpClient = HttpClients.createDefault()
      HttpPost httpPost = new HttpPost(webhookUrl)
      httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())
      httpPost.setEntity(new StringEntity(stringMessage))

      CloseableHttpResponse response = httpClient.execute(httpPost)
      int statusCode = response.getStatusLine().getStatusCode()
      if (SC_OK != statusCode) {
        logger.error("Something wrong: {}, {}", statusCode, response.getEntity())
      }
    }
  }
}
