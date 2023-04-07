package com.utr;

import com.utr.model.Player;
import com.utr.parser.UTRParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
public class AppInitBean implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppInitBean.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        UTRParser parser = new UTRParser();
        Player player = parser.getPlayer("2547696", false);
        logger.debug("UTR fetch success");
    }

}
