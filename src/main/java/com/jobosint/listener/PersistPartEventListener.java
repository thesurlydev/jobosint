package com.jobosint.listener;

import com.jobosint.event.PersistPartEvent;
import com.jobosint.model.Part;
import com.jobosint.service.PartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersistPartEventListener implements ApplicationListener<PersistPartEvent> {

    private final PartService partService;

    @Override
    public void onApplicationEvent(PersistPartEvent event) {
        Part part = event.getPart();
        partService.savePart(part);
    }
}
