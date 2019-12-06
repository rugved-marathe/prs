package io.pms.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.pms.api.model.Responsibility;
import io.pms.api.services.ResponsibilityService;
import io.pms.api.vo.ResponsibilityListVO;
import io.pms.api.vo.ResponsibilityVO;

@RestController
public class ResponsibilityController {

    @Autowired
    ResponsibilityService responsibilityService;

    @RequestMapping(value = "/responsibility", method = RequestMethod.POST)
    public ResponseEntity<Responsibility> createResponsibility(@RequestBody ResponsibilityVO responsibilityVO) {
        return new ResponseEntity<>(responsibilityService.createResponsibility(responsibilityVO), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/responsibility", method = RequestMethod.GET)
    public ResponseEntity<ResponsibilityListVO> getResponsibilities() {
        ResponsibilityListVO responsibilityListVO = responsibilityService.getResponsibilities();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Total-Count", String.valueOf(responsibilityListVO.getResponsibilities().size()));
        return new ResponseEntity<>(responsibilityListVO, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/responsibility/role/{roleId}", method = RequestMethod.GET)
    public ResponseEntity<ResponsibilityListVO> getRoleResponsibilities(@PathVariable("roleId") String roleId) {
        ResponsibilityListVO responsibilityListVO = responsibilityService.getRoleResponsibilities(roleId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Total-Count", String.valueOf(responsibilityListVO.getResponsibilities().size()));
        return new ResponseEntity<>(responsibilityListVO, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/responsibility/{responsibilityId}", method = RequestMethod.PUT)
    public ResponseEntity<Responsibility> editResponsibility(@PathVariable("responsibilityId") String responsinilityId, @RequestBody ResponsibilityVO responsibilityVO) {
        return new ResponseEntity<>(responsibilityService.editResponsibility(responsinilityId, responsibilityVO), HttpStatus.OK);
    }

    @RequestMapping(value = "/responsibility/{responsibilityId}", method = RequestMethod.DELETE)
    public ResponseEntity<Responsibility> deleteResponsibility(@PathVariable("responsibilityId") String responsibilityId) {
        return new ResponseEntity<>(responsibilityService.deleteResponsibility(responsibilityId), HttpStatus.OK);
    }
}
