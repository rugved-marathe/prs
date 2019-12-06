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
import io.pms.api.services.CommonCompetencyService;
import io.pms.api.vo.CommonCompetencyListVO;
import io.pms.api.vo.CommonCompetencyVO;

@RestController
public class CommonCompetencyController {

    @Autowired
    CommonCompetencyService commonCompetencyService;

    @RequestMapping(value = "/common-competency", method = RequestMethod.POST)
    public ResponseEntity<Common> createCommonCompetencies(@RequestBody CommonCompetencyVO commonCompetencyVO) {
        return new ResponseEntity<>(commonCompetencyService.createCommonCompetencies(commonCompetencyVO), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/common-competency", method = RequestMethod.GET)
    public ResponseEntity<CommonCompetencyListVO> getCommonCompetencies() {
        HttpHeaders responseHeaders = new HttpHeaders();
        CommonCompetencyListVO commonCompetencyListVO = commonCompetencyService.getCommonCompetencies();
        responseHeaders.add("X-Total-Count", String.valueOf(commonCompetencyListVO.getCompetencies().size()));
        return new ResponseEntity<>(commonCompetencyListVO, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/common-competency", method = RequestMethod.PUT)
    public ResponseEntity<Common> editCommonCompetencies(@RequestBody CommonCompetencyVO commonCompetencyVO) {
        return new ResponseEntity<>(commonCompetencyService.editCommonCompetencies(commonCompetencyVO), HttpStatus.OK);
    }
}
