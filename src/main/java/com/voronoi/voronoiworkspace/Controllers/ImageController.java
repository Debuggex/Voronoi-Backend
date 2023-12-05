package com.voronoi.voronoiworkspace.Controllers;

import com.voronoi.voronoiworkspace.Services.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Controller
@RequestMapping("/admin")
public class ImageController {

    @Autowired
    private GoogleDriveService googleDriveService;

    @GetMapping("/v1/previewImage/{fileId}")
    public String previewImage(@PathVariable String fileId, Model model) throws GeneralSecurityException, IOException {
        byte[] fileContent = googleDriveService.downloadFile(fileId);
        // Encode the byte array as a data URI
        String dataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent);

        // Add the data URI to the model
        model.addAttribute("dataUri", dataUri);
        return "previewImage.html";
    }
}
