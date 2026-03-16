package org.example.basiclogin.controller;

import io.github.tongbora.bakong.dto.BakongRequest;
import io.github.tongbora.bakong.dto.BakongResponse;
import io.github.tongbora.bakong.dto.CheckTransactionRequest;
import io.github.tongbora.bakong.service.BakongService;
import kh.gov.nbc.bakong_khqr.model.KHQRData;
import kh.gov.nbc.bakong_khqr.model.KHQRResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final BakongService bakongService;

    // Step 1 — Generate QR code
    @PostMapping("/generate-qr")
    public KHQRResponse<KHQRData> generateQR(@RequestBody BakongRequest request) {
        return bakongService.generateQR(request);
    }

    // Step 2 — Get QR as PNG image bytes
    @PostMapping("/qr-image")
    public ResponseEntity<byte[]> getQRImage(@RequestBody KHQRData qrData) {
        byte[] image = bakongService.getQRImage(qrData);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    // Step 3 — Check if transaction is paid
    @PostMapping("/check-transaction")
    public BakongResponse checkTransaction(@RequestBody CheckTransactionRequest request) {
        return bakongService.checkTransactionByMD5(request);
    }
}
