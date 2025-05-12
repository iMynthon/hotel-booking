package com.example.hotelbookingapplication.validation;

import com.example.hotelbookingapplication.validation.service.CheckValidPagination;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@CheckValidPagination
public class PaginationFilter {

    int pageNumber = 0;

    int pageSize = 10;

    public String toStringSizeAndNumber(){
        return String.format("pageNumber=%s&pageSize=%s",this.pageNumber,this.pageSize);
    }
}
