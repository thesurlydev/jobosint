package com.jobosint.service;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenizerService {
    public Integer countTokens(String text) {
        EncodingRegistry registry = Encodings.newLazyEncodingRegistry();
        Encoding encoding = registry.getEncodingForModel(ModelType.GPT_4);
        return encoding.countTokens(text);
    }
}
