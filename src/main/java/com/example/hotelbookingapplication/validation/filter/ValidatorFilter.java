package com.example.hotelbookingapplication.validation.filter;

import com.example.hotelbookingapplication.validation.service.CheckValidPagination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@CheckValidPagination
public class ValidatorFilter {

    protected int pageNumber = 0;

    protected int pageSize = 5;

    public String toStringSizeAndNumber(){
        return String.format("pageNumber=%s&pageSize=%s",this.pageNumber,this.pageSize);
    }
}
