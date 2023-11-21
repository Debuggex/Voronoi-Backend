package com.voronoi.voronoiworkspace.ResponseDTO;

import com.voronoi.voronoiworkspace.Entities.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponse {
    List<User> users;
}
