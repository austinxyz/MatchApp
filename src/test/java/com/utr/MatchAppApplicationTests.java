package com.utr;

import com.utr.match.PlayerController;
import com.utr.match.ZiJingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MatchAppApplicationTests {

	@Autowired
	private ZiJingController controller;

	@Autowired
	private PlayerController playerController;

	@Test
	void contextLoads() {
		//System.out.println(controller.analysis("ZJU-BYD", "0").toString());
		System.out.println(playerController.searchByName("927540").toString());
	}

}
