package frc.robot;

import java.lang.Math;

// This is very jank code but it should give an idea of what needs to be done. If it runs, great!

public class Robot extends IterativeRobot
{

    public void autonomousInit() // Needs to be some sort of initializing function
    {
        // Initialize the table
        NetworkTable table;

        // Setup the length of middles
        double[] middles = new doubles[20]; // There will be errors if we exceed 20, but there's no way there will be 40 midpoints.
    }

    public void autonomousPeriodic() // Needs to be some sort of looping function
    {
        // Table should constantly update to get new values
        table = NetworkTable.getTable("vision/boundingRect");

        // In case the table isn't returning any values, this will return a default value
        double[] defaultValue = new double[0];

        // Set corresponding values from networkTables into program variables
        double[] x = table.getNumberArray("center_x_positions", defaultValue);
        double[] y = table.getNumberArray("center_y_positions", defaultValue);
        double[] h = table.getNumberArray("height", defaultValue);
        double[] w = table.getNumberArray("width", defaultValue);

        /* Processing needs to be done in order to ensure we get the middle
        of the middles of all the boundingRects. This will allow the robot
        to center and orient itself towards the target. */

        // If the length of x isn't 0, process the midpoints...
        if(x.length != 0)
        {
            // Alter 'sub' for the future for loop so it runs properly
            int sub = ((x.length % 2) == 0) ? 0 : -1;

            int inc = 0; // Just to make sure the counting works properly in the for loop

            // Run a for loop that will produce the midpoints of 2 points (Trust me it makes sense)
            for(int i=0; (i<=(x.length + sub)); i++)
            {
                // We need to find the middle of the mid-points of each set of 2

                // First, make sure we are on an odd increment
                if((i % 2) == 1)
                {
                    // If we are on an odd increment, we find the midpoint of a specific set of 2 points
                    middles[inc++] = (x[i-1] + x[i]) / 2;
                }

            }

            /* From here, middles[] has the midpoints of every pair of midpoints that can be found. [0], [1]
            has been averaged, [2], [3] has been averaged, as so on, depending on the value of x.length. Even
            and odd statements are to make sure that only pairs will be able to be averaged, and that extra
            values will be excluded.

            Now that we have the midpoints, the next challenge is to make the robot choose the close of these
            points, and afterwards has to move itself closer so the midpoint is centered in the camera's vision. */

            double left = -1;
            double right = 1;
            double forward = 1;
            double none = 0;

            // Super lazy if-statement that will fail if there are somehow more than 6 mid-midpoints
            if(inc >= 1)
            {
                // This should do pretty much the same thing as your if statements
                // You might want to double check this code...
                int closest = Math.abs(320 - middles[0]);
                int num = 0;
                for(int i = 0; i < middles.length; i++)
                {
                    int closer = Math.abs(320 - middles[i]);
                    closest = Math.min(closer, closest);
                    num = i;
                }

/*              if(inc == 1)
 *              {
 *                  int num = 0;
 *              }
 *
 *              if(inc == 2)
 *              {
 *                  int a = Math.abs(320 - middles[0]);
 *                  int b = Math.abs(320 - middles[1]);
 *
 *                  int num = (a < b) ? 0 : 1;
 *
 *              }
 *
 *              if(inc == 3)
 *              {
 *                  int a = Math.abs(320 - middles[0]);
 *                  int b = Math.abs(320 - middles[1]);
 *                  int c = Math.abs(320 - middles[2]);
 *                  int min = Math.min(a, b, c);
 *
 *                  if(a < b && a < c)
 *                  {
 *                      int num = 0;
 *                  }
 *
 *                  if(b < a && b < c)
 *                  {
 *                      int num = 1;
 *                  }
 *
 *                  if(c < a && c < b)
 *                  {
 *                      int num = 2;
 *                  }
 *
 *              }
 *
 *              if(inc == 4)
 *              {
 *                  int a = Math.abs(320 - middles[0]);
 *                  int b = Math.abs(320 - middles[1]);
 *                  int c = Math.abs(320 - middles[2]);
 *                  int d = Math.abs(320 - middles[3]);
 *
 *                  if(a < b && a < c && a < d)
 *                  {
 *                      int num = 0;
 *                  }
 *
 *                  if(b < a && b < c && b < d)
 *                  {
 *                      int num = 1;
 *                  }
 *
 *                  if(c < a && c < b && c < d)
 *                  {
 *                      int num = 2;
 *                  }
 *
 *                  if(d < a && d < b && d < c)
 *                  {
 *                      int num = 3;
 *                  }
 *
 *              }
 *
 *              if(inc == 5)
 *              {
 *                  int a = Math.abs(320 - middles[0]);
 *                  int b = Math.abs(320 - middles[1]);
 *                  int c = Math.abs(320 - middles[2]);
 *                  int d = Math.abs(320 - middles[3]);
 *                  int e = Math.abs(320 - middles[4]);
 *
 *                  if(a < b && a < c && a < d && a < e)
 *                  {
 *                      int num = 0;
 *                  }
 *
 *                  if(b < a && b < c && b < d && b < e)
 *                  {
 *                      int num = 1;
 *                  }
 *
 *                  if(c < a && c < b && c < d && c < e)
 *                  {
 *                      int num = 2;
 *                  }
 *
 *                  if(d < a && d < b && d < c && d < e)
 *                  {
 *                      int num = 3;
 *                  }
 *
 *                  if(e < a && e < b && e < c && e < d)
 *                  {
 *                      int num = 4;
 *                  }
 *
 *              }
 *
 *              if(inc == 6)
 *              {
 *                  int a = Math.abs(320 - middles[0]);
 *                  int b = Math.abs(320 - middles[1]);
 *                  int c = Math.abs(320 - middles[2]);
 *                  int d = Math.abs(320 - middles[3]);
 *                  int e = Math.abs(320 - middles[4]);
 *                  int f = Math.abs(320 - middles[5]);
 *
 *                  if(a < b && a < c && a < d && a < e && a < f)
 *                  {
 *                      int num = 0;
 *                  }
 *
 *                  if(b < a && b < c && b < d && b < e && b < f)
 *                  {
 *                      int num = 1;
 *                  }
 *
 *                  if(c < a && c < b && c < d && c < e && c < f)
 *                  {
 *                      int num = 2;
 *                  }
 *
 *                  if(d < a && d < b && d < c && d < e && d < f)
 *                  {
 *                      int num = 3;
 *                  }
 *
 *                  if(e < a && e < b && e < c && e < d && e < f)
 *                  {
 *                      int num = 4;
 *                  }
 *
 *                  if(f < a && f < b && f < c && f < d && f < e)
 *                  {
 *                      int num = 5;
 *                  }
 *
 *              }
 */
                // Make the robot move!
                // You could probably use the `closest` variable that I initiated in my for loop
                // Instead of using `middles[num]` because that would just be the closest to 320, right?
                if(middles[num] > 322)
                {
                    mecanumDrive_Cartesian(left, none, none);
                }

                if(middles[num] < 318)
                {
                    mecanumDrive_Cartesian(right, none, none);
                }

                if(middles[0] < 322 && middles[1] > 318)
                {
                    mecanumDrive_Cartesian(none, forward, none);
                }

            }

        }

    }

}

/*
package frc.robot;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot
{

    NetworkTable table;

    public Robot()
    {
        table = NetworkTable.getTable("vision/boundingRect");
    }

    public void robotInit()
    {
        double[] defaultValue = new double[0];

        while (true)
        {
            double[] centers = table.getNumberArray("centers", defaultValue);
            System.out.print("centers: ");

            for (double center : centers)
            {
                System.out.print(center + " ");
            }

            Ststem.out.println();
            Timer.delay(1);
        }

    }

}
*/
