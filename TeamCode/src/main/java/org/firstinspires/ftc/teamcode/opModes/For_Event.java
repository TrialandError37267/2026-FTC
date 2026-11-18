package org.firstinspires.ftc.teamcode.opModes;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.button.Trigger;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.MechanismSubsystem;

// Click any red class names below and press Alt+Enter (Mac: Option+Return) to auto-import
@TeleOp(name = "TTLib: Omni TeleOp", group = "TTLib")
public class For_Event extends CommandOpMode {

    private final ElapsedTime runtime = new ElapsedTime();
    private DriveSubsystem drive;
    private MechanismSubsystem mechanisms;

    private boolean slowMode=false;
    private boolean lastBumperState=false;

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap);
        mechanisms = new MechanismSubsystem(hardwareMap);

        GamepadEx operatorGamepad = new GamepadEx(gamepad2);

        // 1. Gamepad 1 Driving (Default Command)
        drive.setDefaultCommand(new RunCommand(
                () -> {
                    // Check for a fresh press (rising-edge) on the Left Bumper
                    boolean currentBumperState = gamepad1.dpad_left;
                    if (currentBumperState && !lastBumperState) {
                        slowMode = !slowMode; // Flips between true and false
                    }
                    lastBumperState = currentBumperState;

                    // Set speed multiplier based on toggle state (40% speed when ON)
                    double speedMultiplier = slowMode ? 0.4 : 1.0;

                    drive.drive(
                            -gamepad1.left_stick_y * speedMultiplier,
                            gamepad1.left_stick_x * speedMultiplier,
                            gamepad1.right_stick_x * speedMultiplier
                    );
                },
                drive
        ));


        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void run() {
        if (runtime.seconds() == 0) runtime.reset();
        super.run(); // Runs TTLib's CommandScheduler

        // Telemetry matching your original OpMode
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Slow Mode", slowMode ? "ON (40%)" : "OFF (100%)");
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", drive.frontLeftPower, drive.frontRightPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", drive.backLeftPower, drive.backRightPower);
        telemetry.update();
    }
}
