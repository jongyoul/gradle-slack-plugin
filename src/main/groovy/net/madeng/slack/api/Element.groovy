package net.madeng.slack.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class Element {

    String type
    String actionId
    String initialDate
    String imageUrl
    String altText
    String value
    String url
    Text text
    @JsonIgnore
    private String textString
    Text placeholder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    def options = []

    void placeholder(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        placeholder = new Text()
        closure.delegate = placeholder
        closure()
    }

    Object getText(){
        if(textString){
            return textString
        } else {
            return text
        }
    }

    void setText(String text) {
        textString = text
    }

    void text(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        text = new Text()
        closure.delegate = text
        closure()
    }

    void option(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        def option = new Option()
        closure.delegate = option
        options << option
        closure()
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Option {
        String value
        Text text
        String dismissText

        void text(Closure closure) {
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            text = new Text()
            closure.delegate = text
            closure()
        }
    }

}
