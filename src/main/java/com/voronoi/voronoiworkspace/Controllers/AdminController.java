package com.voronoi.voronoiworkspace.Controllers;


import com.google.common.net.HttpHeaders;
import com.voronoi.voronoiworkspace.Entities.User;
import com.voronoi.voronoiworkspace.RequestDTO.DownloadFile;
import com.voronoi.voronoiworkspace.RequestDTO.EditUser;
import com.voronoi.voronoiworkspace.RequestDTO.GetUser;
import com.voronoi.voronoiworkspace.ResponseDTO.BaseResponse;
import com.voronoi.voronoiworkspace.ResponseDTO.UserResponse;
import com.voronoi.voronoiworkspace.Services.GoogleDriveService;
import com.voronoi.voronoiworkspace.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoogleDriveService googleDriveService;



    @GetMapping("/v1/allUsers")
    public ResponseEntity<BaseResponse<UserResponse>> getAllUsers(){
        BaseResponse<UserResponse> baseResponse = new BaseResponse<>();
        UserResponse userResponse = new UserResponse();
        userResponse.setUsers(userService.getAllUsers());
        baseResponse.setResponseMessage("Users fetched Successfully!");
        baseResponse.setResponseCode(1);
        baseResponse.setResponseBody(userResponse);
        return new ResponseEntity<>(baseResponse,HttpStatus.OK);
    }

    @PostMapping("/v1/editUser")
    public ResponseEntity<BaseResponse<?>> editUser(@RequestBody EditUser user){
        return new ResponseEntity<>(userService.editUser(user),HttpStatus.OK);
    }

    @PostMapping("/v1/getUser")
    public ResponseEntity<BaseResponse<User>> getUser(@RequestBody GetUser getUser){
        return new ResponseEntity<>(userService.getUser(getUser),HttpStatus.OK);
    }

    @PostMapping("/v1/addUser")
    public ResponseEntity<BaseResponse<?>> addUser(@RequestBody EditUser user){
        return new ResponseEntity<>(userService.addUser(user),HttpStatus.OK);
    }

    @PostMapping("/v1/deleteUser")
    public ResponseEntity<BaseResponse<UserResponse>> deleteUser(@RequestBody GetUser getUser){
        BaseResponse response = new BaseResponse();
        UserResponse userResponse = new UserResponse();
        userResponse.setUsers(userService.deleteUser(getUser));
        response.setResponseBody(userResponse);
        response.setResponseCode(1);
        response.setResponseMessage("User Delete Successfully");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/v1/getAllFiles")
    public ResponseEntity<?> getAllFiles() throws GeneralSecurityException, IOException {
        return new ResponseEntity<>(googleDriveService.getfiles(),HttpStatus.OK);
    }

    @PostMapping("/v1/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) throws GeneralSecurityException, IOException {
        return new ResponseEntity<>(googleDriveService.uploadFiles(file, userId),HttpStatus.OK);
    }

//    @PostMapping("/v1/downloadFile")
//    public ResponseEntity<?> downloadFile(@RequestBody DownloadFile downloadFile) throws GeneralSecurityException, IOException {
//        return googleDriveService.downloadFile(downloadFile);
//    }

}
