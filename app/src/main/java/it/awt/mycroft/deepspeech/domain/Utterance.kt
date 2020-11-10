package it.awt.mycroft.deepspeech.domain

class Utterance(val actor: UtteranceActor, val text: String)

enum class UtteranceActor {
    USER,
    MYCROFT
}