package com.openex.backend.controller;

import com.openex.backend.dto.WalletRequest;
import com.openex.backend.dto.WalletResponse;
import com.openex.backend.service.WalletService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public List<WalletResponse> getAllWallets() {
        return walletService.getAllWallets();
    }

    @GetMapping("/{id}")
    public WalletResponse getWalletById(@PathVariable Long id) {
        return walletService.getWalletById(id);
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody WalletRequest request) {
        WalletResponse savedWallet = walletService.createWallet(request);
        return ResponseEntity
                .created(URI.create("/api/wallets/" + savedWallet.id()))
                .body(savedWallet);
    }

    @PutMapping("/{id}")
    public WalletResponse updateWallet(@PathVariable Long id, @RequestBody WalletRequest request) {
        return walletService.updateWallet(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        walletService.deleteWallet(id);
        return ResponseEntity.noContent().build();
    }
}
