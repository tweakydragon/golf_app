package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.model.DataSource;

public record SessionDTO(
        Long id,
        String title,
        LocalDateTime uploadDate,
        LocalDateTime sessionDate,
        String location,
        DataSource sourceType,
        List<ShotDTO> shots
) {}
