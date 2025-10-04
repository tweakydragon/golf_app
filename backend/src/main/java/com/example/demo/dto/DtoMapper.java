package com.example.demo.dto;

import com.example.demo.model.Session;
import com.example.demo.model.Shot;
import java.util.List;
import java.util.stream.Collectors;

public final class DtoMapper {
    private DtoMapper() {}

    public static SessionDTO toSessionDTO(Session session, boolean includeShots) {
        List<ShotDTO> shotDtos = includeShots ? session.getShots().stream()
                .map(DtoMapper::toShotDTO)
                .collect(Collectors.toList()) : List.of();
        return new SessionDTO(
                session.getId(),
                session.getTitle(),
                session.getUploadDate(),
                session.getSessionDate(),
                session.getLocation(),
                session.getSourceType(),
                shotDtos
        );
    }

    public static SessionDTO toSessionDTO(Session session) { return toSessionDTO(session, false); }

    public static ShotDTO toShotDTO(Shot s) {
        return new ShotDTO(
                s.getId(),
                s.getShotNumber(),
                s.getClub(),
                s.getClubDescription(),
                s.getShotTime(),
                s.getBallSpeed(),
                s.getClubHeadSpeed(),
                s.getLaunchAngle(),
                s.getLaunchDirection(),
                s.getSpinRate(),
                s.getSpinAxis(),
                s.getCarryDistance(),
                s.getTotalDistance(),
                s.getRollDistance(),
                s.getDeviation(),
                s.getApex(),
                s.getAttackAngle(),
                s.getFaceAngle(),
                s.getFaceToPath(),
                s.getSwingPath(),
                s.getSwingPlane(),
                s.getVerticalFaceImpact(),
                s.getHorizontalFaceImpact(),
                s.getSmash(),
                s.getPeakHeight(),
                s.getDescentAngle(),
                s.getHorizontalLaunch(),
                s.getCarryLateralDistance(),
                s.getTotalLateralDistance(),
                s.getCarryCurveDistance(),
                s.getTotalCurveDistance(),
                s.getDynamicLoft(),
                s.getSpinLoft(),
                s.getLowPoint(),
                s.getFaceTarget(),
                s.getSwingPlaneTilt(),
                s.getSwingPlaneRotation(),
                s.getShotClassification()
        );
    }
}
