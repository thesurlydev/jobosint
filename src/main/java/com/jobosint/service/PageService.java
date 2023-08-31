package com.jobosint.service;

import com.jobosint.event.PageCreatedEvent;
import com.jobosint.model.ext.Page;
import com.jobosint.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class PageService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PageRepository pageRepository;

    @Transactional
    public Page savePage(Page page) {
        boolean isNew = page.id() == null;
        var persistedPage = pageRepository.save(page);
        if (isNew) {
            applicationEventPublisher.publishEvent(new PageCreatedEvent(this, persistedPage));
        }
        return persistedPage;
    }

}
