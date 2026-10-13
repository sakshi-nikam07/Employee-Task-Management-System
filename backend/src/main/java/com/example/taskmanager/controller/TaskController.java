package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Set<String> STATUSES = Set.of("TODO", "IN_PROGRESS", "DONE");

    private final TaskRepository tasks;
    private final UserRepository users;

    public TaskController(TaskRepository tasks, UserRepository users) {
        this.tasks = tasks;
        this.users = users;
    }

    @GetMapping
    public List<Task> list(@AuthenticationPrincipal User me) {
        return "ADMIN".equals(me.getRole()) ? tasks.findAll()
                                            : tasks.findByAssignedToId(me.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Task create(@RequestBody Task t, @RequestParam Long assignedTo) {
        t.setId(null);
        t.setAssignedTo(users.findById(assignedTo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found")));
        return tasks.save(t);
    }

    @PutMapping("/{id}/status")
    public Task updateStatus(@PathVariable Long id, @RequestParam String status,
                             @AuthenticationPrincipal User me) {
        if (!STATUSES.contains(status))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        Task t = tasks.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        boolean owner = t.getAssignedTo() != null && t.getAssignedTo().getId().equals(me.getId());
        if (!owner && !"ADMIN".equals(me.getRole()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        t.setStatus(status);
        return tasks.save(t);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { tasks.deleteById(id); }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employees")
    public List<User> employees() { return users.findAll(); }
}
