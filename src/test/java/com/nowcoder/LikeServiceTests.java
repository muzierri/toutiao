package com.nowcoder;

import com.nowcoder.service.LikeService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 21:16 2019/7/28
 * @Modified By
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class LikeServiceTests {
    @Autowired
    LikeService likeService;

    @Test
    public void testLike() {
        likeService.like(123, 1, 1);
        Assert.assertEquals(1, likeService.getLikeStatus(123, 1, 1));
    }

    @Test
    public void testDisLike() {
        likeService.disLike(123, 1, 1);
        Assert.assertEquals(-1, likeService.getLikeStatus(123, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        throw new IllegalArgumentException();
    }

    @Before
    public void setUp() {
        System.out.println("setup");
    }

    @After
    public void tearDown() {
        System.out.println("teardown");
    }

    @BeforeClass
    public static void BeforeClass() {
        System.out.println("beforeclass");
    }

    @AfterClass
    public static void AfterClass() {
        System.out.println("afterclass");
    }
}
