package com.jobosint.controller;

import com.jobosint.model.ApplicationDetail;
import com.jobosint.model.ApplicationEventDetail;
import com.jobosint.model.form.ApplicationEventForm;
import com.jobosint.service.ApplicationEventService;
import com.jobosint.service.ApplicationService;
import com.jobosint.service.AttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventController {
    private final ApplicationService applicationService;
    private final ApplicationEventService applicationEventService;
    private final AttributeService attributeService;

    @GetMapping("/event")
    public String eventForm(Model model) {
        var eventForm = new ApplicationEventForm();
        model.addAttribute("eventForm", eventForm);

        prepareEventForm(model);

        return "applicationEventForm";
    }

    @GetMapping("/events/{id}/edit")
    public String editEvent(@PathVariable UUID id, Model model) {
        var maybeEvent = applicationEventService.getApplicationEventDetailById(id);
        maybeEvent.ifPresent(event -> {
            var eventForm = new ApplicationEventForm(event);
            model.addAttribute("eventForm", eventForm);
            prepareEventForm(model);
        });
        return "applicationEventForm";
    }

    @PostMapping("/event")
    public RedirectView eventSubmit(@ModelAttribute ApplicationEventForm eventForm) {
        var appEvent = eventForm.toApplicationEvent();
        applicationEventService.save(appEvent);
        return new RedirectView("events");
    }

    @GetMapping("/events")
    public String events(Model model) {
        List<ApplicationEventDetail> events = applicationEventService.getAllApplicationEventDetails();
        model.addAttribute("events", events);
        return "applicationEvents";
    }

    private void prepareEventForm(Model model) {
        Iterable<ApplicationDetail> apps = applicationService.getAllApplications();
        model.addAttribute("applicationLookup", apps);

        List<String> eventTypes = attributeService.getEventTypes();
        model.addAttribute("eventTypes", eventTypes);

        List<String> interviewTypes = attributeService.getInterviewTypes();
        model.addAttribute("interviewTypes", interviewTypes);
    }
}
