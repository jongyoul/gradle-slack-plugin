package net.madeng.slack


import org.gradle.api.GradleException
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SlackPluginTest extends Specification {
  @Rule
  TemporaryFolder testProjectDir = new TemporaryFolder()

  File settingsFile
  File buildFile

  String pluginId = System.getenv("PLUGIN_ID")
  String pluginArtifact = System.getenv("PLUGIN_ARTIFACT")
  File pluginPropertiesFile = new File(System.getenv("PLUGIN_PROPERTIES_FILE"))
  List<File> pluginClasspath
  String webhookUrl
  String oauthToken
  String channel

  String baseSettingScript = "rootProject.name = '${pluginArtifact}-test'"
  String baseBuildScript = """
plugins {
  id '${pluginId}'
}
"""

  def setup() {
    settingsFile = testProjectDir.newFile("settings.gradle")
    buildFile = testProjectDir.newFile("build.gradle")

    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    if (pluginClasspathResource == null) {
      throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
    }
    pluginClasspath = pluginClasspathResource.readLines().collect {new File(it)}

    if (!pluginPropertiesFile.exists()) {
      throw new GradleException("You must create ${propertiesFile.path} containing webhookUrl, oauthToken, and channel properties")
    }
    pluginPropertiesFile.withInputStream {
      Properties properties = new Properties()
      properties.load(it)
      def getProperty = {name ->
        if (!properties.get(name)) {
          throw new GradleException("Expected ${name} value in ${propertiesFile}")
        }
        properties.get(name).toString().replaceAll(/^"(.*)"$/, '$1')
      }
      webhookUrl = getProperty('webhookUrl')
      channel = getProperty('channel')
      oauthToken = getProperty('oauthToken')
    }

  }

  def "publishSlack task publishes slack"() {
    given:
    settingsFile << baseSettingScript
    buildFile << baseBuildScript
    buildFile << """
slack {
  webhookUrl = '${webhookUrl}'
  message {
    text = "a message from gradle"
    block {
      type = "section"
      text {
        type = "mrkdwn"
        text = "Danny Torrence left the following review for your property:"
      }
    }
    block {
      type = "section"
      blockId = "section567"
      text {
        type = 'mrkdwn'
        text = "<https://google.com|Overlook Hotel> \\n :star: \\n Doors had too many axe holes, guest in room 237 was far too rowdy, whole place felt stuck in the 1920s."
      }
      accessory {
        type = "image"
        imageUrl = "https://is5-ssl.mzstatic.com/image/thumb/Purple3/v4/d3/72/5c/d3725c8f-c642-5d69-1904-aa36e4297885/source/256x256bb.jpg"
        altText = "Haunted hotel image"
      }
    }
    block {
      type = "divider"
    }
    block {
      type = "section"
      text {
        type = "mrkdwn"
        text = "*Sally* has requested you set the deadline for the Nano launch project"
      }
      accessory {
        type = "datepicker"
        actionId = "datepicker123"
        initialDate = "1990-04-28"
        placeholder {
          type = "plain_text"
          text = "Select a date"
        }
      }
    }
    block {
      type = "context"
      element {
        type = "image"
        imageUrl = "https://image.freepik.com/free-photo/red-drawing-pin_1156-445.jpg"
        altText = "images"
      }
      element {
        type = "mrkdwn"
        text = "Location: **Dogpatch**"
      }
    }
  }
}
"""
    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments(SlackPlugin.PUBLISH_TO_SLACK_TASK_NAME)
        .withPluginClasspath(pluginClasspath)
        .build()

    then:
    result.task(":${SlackPlugin.PUBLISH_TO_SLACK_TASK_NAME}").outcome == SUCCESS
  }

  def "publishSlack message with options"() {
    given:
    settingsFile << baseSettingScript
    buildFile << baseBuildScript
    buildFile << """
slack {
  webhookUrl = '${webhookUrl}'
  message {
    text = "a message from gradle"
    block {
      type = "actions"
      blockId = "actionblock789"
      element {
        type = "datepicker"
        actionId = "datepicker123"
        initialDate = "1990-04-28"
        placeholder {
          type = "plain_text"
          text = "Select a date"
        }
      }
      element {
        type = "overflow"
        option {
          text {
            type = "plain_text"
            text = "*this is plain_text text* value-0"
          }
          value = "value-0"
        }
        option {
          text {
            type = "plain_text"
            text = "*this is mrkdwn text* value-1"
          }
          value = "value-1"
        }
        option {
          text {
            type = "plain_text"
            text = "*this is plain_text text* value-2"
          }
          value = "value-2"
        }
        option {
          text {
            type = "plain_text"
            text = "*this is markdown text* value-3"
          }
          value = "value-3"
        }
        option {
          text {
            type = "plain_text"
            text = "*this is plain_text text*"
          }
          value = "value-4"
        }
        actionId = "overflow"
      }
      element {
        type = "button"
        text {
          type = "plain_text"
          text = "Click Me"
        }
        value = "click_me_123"
        actionId = "button"
      }
    }
  }
}
"""

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments(SlackPlugin.PUBLISH_TO_SLACK_TASK_NAME)
        .withPluginClasspath(pluginClasspath)
        .build()

    then:
    result.task(":${SlackPlugin.PUBLISH_TO_SLACK_TASK_NAME}").outcome == SUCCESS
  }


  def "post message using chat api"() {
    given:
    settingsFile << baseSettingScript
    buildFile << baseBuildScript
    buildFile << """
slack {
  oauthToken = '${oauthToken}'
  message {
    channel = '${channel}'
    text = "a message from gradle"
    block {
      type = "actions"
      blockId = "actionblock789"
      element {
        type = "datepicker"
        actionId = "datepicker123"
        initialDate = "1990-04-28"
        placeholder {
          type = "plain_text"
          text = "Select a date"
        }
      }
      element {
        type = "overflow"
        option {
          text {
            type = "plain_text"
            text = "*this is plain_text text* value-0"
          }
          value = "value-0"
        }
        option {
          text {
            type = "plain_text"
            text = "*this is mrkdwn text* value-1"
          }
          value = "value-1"
        }
        option {
          text {
            type = "plain_text"
            text = "*this is plain_text text* value-2"
          }
          value = "value-2"
        }
        option {
          text {
            type = "plain_text"
            text = "*this is markdown text* value-3"
          }
          value = "value-3"
        }
        option {
          text {
            type = "plain_text"
            text = "*this is plain_text text*"
          }
          value = "value-4"
        }
        actionId = "overflow"
      }
      element {
        type = "button"
        text {
          type = "plain_text"
          text = "Click Me"
        }
        value = "click_me_123"
        actionId = "button"
      }
    }
  }
}
"""

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments(SlackPlugin.PUBLISH_TO_SLACK_TASK_NAME)
        .withPluginClasspath(pluginClasspath)
        .build()

    then:
    result.task(":publishToSlack").outcome == SUCCESS
    !result.output.contains('error')
  }

  def "post threaded message using chat api"() {
    given:
    settingsFile << baseSettingScript
    buildFile << baseBuildScript
    buildFile << """
slack {
  oauthToken = '${oauthToken}'
  message {
    channel = '${channel}'
    text = "A message potentially generating lots of noise"
  }
}

task publishThread(type: net.madeng.slack.SlackTask){
  dependsOn publishToSlack
  doFirst{
    project.slack {
      message {
        threadTs = publishToSlack.slackResponse.ts
        text = "Let us corral it with a thread"
      }
    }
  }
}

task publishThreadAgain(type: net.madeng.slack.SlackTask){
  dependsOn publishThread
  doFirst{
    project.slack {
      message {
        threadTs = publishToSlack.slackResponse.ts
        text = "Normally you'd probably just have a longer initial reply"
      }
    }
  }
}
"""

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('publishThreadAgain')
        .withPluginClasspath(pluginClasspath)
        .build()

    then:
    result.task(":publishThreadAgain").outcome == SUCCESS
    println result.output
    !result.output.contains('error')
  }
}