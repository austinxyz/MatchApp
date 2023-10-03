package com.utr.match.entity;

import com.utr.model.Division;
import com.utr.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import com.utr.model.Event;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class EventDBLoader {
    public EventDBLoader() {
    }

    @Autowired
    EventRepository eventRepo;

    public void updateEvent(Event event) {

        EventEntity entity = eventRepo.findByEventId(event.getId());
        convertEvent(entity, event);

    }

    private void convertEvent(EventEntity entity, Event event) {

        for (DivisionEntity divisionEntity: entity.getDivisions()) {
            //System.out.println(divisionEntity.getName());
            Division div = event.getDivisionByName(divisionEntity.getName());
            if (div == null) {
                System.out.println(divisionEntity.getName() + " is not existed!");
                continue;
            }
            div.setDisplayName(divisionEntity.getChineseName());
            convertDivision(divisionEntity, div);
        }
    }

    private void convertDivision(DivisionEntity divisionEntity, Division div) {

        for (PlayerEntity playerEntity: divisionEntity.getPlayers()) {

            Player player = div.getPlayerByUTRId(playerEntity.getUtrId());
            if (player !=null) {
                player.setUTR(getUtr(playerEntity));
            } else {
                System.out.println(playerEntity.getName() + " can not find in div " + div.getName());
            }
        }

        div.getPlayers().sort((o1, o2) -> Float.compare(o2.getUTR(), o1.getUTR()));

    }

    private String getPlayerName(PlayerEntity playerEntity) {
        return playerEntity.getLastName() + " " + playerEntity.getFirstName();
    }

    private String getUtr(PlayerEntity playerEntity) {
        String utr="0.0";
        if (playerEntity.getUtrs().size()>0) {
            utr = String.format("%.02f", playerEntity.getUtrs().iterator().next().getUtr());
        }
        return utr;
    }

}
