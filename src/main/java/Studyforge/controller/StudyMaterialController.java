package Studyforge.controller;

import Studyforge.model.StudyMaterial;
import Studyforge.service.StudyMaterialService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/courses/{courseId}/materials")
@CrossOrigin(origins = "http://localhost:5173")
public class StudyMaterialController {

    private final StudyMaterialService studyMaterialService;

    public StudyMaterialController(
            StudyMaterialService studyMaterialService) {

        this.studyMaterialService = studyMaterialService;
    }

    @GetMapping
    public List<StudyMaterial> getMaterials(
            @PathVariable Long courseId) {

        return studyMaterialService
                .getMaterialsByCourse(courseId);
    }

    @PostMapping
    public StudyMaterial uploadMaterial(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file)
            throws IOException {

        return studyMaterialService
                .uploadMaterial(courseId, file);
    }

    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable Long courseId,
            @PathVariable Long materialId) {

        boolean deleted =
                studyMaterialService.deleteMaterial(materialId);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}