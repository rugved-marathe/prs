package io.pms.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.pms.api.model.Common;
import io.pms.api.services.BusinessPhilosophyService;
import io.pms.api.vo.BusinessPhilosophyListVO;
import io.pms.api.vo.BusinessPhilosphyVO;

@RestController
public class BusinessPhilosophyController {

    @Autowired
    BusinessPhilosophyService businessPhilosophyService;

    @RequestMapping(value = "/business-philosophy", method = RequestMethod.POST)
    public ResponseEntity<Common> createBusinessPhilosophy(@RequestBody BusinessPhilosphyVO businessPhilosphyVO) {
        return new ResponseEntity<>(businessPhilosophyService.createBusinessPhilosophy(businessPhilosphyVO), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/business-philosophy", method = RequestMethod.PUT)
    public ResponseEntity<Common> editBusinessPhilosophy(@RequestBody BusinessPhilosphyVO businessPhilosphyVO) {
        return new ResponseEntity<>(businessPhilosophyService.editBusinessPhilosophy(businessPhilosphyVO), HttpStatus.OK);
    }

    @RequestMapping(value = "/business-philosophy", method = RequestMethod.GET)
    public ResponseEntity<BusinessPhilosophyListVO> getBusinessPhilosophies() {
        BusinessPhilosophyListVO businessPhilosophyListVO = businessPhilosophyService.getBusinessPhilosophies();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Total-Count", String.valueOf(businessPhilosophyListVO.getPhilosophies().size()));
        return new ResponseEntity<>(businessPhilosophyListVO, responseHeaders, HttpStatus.OK);
    }
}
