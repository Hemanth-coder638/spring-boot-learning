package com.hemanth.spring_ai_lab;

import com.hemanth.spring_ai_lab.config.OpenAiConfig;
import org.springframework.ai.model.openai.autoconfigure.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        OpenAiChatAutoConfiguration.class,
        OpenAiAudioSpeechAutoConfiguration.class,
        OpenAiEmbeddingAutoConfiguration.class,
        OpenAiAudioTranscriptionAutoConfiguration.class,
        OpenAiImageAutoConfiguration.class,
        OpenAiModerationAutoConfiguration.class
})
public class SpringAiLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiLabApplication.class, args);
	}

}
