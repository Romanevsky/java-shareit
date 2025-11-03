package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemIdAndBookerId(Long itemId, Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    List<Booking> findByItemOwnerId(Long ownerId);
}
