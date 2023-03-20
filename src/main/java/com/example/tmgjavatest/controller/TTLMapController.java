package com.example.tmgjavatest.controller;

import com.example.tmgjavatest.exception.NoKeyValuePairException;
import com.example.tmgjavatest.model.dto.request.TTLMapRequest;
import com.example.tmgjavatest.model.dto.response.TTLMapResponse;
import com.example.tmgjavatest.service.TTLMapService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "map", produces = "application/json")
public class TTLMapController {
    private static final String KEY_REQUEST_PARAM_VALIDATION_MESSAGE =
            "'key' request parameter should be specified and it must not be empty";
    private final TTLMapService<String, String> mapService;

    @Autowired
    public TTLMapController(TTLMapService<String, String> mapService) {
        this.mapService = mapService;
    }

    @PutMapping("/put")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void put(@RequestBody @Valid TTLMapRequest pair) {
        mapService.put(pair.getKey(), pair.getValue(), pair.getTimeToLiveInSeconds());
    }

    @GetMapping(value = "/get")
    @ResponseStatus(value = HttpStatus.OK)
    public TTLMapResponse get(@NotEmpty(message = KEY_REQUEST_PARAM_VALIDATION_MESSAGE) @RequestParam String key) {
        String value = mapService.get(key);
        if(value == null) throw new NoKeyValuePairException();

        return new TTLMapResponse(key, value);
    }

    @DeleteMapping(value = "/remove")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void remove(@NotEmpty(message = KEY_REQUEST_PARAM_VALIDATION_MESSAGE) @RequestParam String key) {
        mapService.remove(key);
    }
}
