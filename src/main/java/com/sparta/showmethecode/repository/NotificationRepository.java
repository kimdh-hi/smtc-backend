package com.sparta.showmethecode.repository;

import com.sparta.showmethecode.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    List<Notification> findAllByReceiverId(Long id);
}
