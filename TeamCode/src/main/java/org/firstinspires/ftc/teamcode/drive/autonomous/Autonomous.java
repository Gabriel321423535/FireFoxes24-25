package org.firstinspires.ftc.teamcode.drive.autonomous;
import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Config
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "TEST_AUTONOMOUS", group = "Autonomous")
public class Autonomous extends LinearOpMode {
    MecanumDrive.Params parameters = new MecanumDrive.Params();

    public class Lift {
        private final DcMotorEx linearSlide1;
        private final DcMotorEx linearSlide2;

        public Lift(HardwareMap hardwareMap) {
            linearSlide1 = hardwareMap.get(DcMotorEx.class, "linearSlide1");
            linearSlide2 = hardwareMap.get(DcMotorEx.class, "linearSlide2");
            linearSlide1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            linearSlide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            linearSlide1.setDirection(DcMotorSimple.Direction.FORWARD);
            linearSlide2.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        public class LiftUp implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    linearSlide1.setPower(0.8);
                    linearSlide2.setPower(0.8);
                    initialized = true;
                }

                double pos = linearSlide1.getCurrentPosition();  // Assumes both slides at same pos
                packet.put("Linear Slide Positions", pos);
                if (pos < parameters.LINEAR_SLIDE_MAX) {    // Keep raising lift if it hasn't reached max height yet
                    return true;
                } else {
                    // If lift is at desired position, stop raising
                    linearSlide1.setPower(0);
                    linearSlide2.setPower(0);
                    return false;
                }
            }
        }

        public Action liftUp() {
            return new LiftUp();
        }

        public class LiftDown implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    linearSlide1.setPower(-0.8);
                    linearSlide2.setPower(-0.8);
                    initialized = true;
                }

                double pos = linearSlide1.getCurrentPosition();
                packet.put("liftPos", pos);
                if (pos > parameters.LINEAR_SLIDE_MIN) {    // Keep lowering lift if it hasn't reached max height yet
                    return true;
                } else {
                    // If lift is at desired position, stop raising
                    linearSlide1.setPower(0);
                    return false;
                }
            }
        }

        public Action liftDown() {
            return new LiftDown();
        }
    }

    public class Claw {
        private Servo claw;

        public Claw(HardwareMap hardwareMap) {
            claw = hardwareMap.get(Servo.class, "claw");
        }

        public class ClawClose implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                claw.setPosition(parameters.CLAW_CLOSE);
                return false;
            }
        }

        public Action closeClaw() {
            return new ClawClose();
        }

        public class ClawOpen implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                claw.setPosition(parameters.CLAW_OPEN);
                return false;
            }
        }

        public Action openClaw() {
            return new ClawOpen();
        }
    }

    public class Twist {
        Servo twist;

        public Twist(HardwareMap hardwareMap) {
            Servo twist = hardwareMap.get(Servo.class, "twist");
        }

        public class TwistUp implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                twist.setPosition(parameters.TWIST_HIGH);
                return false;
            }
        }

        public Action twistUp() {
            return new TwistUp();
        }

        public class TwistDown implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                twist.setPosition(parameters.TWIST_LOW);
                return false;
            }
        }

        public Action twistDown() {
            return new TwistDown();
        }
    }

    @Override
    public void runOpMode() {
        Pose2d initialPose = new Pose2d(0, 0, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        Claw claw = new Claw(hardwareMap);
        Lift lift = new Lift(hardwareMap);
        Twist twist = new Twist(hardwareMap);
        Pose2d bucketPose = new Pose2d(55, 54, Math.toRadians(45));
        double secondsToWait = 1;
        double initialBlockX = 35.0;
        Pose2d block1Pose = new Pose2d(initialBlockX+10*0, 26, Math.toRadians(0));
        Pose2d block2Pose = new Pose2d(initialBlockX+10*1, 26, Math.toRadians(0));
        Pose2d block3Pose = new Pose2d(initialBlockX+10*2, 26, Math.toRadians(0));

        // Replace contents with whatever path you decide on in MeepMeep
        TrajectoryActionBuilder initialGoToBucket = drive.actionBuilder(initialPose)
                .setTangent(Math.toRadians(0))
                .splineToLinearHeading(bucketPose, Math.toRadians(315));
        TrajectoryActionBuilder goToBlock1 = drive.actionBuilder(bucketPose)
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(block1Pose, 0);
        TrajectoryActionBuilder goBackFromBlock1 = drive.actionBuilder(block1Pose)
                .splineToLinearHeading(bucketPose, 45);
        TrajectoryActionBuilder goToBlock2 = drive.actionBuilder(bucketPose)
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(block2Pose, 0);
        TrajectoryActionBuilder goBackFromBlock2 = drive.actionBuilder(block2Pose)
                .splineToLinearHeading(bucketPose, 45);
        TrajectoryActionBuilder goToBlock3 = drive.actionBuilder(bucketPose)
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(block3Pose, 0);
        TrajectoryActionBuilder goBackFromBlock3 = drive.actionBuilder(block3Pose)
                .splineToLinearHeading(bucketPose, 45);
        TrajectoryActionBuilder goToSubmersible = drive.actionBuilder(bucketPose)
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(new Pose2d(26, 10, Math.toRadians(180)), Math.toRadians(270));
        // actions that need to happen on init; for instance, a claw tightening.
        Actions.runBlocking(lift.liftUp());
        Actions.runBlocking(twist.twistUp());
        Actions.runBlocking(claw.closeClaw());

        while (!isStopRequested() && !opModeIsActive()) {
            telemetry.addData("X Position during Init", drive.pose.position.x);
            telemetry.addData("Y Position during Init", drive.pose.position.y);
            telemetry.addData("Heading during Init", drive.pose.heading.real);

            telemetry.update();
        }

        // If we're using all of these, might as well build them all, right?
        Action initialGoToBucketBuilt = initialGoToBucket.build();
        Action goToBlock1Built = goToBlock1.build();
        Action goBackFromBlock1Built = goBackFromBlock1.build();
        Action goToBlock2Built = goToBlock2.build();
        Action goBackFromBlock2Built = goBackFromBlock2.build();
        Action goToBlock3Built = goToBlock3.build();
        Action goBackFromBlock3Built = goBackFromBlock3.build();

        telemetry.update();
        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(
                new SequentialAction(
                    initialGoToBucketBuilt,
                    claw.openClaw(),
                    twist.twistDown(),
                    lift.liftDown(),
                    goToBlock1Built,
                    claw.closeClaw(),
                    lift.liftUp(),
                    twist.twistUp(),
                    goBackFromBlock1Built,
                    claw.openClaw(),
                    twist.twistDown(),
                    lift.liftDown(),
                    goToBlock2Built,
                    claw.closeClaw(),
                    lift.liftUp(),
                    twist.twistUp(),
                    goBackFromBlock2Built,
                    claw.openClaw(),
                    twist.twistDown(),
                    lift.liftDown(),
                    goToBlock3Built,
                    claw.closeClaw(),
                    lift.liftUp(),
                    twist.twistUp(),
                    goBackFromBlock3Built
                )
        );
    }
}
