package org.example.springwithdistributedlock.outbound.controller;

import lombok.RequiredArgsConstructor;
import org.example.springwithdistributedlock.outbound.service.OutboundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/outbound")
@RequiredArgsConstructor
public class OutboundController {

    private final OutboundService outboundService;

    @PutMapping("/decrease/{outboundId}")
    public ResponseEntity<String> outboundChange(@PathVariable(name = "outboundId") Long outboundId) {
        outboundService.notDistributedOutboundChange(outboundId);
        return ResponseEntity
                .ok()
                .build();
    }
}
