package com.utr.match;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MatchAppApplicationTests {

	@Autowired
	private TeamController controller;

	@Test
	void contextLoads() {
		//System.out.println(controller.analysis("ZJU_BYD", "0").toString());
		System.out.println(controller.analysisFixed("ZJU_BYD", "", "Dai  Ian,Li Haoyang_Dai  Ian,Teoh  Ian", "", "", "").toString());
	}

}
