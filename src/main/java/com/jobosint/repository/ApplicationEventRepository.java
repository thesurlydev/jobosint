package com.jobosint.repository;

import com.jobosint.model.ApplicationEvent;
import com.jobosint.model.ApplicationEventDetail;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationEventRepository extends CrudRepository<ApplicationEvent, UUID> {
    @NotNull
    @Override
    List<ApplicationEvent> findAll();

    String APP_EVENT_DETAIL_SELECT = """
            select ae.id, ae.event_date, ae.event_type, ae.notes, ae.interview_type,
                    a.id as applicationId,
                    j.id as jobId, j.title as jobTitle,
                    c.id as companyId, c.name as companyName
             from jobosint.public.application a,
                  jobosint.public.application_event ae,
                  jobosint.public.company c,
                  jobosint.public.job j
             where a.id = ae.application and a.job = j.id and j.company = c.id
            """;

    @Query(APP_EVENT_DETAIL_SELECT + " order by ae.event_date desc")
    List<ApplicationEventDetail> findAllApplicationEventDetails();

    @Query(APP_EVENT_DETAIL_SELECT + " and ae.id = :id")
    Optional<ApplicationEventDetail> findApplicationEventDetailById(UUID id);
}
