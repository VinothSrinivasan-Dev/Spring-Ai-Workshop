package dev.vinothsrini.Spring_Ai_Workshop.multimodal;

import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
//import org.springframework.ai.openai.api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioGeneration {

    private final OpenAiAudioSpeechModel speechModel;
    public AudioGeneration(OpenAiAudioSpeechModel speechModel) {
        this.speechModel = speechModel;
    }

    @GetMapping("/speak")
    public ResponseEntity<byte[]> speak(@RequestParam(defaultValue = "It's a great time to be a Java & Spring Developer") String text) {
        var options= OpenAiAudioSpeechOptions.builder()
                .model("")
//                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
//                .responseFormat(OpenAiAudio.SpeechRequest.AudioResponseFormat.MP3)
//                .speed(1.0f)
                .build();

        TextToSpeechPrompt prompt=new TextToSpeechPrompt(text,options);
        TextToSpeechResponse speechResponse = speechModel.call(prompt);

        byte[] audioBytes = speechResponse.getResult().getOutput();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,"audio/mpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=speech.mp3")
                .body(audioBytes);


    }
}
