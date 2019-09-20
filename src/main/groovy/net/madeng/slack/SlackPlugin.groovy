package net.madeng.slack

import org.gradle.api.Plugin
import org.gradle.api.Project

class SlackPlugin implements Plugin<Project> {
  static String SLACK_EXTENSION_NAME = 'slack'
  static String PUBLISH_TO_SLACK_TASK_NAME = 'publishToSlack'

  @Override
  void apply(Project project) {
    project.extensions.create(SLACK_EXTENSION_NAME, SlackExtension)

    project.tasks.create(PUBLISH_TO_SLACK_TASK_NAME, SlackTask) {st ->
      st.group = 'verification'
      st.description = 'Publish message to slack'
    }
  }
}
