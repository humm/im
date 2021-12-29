package com.hoomoomoo.im;

import java.awt.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2021/12/29
 */
public class ShakeMouse {

    public static void main(String[] args) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        Point pos = MouseInfo.getPointerInfo().getLocation();

        int last_x = pos.x;
        int last_y = pos.y;

        int mov = 1;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        System.out.println("Screen size: " + screenSize.getWidth() + "*" + screenSize.getHeight());

        while (true) {
            System.out.println(pos.x + " " + pos.y);
            PointerInfo pos_info = MouseInfo.getPointerInfo();
            if (pos_info == null) {
                System.out.println("Get location fail!");
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                pos = pos_info.getLocation();

                if ((pos.x == last_x) && (pos.y == last_y)) {
                    System.out.println("moving!");

                    if (pos.y <= 0) {
                        mov = 1;
                    }
                    if (pos.y > 0) {
                        mov = -1;
                    }
                    robot.mouseMove(pos.x, pos.y + mov);

                    robot.mouseMove(pos.x, pos.y);
                }
                pos_info = MouseInfo.getPointerInfo();
                if (pos_info == null) {
                    System.out.println("Get location fail!");
                    try {
                        Thread.sleep(30000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    pos = pos_info.getLocation();

                    last_x = pos.x;
                    last_y = pos.y;
                    try {
                        Thread.sleep(30000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
