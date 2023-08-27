package com.jobosint.controller;

import com.jobosint.model.Note;
import com.jobosint.model.form.NoteForm;
import com.jobosint.repository.NoteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

@Controller
public class NoteFormController {

    private NoteRepository noteRepository;

    public NoteFormController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @PostMapping("/note")
    public RedirectView saveNote(@ModelAttribute NoteForm noteForm) {
        var note = new Note(null, noteForm.getJobId(), noteForm.getDescription(), LocalDateTime.now());
        noteRepository.save(note);
        return new RedirectView("/jobs/" + noteForm.getJobId());
    }
}
