package com.jobosint.repository;

import com.jobosint.model.ApplicationEvent;
import com.jobosint.model.ApplicationEventDetail;
import com.jobosint.model.ApplicationEventParticipant;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationEventRepository extends ListCrudRepository<ApplicationEvent, UUID> {
    @NotNull
    @Override
    List<ApplicationEvent> findAll();

    String APP_EVENT_DETAIL_SELECT = """
            select ae.id,
                   ae.event_date,
                   ae.event_type,
                   ae.notes,
                   ae.interview_type,
                   ae.interviewer_id,
                   a.id              as applicationId,
                   j.id              as jobId,
                   j.title           as jobTitle,
                   c.id              as companyId,
                   c.name            as companyName,
                   contact.full_name as interviewerFullName,
                   contact.title     as interviewerTitle
            from jobosint.public.application_event ae
                     inner join jobosint.public.application a on a.id = ae.application
                     inner join jobosint.public.job j on a.job = j.id
                     inner join jobosint.public.company c on j.company = c.id
                     LEFT OUTER JOIN jobosint.public.contact contact on ae.interviewer_id = contact.id
            """;

    @Query(APP_EVENT_DETAIL_SELECT + " order by ae.event_date desc")
    List<ApplicationEventDetail> findAllApplicationEventDetails();

    @Query(APP_EVENT_DETAIL_SELECT + " where ae.id = :id")
    Optional<ApplicationEventDetail> findApplicationEventDetailById(UUID id);
}