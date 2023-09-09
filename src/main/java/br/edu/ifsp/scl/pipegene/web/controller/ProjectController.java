package br.edu.ifsp.scl.pipegene.web.controller;

import br.edu.ifsp.scl.pipegene.domain.Project;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.project.ProjectCRUD;
import br.edu.ifsp.scl.pipegene.web.model.account.request.CreateUserRequest;
import br.edu.ifsp.scl.pipegene.web.model.project.ProjectResponse;
import br.edu.ifsp.scl.pipegene.web.model.project.ProjectUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestMapping("/api/v1/projects")
@RestController
public class ProjectController {

    private final ProjectCRUD projectCRUD;

    public ProjectController(ProjectCRUD projectCRUD) {
        this.projectCRUD = projectCRUD;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createNewProject(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam List<MultipartFile> files,
            @RequestParam List<String> usernameList) {
        System.out.println(name);
        Project project = projectCRUD.createNewProject(name, description, files, usernameList);

        return ResponseEntity.ok(ProjectResponse.createFromProject(project));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listAllProjects() {
        List<Project> projects = projectCRUD.findAllProjects();

        return ResponseEntity.ok(
                projects.stream()
                        .map(ProjectResponse::createFromProject)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> findProjectById(@PathVariable UUID projectId) {
        Project project = projectCRUD.findProjectById(projectId);

        return ResponseEntity.ok(ProjectResponse.createFromProject(project));
    }

    @GetMapping("/pipelines/{pipelineId}")
    public ResponseEntity<ProjectResponse> findProjectByPipelineId(@PathVariable UUID pipelineId) {
        Project project = projectCRUD.findProjectByPipelineId(pipelineId);

        return ResponseEntity.ok(ProjectResponse.createFromProject(project));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProjectById(@PathVariable UUID projectId,
                                                             @RequestBody @Valid ProjectUpdateRequest request) {
        Project project = projectCRUD.updateProjectById(projectId, request);

        return ResponseEntity.ok(ProjectResponse.createFromProject(project));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> deleteProjectById(@PathVariable UUID projectId) {
        projectCRUD.deleteProjectById(projectId);

        return ResponseEntity.ok().build();
    }

}
