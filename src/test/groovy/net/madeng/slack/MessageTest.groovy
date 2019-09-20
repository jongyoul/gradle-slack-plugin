package net.madeng.slack

import groovy.json.JsonSlurper
import spock.lang.Specification

class MessageTest extends Specification {

    def "message json"() {
        given:
        def extension = new SlackExtension()
        extension.message {
            text = "a message from slack"
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

        when:
        def result = new JsonSlurper().parseText(SlackTask.messageJson(extension.message))

        then:
        result.blocks.size() == 5
        result.blocks[1].accessory.type == 'image'
        result.blocks[4].type == 'context'
        result.blocks[4].elements[0].type == 'image'
        result.blocks[4].elements[0].alt_text == 'images'
        result.blocks[4].elements[1].type == 'mrkdwn'
    }

}
