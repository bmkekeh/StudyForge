package Studyforge.service;

import Studyforge.model.Course;
import Studyforge.model.StudyMaterial;
import Studyforge.repository.CourseRepository;
import Studyforge.repository.StudyMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@Service
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final CourseRepository courseRepository;

    public StudyMaterialService(
            StudyMaterialRepository studyMaterialRepository,
            CourseRepository courseRepository) {

        this.studyMaterialRepository = studyMaterialRepository;
        this.courseRepository = courseRepository;
    }

    public List<StudyMaterial> getMaterialsByCourse(Long courseId) {
        return studyMaterialRepository.findByCourseId(courseId);
    }

    public StudyMaterial uploadMaterial(
            Long courseId,
            MultipartFile file) throws IOException {

        Course course = courseRepository
                .findById(courseId)
                .orElse(null);

        if (course == null) {
            return null;
        }

        String originalFileName = file.getOriginalFilename();

        String storedFileName =
                UUID.randomUUID() + "_" + originalFileName;

        Path uploadDirectory = Paths.get("uploads");

        Files.createDirectories(uploadDirectory);

        Path filePath =
                uploadDirectory.resolve(storedFileName);

        file.transferTo(filePath);

        String extractedText;

        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            extractedText = stripper.getText(document);
        }

        StudyMaterial material = new StudyMaterial(
                originalFileName,
                filePath.toString(),
                course
        );

        material.setExtractedText(extractedText);

        return studyMaterialRepository.save(material);
    }

    public boolean deleteMaterial(Long materialId) {

        StudyMaterial material = studyMaterialRepository
                .findById(materialId)
                .orElse(null);

        if (material == null) {
            return false;
        }

        // Delete the actual uploaded file
        try {
            Path path = Paths.get(material.getFilePath());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not delete uploaded file.",
                    e
            );
        }

        // Delete database record
        studyMaterialRepository.delete(material);

        return true;
    }
}