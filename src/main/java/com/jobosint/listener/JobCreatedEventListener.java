package com.jobosint.listener;

import com.jobosint.event.JobCreatedEvent;
import com.jobosint.model.Job;
import com.jobosint.model.JobAttribute;
import com.jobosint.model.ai.JobAttributes;
import com.jobosint.service.JobService;
import com.jobosint.service.ai.JobAttributeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class JobCreatedEventListener implements ApplicationListener<JobCreatedEvent> {

    private final JobService jobService;
    private final JobAttributeService jobAttributesService;

    @Override
    public void onApplicationEvent(@NonNull JobCreatedEvent event) {

        log.info("Received: {}", event);

        Job job = event.getJob();
        String jobDescription = job.content();

        Optional<JobAttributes> jobAttributes = jobAttributesService.parseJobDescription(jobDescription);
        if (jobAttributes.isPresent()) {
            JobAttribute jobAttribute = getJobAttribute(jobAttributes.get(), job);
            jobAttributesService.saveJobAttributes(jobAttribute);
        }
    }

    @NotNull
    private JobAttribute getJobAttribute(JobAttributes jobAttributes, Job savedJob) {
        List<String> interviewSteps = getNonNullList(jobAttributes.interviewProcess().processSteps());
        List<String> programmingLanguages = getNonNullList(jobAttributes.technologyStack().programmingLanguages());
        List<String> databases = getNonNullList(jobAttributes.technologyStack().databases());
        List<String> frameworks = getNonNullList(jobAttributes.technologyStack().frameworks());
        List<String> cloudServices = getNonNullList(jobAttributes.technologyStack().cloudServices());
        List<String> cloudProviders = getNonNullList(jobAttributes.technologyStack().cloudProviders());
        List<String> requiredQualifications = getNonNullList(jobAttributes.jobQualifications().required());
        List<String> preferredQualifications = getNonNullList(jobAttributes.jobQualifications().preferred());
        List<String> cultureValues = getNonNullList(jobAttributes.culture().values());

        return new JobAttribute(
                null,
                savedJob.id(),
                interviewSteps,
                programmingLanguages,
                databases,
                frameworks,
                cloudServices,
                cloudProviders,
                requiredQualifications,
                preferredQualifications,
                cultureValues
        );
    }

    private List<String> getNonNullList(List<String> list) {
        return list != null ? list : Collections.emptyList();
    }
}
