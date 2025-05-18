package com.example.hotelbookingapplication.mapper;

import com.example.hotelbookingapplication.dto.request.UpsertHotelRequest;
import com.example.hotelbookingapplication.dto.response.AllHotelResponse;
import com.example.hotelbookingapplication.dto.response.HotelRatingResponse;
import com.example.hotelbookingapplication.dto.response.HotelResponse;
import com.example.hotelbookingapplication.model.jpa.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {RoomMapper.class})
public interface HotelMapper {

    Hotel requestToHotel(UpsertHotelRequest request);

    Hotel requestToHotel(Integer id,UpsertHotelRequest request);

    HotelResponse hotelToResponse(Hotel hotel);

    HotelRatingResponse hotelToRatingResponse(Hotel hotel);

    default AllHotelResponse listHotelToListHotelResponse(List<Hotel> hotels){
        return new AllHotelResponse(hotels
                .stream()
                .map(this::hotelToResponse).toList());
    }

    @Mapping(target = "rating",ignore = true)
    @Mapping(target = "numberOfRating",ignore = true)
    @Mapping(target = "rooms",ignore = true)
    void update(@MappingTarget Hotel destination, Hotel root);


}
