package net.madeng.gradle.plugin.slack

import org.gradle.api.Plugin
import org.gradle.api.Project

class SlackPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    SlackExtension slackExtension = project.extensions.create('slack', SlackExtension)

    project.tasks.create('publishToSlack', SlackTask) {st ->
      description 'Publish message to slack'
    }
  }
}
