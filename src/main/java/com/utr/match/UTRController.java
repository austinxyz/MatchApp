package com.utr.match;


import com.utr.match.entity.*;
import com.utr.match.usta.*;
import com.utr.match.usta.po.*;
import com.utr.match.utr.UTRDivisionExcelExport;
import com.utr.match.utr.UTRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/utr")
public class UTRController {

    @Autowired
    private UTRService utrService;

    @CrossOrigin(origins = "*")
    @GetMapping("/events")
    public ResponseEntity<List<EventEntity>> events(
    ) {
        List<EventEntity> events = utrService.getEvents();

        if (!events.isEmpty()) {
            return ResponseEntity.ok(events);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/divisions/{id}/utrs")
    public ResponseEntity<DivisionEntity> updateCandidatesUTR(@PathVariable("id") String id,
                                                       @RequestParam("action") String action
    ) {

        if (action.equals("refreshValue")) {

            DivisionEntity division = utrService.getDivision(Long.valueOf(id));

            if (division != null) {
                utrService.updateCandidatesUTRValue(division, false, false);
                return new ResponseEntity<>(division, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/exportExcel/divisions/{divisionId}")
    public ModelAndView exportDivisionToExcel(@PathVariable("divisionId") String divisionId) {
        ModelAndView mav = new ModelAndView();
        mav.setView(new UTRDivisionExcelExport());

        DivisionEntity div = utrService.getDivision(Long.valueOf(divisionId));

        //send to excelImpl class
        mav.addObject("div", div);
        return mav;
    }
}