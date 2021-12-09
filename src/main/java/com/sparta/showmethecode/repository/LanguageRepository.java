package com.sparta.showmethecode.repository;

import com.sparta.showmethecode.domain.Language;
import com.sparta.showmethecode.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    @Query("select l.name from Language l where l.user.id = :userId")
    List<String> findByUserId(Long userId);

    List<Language> findAllByName(String name);
}
