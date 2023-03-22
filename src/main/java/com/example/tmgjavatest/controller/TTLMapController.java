package com.example.tmgjavatest.controller;

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

/**
 * A Spring controller that handles time-to-live map related requests.
 */
@Validated
@RestController
@RequestMapping(value = "map", produces = "application/json")
public class TTLMapController {
    private static final String KEY_REQUEST_PARAM_VALIDATION_MESSAGE =
            "'key' request parameter should be specified and it must not be empty";
    private final TTLMapService<String, String> mapService;

    /**
     * Instantiates a new TTLMapController.
     *
     * @param mapService the map service
     */
    @Autowired
    public TTLMapController(TTLMapService<String, String> mapService) {
        this.mapService = mapService;
    }

    /**
     * Puts a new entry in the map.
     * If timeToLiveInSeconds is not specified the entry will not expire.
     * If the key is already being used in the map, its value will be replaced.
     *
     * @param request with the key-value pair and a non-mandatory time-to-live
     */
    @PutMapping("/put")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void put(@RequestBody @Valid TTLMapRequest request) {
        mapService.put(request.getKey(), request.getValue(), request.getTimeToLiveInSeconds());
    }

    /**
     * Gets the value based on a specified key.
     *
     * @param key the key
     * @return the response with the key and value.
     */
    @GetMapping(value = "/get")
    @ResponseStatus(value = HttpStatus.OK)
    public TTLMapResponse get(@NotEmpty(message = KEY_REQUEST_PARAM_VALIDATION_MESSAGE) @RequestParam String key) {
        return new TTLMapResponse(key, mapService.get(key));
    }

    /**
     * Removes an entry for the map for the specified key.
     *
     * @param key the key
     */
    @DeleteMapping(value = "/remove")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void remove(@NotEmpty(message = KEY_REQUEST_PARAM_VALIDATION_MESSAGE) @RequestParam String key) {
        mapService.remove(key);
    }
}
