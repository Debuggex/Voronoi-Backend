package com.voronoi.voronoiworkspace.ResponseDTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<ResponseType> {

    private Integer responseCode;

    private String responseMessage;

    private ResponseType responseBody;
}
