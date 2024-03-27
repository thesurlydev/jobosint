package com.jobosint.repository;

import com.jobosint.model.Contact;
import com.jobosint.model.ContactDetail;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ContactRepository extends CrudRepository<Contact, UUID> {

    String CONTACT_DETAIL_SELECT = """
    select c.*, co.name, co.website_url, co.location, co.summary, co.stock_ticker, co.employee_count
    from jobosint.public.contact c, jobosint.public.company co
    where c.company = co.id order by c.full_name""";

    @NotNull
    @Override
    List<Contact> findAll();

    @Query(CONTACT_DETAIL_SELECT)
    List<ContactDetail> findAllContactDetailOrderByFullName();
}
