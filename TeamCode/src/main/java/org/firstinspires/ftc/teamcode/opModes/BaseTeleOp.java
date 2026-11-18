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
@TeleOp(name = "Base TeleOp", group = "TTLib")
public class BaseTeleOp extends CommandOpMode {

    private final ElapsedTime runtime = new ElapsedTime();
    private DriveSubsystem drive;
    private MechanismSubsystem mechanisms;

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap);
        mechanisms = new MechanismSubsystem(hardwareMap);

        GamepadEx operatorGamepad = new GamepadEx(gamepad2);

        // 1. Gamepad 1 Driving (Default Command)
        drive.setDefaultCommand(new RunCommand(
                () -> drive.drive(
                        -gamepad1.left_stick_y,
                        gamepad1.left_stick_x,
                        gamepad1.right_stick_x
                ),
                drive
        ));

        // 2. Gamepad 2 Right Bumper -> Toggle Intake (0.9 power)
        operatorGamepad.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(new InstantCommand(mechanisms::toggleIntake, mechanisms));

        // 3. Gamepad 2 Right Trigger -> Toggle RBW (-1.0 power)
        new Trigger(() -> gamepad2.right_trigger > 0.5)
                .whenActive(new InstantCommand(mechanisms::toggleRbw, mechanisms));


        // 4. Gamepad 2 Left Bumper -> Toggle Pollen Motor (0.3 power)
        operatorGamepad.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(new InstantCommand(mechanisms::togglePollen, mechanisms));

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void run() {
        if (runtime.seconds() == 0) runtime.reset();
        super.run(); // Runs TTLib's CommandScheduler

        // Telemetry matching your original OpMode
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", drive.frontLeftPower, drive.frontRightPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", drive.backLeftPower, drive.backRightPower);
        telemetry.addData("Intake Status", mechanisms.intakeOn ? "RUNNING" : "STOPPED");
        telemetry.addData("rbw Status", mechanisms.rbwOn ? "RUNNING" : "STOPPED");
        telemetry.addData("pollenMotor", mechanisms.pmOn ? "RUNNING" : "STOPPED");
        telemetry.update();
    }
}
