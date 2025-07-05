package com.example.demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
public class Shot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Basic shot information
    private Integer shotNumber;
    private String club;
    private String clubDescription;     // Added for Awesome Golf
    private LocalDateTime shotTime;      // Timestamp of shot
    private Double altitude;             // Altitude in feet
    
    // Key metrics from Garmin R10
    private Double ballSpeed;            // mph
    private Double clubHeadSpeed;        // mph
    private Double launchAngle;          // degrees
    private Double launchDirection;      // degrees (+ right, - left)
    private Double spinRate;             // rpm
    private Double spinAxis;             // degrees
    private Double carryDistance;        // yards
    private Double totalDistance;        // yards
    private Double rollDistance;         // yards (Added for Awesome Golf)
    private Double deviation;            // feet (+ right, - left)
    private Double apex;                 // feet
    
    // Advanced metrics
    private Double attackAngle;          // degrees
    private Double faceAngle;            // degrees
    private Double faceToPath;           // degrees
    private Double swingPath;            // degrees
    private Double swingPlane;           // degrees
    private Double verticalFaceImpact;   // inches from center
    private Double horizontalFaceImpact; // inches from center
    
    // Awesome Golf specific metrics
    private Double smash;                // Smash factor
    private Double peakHeight;           // Peak height in feet
    private Double descentAngle;         // Descent angle in degrees
    private Double horizontalLaunch;     // Horizontal launch angle in degrees
    private Double carryLateralDistance; // Lateral carry distance in yards
    private Double totalLateralDistance; // Total lateral distance in yards
    private Double carryCurveDistance;   // Carry curve distance in yards
    private Double totalCurveDistance;   // Total curve distance in yards
    private Double dynamicLoft;          // Dynamic loft in degrees
    private Double spinLoft;             // Spin loft in degrees
    private Double lowPoint;             // Low point in inches
    private Double faceTarget;           // Face to target in degrees
    private Double swingPlaneTilt;       // Swing plane tilt in degrees
    private Double swingPlaneRotation;   // Swing plane rotation in degrees
    private String shotClassification;   // Classification of the shot (e.g., "Push Slice")
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    @JsonIgnore  // Prevent infinite recursion when serializing
    private Session session;
    
    // Default constructor required by JPA
    public Shot() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getShotNumber() {
        return shotNumber;
    }

    public void setShotNumber(Integer shotNumber) {
        this.shotNumber = shotNumber;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getClubDescription() {
        return clubDescription;
    }

    public void setClubDescription(String clubDescription) {
        this.clubDescription = clubDescription;
    }

    public LocalDateTime getShotTime() {
        return shotTime;
    }

    public void setShotTime(LocalDateTime shotTime) {
        this.shotTime = shotTime;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getRollDistance() {
        return rollDistance;
    }

    public void setRollDistance(Double rollDistance) {
        this.rollDistance = rollDistance;
    }

    public Double getSmash() {
        return smash;
    }

    public void setSmash(Double smash) {
        this.smash = smash;
    }

    public Double getPeakHeight() {
        return peakHeight;
    }

    public void setPeakHeight(Double peakHeight) {
        this.peakHeight = peakHeight;
    }

    public Double getDescentAngle() {
        return descentAngle;
    }

    public void setDescentAngle(Double descentAngle) {
        this.descentAngle = descentAngle;
    }

    public Double getHorizontalLaunch() {
        return horizontalLaunch;
    }

    public void setHorizontalLaunch(Double horizontalLaunch) {
        this.horizontalLaunch = horizontalLaunch;
    }

    public Double getCarryLateralDistance() {
        return carryLateralDistance;
    }

    public void setCarryLateralDistance(Double carryLateralDistance) {
        this.carryLateralDistance = carryLateralDistance;
    }

    public Double getTotalLateralDistance() {
        return totalLateralDistance;
    }

    public void setTotalLateralDistance(Double totalLateralDistance) {
        this.totalLateralDistance = totalLateralDistance;
    }

    public Double getCarryCurveDistance() {
        return carryCurveDistance;
    }

    public void setCarryCurveDistance(Double carryCurveDistance) {
        this.carryCurveDistance = carryCurveDistance;
    }

    public Double getTotalCurveDistance() {
        return totalCurveDistance;
    }

    public void setTotalCurveDistance(Double totalCurveDistance) {
        this.totalCurveDistance = totalCurveDistance;
    }

    public Double getDynamicLoft() {
        return dynamicLoft;
    }

    public void setDynamicLoft(Double dynamicLoft) {
        this.dynamicLoft = dynamicLoft;
    }

    public Double getSpinLoft() {
        return spinLoft;
    }

    public void setSpinLoft(Double spinLoft) {
        this.spinLoft = spinLoft;
    }

    public Double getLowPoint() {
        return lowPoint;
    }

    public void setLowPoint(Double lowPoint) {
        this.lowPoint = lowPoint;
    }

    public Double getFaceTarget() {
        return faceTarget;
    }

    public void setFaceTarget(Double faceTarget) {
        this.faceTarget = faceTarget;
    }

    public Double getSwingPlaneTilt() {
        return swingPlaneTilt;
    }

    public void setSwingPlaneTilt(Double swingPlaneTilt) {
        this.swingPlaneTilt = swingPlaneTilt;
    }

    public Double getSwingPlaneRotation() {
        return swingPlaneRotation;
    }

    public void setSwingPlaneRotation(Double swingPlaneRotation) {
        this.swingPlaneRotation = swingPlaneRotation;
    }

    public String getShotClassification() {
        return shotClassification;
    }

    public void setShotClassification(String shotClassification) {
        this.shotClassification = shotClassification;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Double getBallSpeed() {
        return ballSpeed;
    }

    public void setBallSpeed(Double ballSpeed) {
        this.ballSpeed = ballSpeed;
    }

    public Double getClubHeadSpeed() {
        return clubHeadSpeed;
    }

    public void setClubHeadSpeed(Double clubHeadSpeed) {
        this.clubHeadSpeed = clubHeadSpeed;
    }

    public Double getLaunchAngle() {
        return launchAngle;
    }

    public void setLaunchAngle(Double launchAngle) {
        this.launchAngle = launchAngle;
    }

    public Double getLaunchDirection() {
        return launchDirection;
    }

    public void setLaunchDirection(Double launchDirection) {
        this.launchDirection = launchDirection;
    }

    public Double getSpinRate() {
        return spinRate;
    }

    public void setSpinRate(Double spinRate) {
        this.spinRate = spinRate;
    }

    public Double getSpinAxis() {
        return spinAxis;
    }

    public void setSpinAxis(Double spinAxis) {
        this.spinAxis = spinAxis;
    }

    public Double getCarryDistance() {
        return carryDistance;
    }

    public void setCarryDistance(Double carryDistance) {
        this.carryDistance = carryDistance;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Double getDeviation() {
        return deviation;
    }

    public void setDeviation(Double deviation) {
        this.deviation = deviation;
    }

    public Double getApex() {
        return apex;
    }

    public void setApex(Double apex) {
        this.apex = apex;
    }

    public Double getAttackAngle() {
        return attackAngle;
    }

    public void setAttackAngle(Double attackAngle) {
        this.attackAngle = attackAngle;
    }

    public Double getFaceAngle() {
        return faceAngle;
    }

    public void setFaceAngle(Double faceAngle) {
        this.faceAngle = faceAngle;
    }

    public Double getFaceToPath() {
        return faceToPath;
    }

    public void setFaceToPath(Double faceToPath) {
        this.faceToPath = faceToPath;
    }

    public Double getSwingPath() {
        return swingPath;
    }

    public void setSwingPath(Double swingPath) {
        this.swingPath = swingPath;
    }

    public Double getSwingPlane() {
        return swingPlane;
    }

    public void setSwingPlane(Double swingPlane) {
        this.swingPlane = swingPlane;
    }

    public Double getVerticalFaceImpact() {
        return verticalFaceImpact;
    }

    public void setVerticalFaceImpact(Double verticalFaceImpact) {
        this.verticalFaceImpact = verticalFaceImpact;
    }

    public Double getHorizontalFaceImpact() {
        return horizontalFaceImpact;
    }

    public void setHorizontalFaceImpact(Double horizontalFaceImpact) {
        this.horizontalFaceImpact = horizontalFaceImpact;
    }
}