package org.firstinspires.ftc.teamcode.drive.opmode;


/**
 * This file contains an example of a Linear "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode is executed.
 *
 * This particular OpMode illustrates driving a 4-motor Omni-Directional (or Holonomic) robot.
 * This code will work with either a Mecanum-Drive or an X-Drive train.
 * Both of these drives are illustrated at https://gm0.org/en/latest/docs/robot-design/drivetrains/holonomic.html
 * Note that a Mecanum drive must display an X roller-pattern when viewed from above.
 *
 * Also note that it is critical to set the correct rotation direction for each motor.  See details below.
 *
 * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
 * Each motion axis is controlled by one Joystick axis.
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 * This code is written assuming that the right-side motors need to be reversed for the robot to drive forward.
 * When you first test your robot, if it moves backward when you push the left stick forward, then you must flip
 * the direction of all 4 motors (see code below).
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */
/*
@TeleOp(name="Main Drive 11/17", group="Linear Opmode")
@Disabled
public class oldDrive extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotor wheelMtr = null;
    private DcMotor linearSlide1 = null;
    private DcMotor linearSlide2 = null;
    private Servo servo = null;
    private Servo plane = null;
    private DcMotor lastWheel = null;


    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        linearSlide1 = hardwareMap.get(DcMotor.class, "lS1");
        // reset encoder counts kept by motors.
        linearSlide1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        linearSlide1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Initialize the hardware variables. Note that the strings used here must correspond
        linearSlide2 = hardwareMap.get(DcMotor.class, "lS2");
        // reset encoder counts kept by motors.
        linearSlide2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        linearSlide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        linearSlide1.setDirection(DcMotorSimple.Direction.REVERSE);
        linearSlide2.setDirection(DcMotorSimple.Direction.FORWARD);

        wheelMtr = hardwareMap.get(DcMotor.class, "wheel");
        wheelMtr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelMtr.setDirection(DcMotorSimple.Direction.FORWARD);


        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "lfd");
        leftBackDrive  = hardwareMap.get(DcMotor.class, "lbd");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rfd");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rbd");

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        servo = hardwareMap.get(Servo.class, "servo");
        plane = hardwareMap.get(Servo.class, "plane");
        lastWheel = hardwareMap.get(DcMotor.class, "perp");

        servo.setPosition(1);
        plane.setPosition(1);



        // ########################################################################################
        // !!!            IMPORTANT Drive Information. Test your motor directions.            !!!!!
        // ########################################################################################
        // Most robots need the motors on one side to be reversed to drive forward.
        // The motor reversals shown here are for a "direct drive" robot (the wheels turn the same direction as the motor shaft)
        // If your robot has additional gear reductions or uses a right-angled drive, it's important to ensure
        // that your motors are turning in the correct direction.  So, start out with the reversals here, BUT
        // when you first test your robot, push the left joystick forward and observe the direction the wheels turn.
        // Reverse the direction (flip FORWARD <-> REVERSE ) of any wheel that runs backward
        // Keep testing until ALL the wheels move the robot forward when you push the left joystick forward.
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial_target   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral_target     =  gamepad1.left_stick_x;
            double axial_real = axial_target*Math.cos(0) + lateral_target*Math.sin(0);

            double yaw =  gamepad1.right_stick_x / 2;

            double override = gamepad2.right_stick_y;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            //double leftFrontPower  = axial + lateral + yaw;
            //double rightFrontPower = axial - lateral - yaw;
            //double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
            }

            // Run wheels
            double wheelPwr = gamepad2.left_stick_y;

            // Linear slide
            if (gamepad2.y){
                // set motors to run forward for 5000 encoder counts.
                linearSlide1.setTargetPosition(3000);
                linearSlide2.setTargetPosition(3000);
                // set motors to run to target encoder position and stop with brakes on.
                linearSlide1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                linearSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                // set motors to run to target encoder position and stop with brakes on.
                linearSlide1.setPower(0.5);
                linearSlide2.setPower(0.5);
            }
            else if (gamepad2.b){
                linearSlide1.setTargetPosition(2000);
                linearSlide2.setTargetPosition(2000);
                linearSlide1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                linearSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                linearSlide1.setPower(0.5);
                linearSlide2.setPower(0.5);
            }
            else if (gamepad2.a){
                linearSlide1.setTargetPosition(0);
                linearSlide2.setTargetPosition(0);
                linearSlide1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                linearSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                linearSlide1.setPower(0.5);
                linearSlide2.setPower(0.5);
            }
            if (gamepad2.left_stick_button){
                linearSlide1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                linearSlide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }

            if (gamepad2.dpad_left){
                servo.setPosition(1);
            }
            else if (gamepad2.dpad_right){
                servo.setPosition(0);
            }
            if (gamepad2.x){
                plane.setPosition(0);
            }
            if (gamepad2.dpad_up){
                lastWheel.setPower(1);
            }
            else if (gamepad2.dpad_down){
                lastWheel.setPower(0);
            }

            // Sensitive Button
            if (gamepad1.right_bumper){
                leftFrontPower  /= 3;
                rightFrontPower /= 3;
                leftBackPower   /= 3;
                rightBackPower  /= 3;
            }

            if (override > 0.5 || override < -0.5){
                linearSlide1.setTargetPosition((int) linearSlide1.getCurrentPosition() + (int) (-override*50));
                linearSlide2.setTargetPosition((int) linearSlide2.getCurrentPosition() + (int) (-override*50));
                linearSlide1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                linearSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                linearSlide1.setPower(0.5);
                linearSlide2.setPower(0.5);
            }




            // Send calculated power to wheels
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);
            wheelMtr.setPower(wheelPwr);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.update();
        }
    }}
*/