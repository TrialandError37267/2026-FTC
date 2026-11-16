package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

// Click "SubsystemBase" below and press Alt+Enter (Mac: Option+Return) to auto-import
public class MechanismSubsystem extends SubsystemBase {
    private final DcMotor intakeMotor;
    private final DcMotor pollenMotor;
    private final CRServo rbw;

    public boolean intakeOn = false;
    public boolean pmOn = false;
    public boolean rbwOn = false;

    public MechanismSubsystem(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        pollenMotor = hardwareMap.get(DcMotor.class, "pollenMotor");
        rbw = hardwareMap.get(CRServo.class, "rbw");

        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        pollenMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void toggleIntake() {
        intakeOn = !intakeOn;
        intakeMotor.setPower(intakeOn ? 0.9 : 0.0);
    }

    public void toggleRbw() {
        rbwOn = !rbwOn;
        rbw.setPower(rbwOn ? -1.0 : 0.0);
    }

    public void togglePollen() {
        pmOn = !pmOn;
        pollenMotor.setPower(pmOn ? 0.3 : 0.0);
    }
}
