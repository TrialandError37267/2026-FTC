package org.firstinspires.ftc.teamcode.subsystems;

import android.util.Size;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VisionSubsystem extends SubsystemBase {
    private final VisionPortal visionPortal;
    private final AprilTagProcessor aprilTagProcessor;
    private List<AprilTagDetection> latestDetections = Collections.emptyList();
    private boolean exposureLocked = false;

    // 1. Set this to the exact black-border width of the BIOBUZZ AprilTag sticker (in inches)
    private static final double BIOBUZZ_TAG_SIZE_INCHES = 3.0;

    // 2. Replace with your AR0234 checkerboard calibration numbers at 640x480
    private static final double FX = 578.272;
    private static final double FY = 578.272;
    private static final double CX = 320.0;
    private static final double CY = 240.0;

    public VisionSubsystem(HardwareMap hardwareMap) {
        // Register BIOBUZZ tag IDs (0-30) so SDK 9.2.0 computes full ftcPose for every tag
        AprilTagLibrary.Builder libraryBuilder = new AprilTagLibrary.Builder();
        for (int id = 0; id <= 30; id++) {
            libraryBuilder.addTag(id, "BioBuzzTag_" + id, BIOBUZZ_TAG_SIZE_INCHES, DistanceUnit.INCH);
        }

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagLibrary(libraryBuilder.build())
                .setLensIntrinsics(FX, FY, CX, CY)
                .setDrawAxes(true)
                .setDrawTagID(true)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .addProcessor(aprilTagProcessor)
                .build();
    }

    @Override
    public void periodic() {
        if (!exposureLocked && visionPortal.getCameraState() == VisionPortal.CameraState.STREAMING) {
            lockExposure(6, 250);
            exposureLocked = true;
        }
        latestDetections = aprilTagProcessor.getDetections();
    }

    private void lockExposure(int exposureMs, int gain) {
        ExposureControl exp = visionPortal.getCameraControl(ExposureControl.class);
        if (exp.getMode() != ExposureControl.Mode.Manual) {
            exp.setMode(ExposureControl.Mode.Manual);
        }
        exp.setExposure(exposureMs, TimeUnit.MILLISECONDS);

        GainControl gc = visionPortal.getCameraControl(GainControl.class);
        gc.setGain(gain);
    }

    // --- New Methods for Reading & Sorting Multiple Tags in a Row ---

    // Returns all currently visible tags sorted Left -> Right across the camera frame
    public List<AprilTagDetection> getTagsLeftToRight() {
        List<AprilTagDetection> sorted = new ArrayList<>(latestDetections);
        Collections.sort(sorted, Comparator.comparingDouble(tag -> tag.center.x));
        return sorted;
    }

    // Returns just the IDs of all visible tags in Left -> Right order (e.g., [12, 13, 14, 15])
    public List<Integer> getVisibleTagIdsLeftToRight() {
        List<Integer> ids = new ArrayList<>();
        for (AprilTagDetection tag : getTagsLeftToRight()) {
            ids.add(tag.id);
        }
        return ids;
    }

    // Returns the ID of the tag closest to the center of your camera (great for aiming!)
    public int getCenterMostTagId() {
        AprilTagDetection best = null;
        double smallestOffset = Double.MAX_VALUE;

        for (AprilTagDetection tag : latestDetections) {
            double offsetFromCenter = Math.abs(tag.center.x - CX);
            if (offsetFromCenter < smallestOffset) {
                smallestOffset = offsetFromCenter;
                best = tag;
            }
        }
        return (best != null) ? best.id : -1; // Returns -1 if no tags are visible
    }

    // --- Single-Tag Lookup & Position Getters ---

    public AprilTagDetection getTagById(int id) {
        for (AprilTagDetection tag : latestDetections) {
            if (tag.id == id && tag.ftcPose != null) {
                return tag;
            }
        }
        return null;
    }

    public boolean hasTag(int id) {
        return getTagById(id) != null;
    }

    public double getForwardInches(int id) {
        AprilTagDetection tag = getTagById(id);
        return (tag != null) ? tag.ftcPose.y : 0.0;
    }

    public double getStrafeInches(int id) {
        AprilTagDetection tag = getTagById(id);
        return (tag != null) ? tag.ftcPose.x : 0.0;
    }

    public double getRangeInches(int id) {
        AprilTagDetection tag = getTagById(id);
        return (tag != null) ? tag.ftcPose.range : 0.0;
    }

    public double getBearingDeg(int id) {
        AprilTagDetection tag = getTagById(id);
        return (tag != null) ? tag.ftcPose.bearing : 0.0;
    }

    public double getYawDeg(int id) {
        AprilTagDetection tag = getTagById(id);
        return (tag != null) ? tag.ftcPose.yaw : 0.0;
    }

    public void closeCamera() {
        visionPortal.close();
    }
}
