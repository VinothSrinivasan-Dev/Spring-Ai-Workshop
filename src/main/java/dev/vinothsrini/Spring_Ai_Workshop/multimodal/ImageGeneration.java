package dev.vinothsrini.Spring_Ai_Workshop.multimodal;

import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ImageGeneration {

    private OpenAiImageModel imageModel;

    public ImageGeneration(OpenAiImageModel imageModel) {
        this.imageModel= imageModel;
    }

    @GetMapping("/generate-image")
    public ResponseEntity<Map<String,String>> generateImage(@RequestParam(defaultValue = "A beautiful sunset over mountains same like kaniyakumari with thiruvalluvar statue") String prompt){

        ImageOptions options= OpenAiImageOptions.builder()
                .model("gpt-image-1-mini")
                .width(1024)
                .height(1024)
                .quality("auto")
//                .style("vivid")
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(prompt,options);
        ImageResponse imageResponse = imageModel.call(imagePrompt);

        String uri=imageResponse.getResult().getOutput().getUrl();

        return ResponseEntity.ok(Map.of(
                "prompt",prompt,
                "imageUrl",uri
        ));

    }
}
