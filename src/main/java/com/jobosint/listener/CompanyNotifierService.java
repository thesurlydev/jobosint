package com.jobosint.listener;


import com.jobosint.model.Company;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.sql.Connection;
import java.util.function.Consumer;

@Service
public class CompanyNotifierService {
    private static final String COMPANY_CHANNEL = "company";

    private final JdbcTemplate jdbcTemplate;

    public CompanyNotifierService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void notifyCompanyCreated(Company company) {
        jdbcTemplate.execute("NOTIFY %s, '%s'".formatted(COMPANY_CHANNEL, company.id()));
    }

    public Runnable createCompanyNotificationHandler(Consumer<PGNotification> consumer) {
        return () -> {
          jdbcTemplate.execute((Connection conn) -> {
              conn.createStatement().execute("LISTEN " + COMPANY_CHANNEL);

              PGConnection pgconn = conn.unwrap(PGConnection.class);

              while(!Thread.currentThread().isInterrupted()) {
                  PGNotification[] nts = pgconn.getNotifications(10000);
                  if (nts == null) {
                      continue;
                  }

                  for( PGNotification nt : nts) {
                      consumer.accept(nt);
                  }
              }

              return 0;
          });
        };
    }
}
