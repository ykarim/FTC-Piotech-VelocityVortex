package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Servo;

import org.lasarobotics.vision.ftc.resq.Beacon;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;

import static java.lang.System.currentTimeMillis;

public class RobotUtilities {

    private Robot robot = null;

    public boolean continuousIntake = false;
    public boolean continuousShoot = false;

    private boolean leftBeaconPusherExtended = false;
    private boolean rightBeaconPusherExtended = false;

    private boolean lightLED = false;

    public RobotUtilities(Robot robot) {
        this.robot = robot;
    }

    public void capBall() {
        //TODO
    }

    public void toggleBeaconPresser(Servo servo) {
        if (servo == robot.leftBeacon) {
            if (!leftBeaconPusherExtended) {
                RobotConstants.leftBeaconPusherPosition = RobotConstants.SERVO_MAX;
                servo.setPosition(RobotConstants.leftBeaconPusherPosition);
                leftBeaconPusherExtended = true;
            } else {
                RobotConstants.leftBeaconPusherPosition = RobotConstants.SERVO_MIN;
                servo.setPosition(RobotConstants.leftBeaconPusherPosition);
                leftBeaconPusherExtended = false;
            }
        } else if (servo == robot.rightBeacon) {
            if (!rightBeaconPusherExtended) {
                RobotConstants.rightBeaconPusherPosition = RobotConstants.SERVO_MAX;
                servo.setPosition(RobotConstants.rightBeaconPusherPosition);
                rightBeaconPusherExtended = true;
            } else {
                RobotConstants.rightBeaconPusherPosition = RobotConstants.SERVO_MIN;
                servo.setPosition(RobotConstants.rightBeaconPusherPosition);
                rightBeaconPusherExtended = false;
            }
        }
    }

    public void toggleLightLED() {
        if (lightLED) {
            lightLED = false;
            robot.lightSensor.enableLed(lightLED);
        } else {
            lightLED = true;
            robot.lightSensor.enableLed(lightLED);
        }
    }

    public void pushBeaconButton(Beacon.BeaconAnalysis analysis, Robot.TeamColor teamColor) {
        boolean leftBlue, leftRed, rightBlue, rightRed;

        leftBlue = analysis.isLeftBlue();
        leftRed = analysis.isLeftRed();
        rightBlue = analysis.isRightBlue();
        rightRed = analysis.isRightRed();

        if (teamColor == Robot.TeamColor.BLUE) {
            if (leftBlue) {
                toggleBeaconPresser(robot.leftBeacon);
                toggleBeaconPresser(robot.leftBeacon);
            } else if (rightBlue) {
                toggleBeaconPresser(robot.rightBeacon);
                toggleBeaconPresser(robot.rightBeacon);
            }
        } else if (teamColor == Robot.TeamColor.RED) {
            if (leftRed) {
                toggleBeaconPresser(robot.leftBeacon);
                toggleBeaconPresser(robot.leftBeacon);
            } else if (rightRed) {
                toggleBeaconPresser(robot.rightBeacon);
                toggleBeaconPresser(robot.rightBeacon);
            }
        }
    }

    public void shootBall(LinearVisionOpMode opMode) {
        shootBalls(true);
        waitFor(opMode, RobotConstants.shotWaitPeriod);

        intakeBalls(true);
        waitFor(opMode, 3);

        intakeBalls(false);
        shootBalls(false);
    }

    public void shootDoubleBall(LinearVisionOpMode opMode) {
        shootBalls(true);
        waitFor(opMode, RobotConstants.shotWaitPeriod);

        intakeBalls(true);
        waitFor(opMode, 5);

        intakeBalls(false);
        shootBalls(false);
    }

    public void intakeBalls(boolean condition) {
        if (condition) {
            robot.intake.setPower(-RobotConstants.intakeSpeed);
        } else {
            robot.intake.setPower(0);
        }
    }

    public void shootBalls(boolean condition) {
        if (condition) {
            robot.shoot.setPower(-RobotConstants.shootSpeed);
        } else {
            robot.shoot.setPower(0);
        }
    }

    public void continuousIntake() {
        if (!continuousIntake) {
            continuousIntake = true;
            robot.intake.setPower(-RobotConstants.intakeSpeed);
        } else {
            continuousIntake = false;
            robot.intake.setPower(0);
        }
    }

    public void continuousShoot() {
        if (!continuousShoot) {
            continuousShoot = true;
            robot.shoot.setPower(-RobotConstants.shootSpeed);
        } else {
            continuousShoot = false;
            robot.shoot.setPower(0);
        }
    }

    /**
     * Aligns ODS by moving left or right given which side line is located on
     * @param direction
     */
    public void alignWithLine(RobotMovement.Direction direction, int timeoutSec) {
        long stop = System.currentTimeMillis() + (timeoutSec * 1000);
        toggleLightLED();

        RobotMovement robotMovement = new RobotMovement(robot);
        RobotConstants.moveSpeed = 0.5;
        robotMovement.move(direction);

        while (robot.lightSensor.getLightDetected() < RobotConstants.whiteLineValue
                && System.currentTimeMillis() != stop) {
        }
        robotMovement.move(RobotMovement.Direction.NONE);

        if (robot.lightSensor.getLightDetected() < RobotConstants.perfectWhiteLineValue) {
            if (direction == RobotMovement.Direction.EAST) {
                robotMovement.move(RobotMovement.Direction.WEST);
            } else {
                robotMovement.move(RobotMovement.Direction.EAST);

            }
        }
        while (robot.lightSensor.getLightDetected() < RobotConstants.perfectWhiteLineValue
                && System.currentTimeMillis() != stop) { }
        robotMovement.move(RobotMovement.Direction.NONE);
        RobotConstants.moveSpeed = 1.0;
        toggleLightLED();
    }

    private void waitFor(LinearVisionOpMode opMode, int sec) {
        long millis = sec * 1000;
        long stopTime = currentTimeMillis() + millis;
        while(opMode.opModeIsActive() && currentTimeMillis() < stopTime) {
            try {
                opMode.waitOneFullHardwareCycle();
            } catch(Exception ex) {}
        }
    }
}
