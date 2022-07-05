package com.iot.sync.web;

import com.iot.sync.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SyncController {
    private final  DeviceService deviceService;

    @GetMapping("/exportExcel")
    public void download(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {

    }

}
