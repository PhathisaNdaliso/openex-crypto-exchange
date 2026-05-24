package com.openex.backend.service;

import com.openex.backend.dto.WalletRequest;
import com.openex.backend.dto.WalletResponse;
import com.openex.backend.model.User;
import com.openex.backend.model.Wallet;
import com.openex.backend.repository.WalletRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public WalletService(
            WalletRepository walletRepository,
            UserService userService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.walletRepository = walletRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    @Cacheable("walletsAll")
    public List<WalletResponse> getAllWallets() {
        return walletRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "walletsById", key = "#id")
    public WalletResponse getWalletById(Long id) {
        return toResponse(findWalletEntityById(id));
    }

    @CacheEvict(cacheNames = {"walletsAll", "walletsById"}, allEntries = true)
    public WalletResponse createWallet(WalletRequest request) {
        validateWalletRequest(request);
        User user = userService.findUserEntityById(request.userId());
        String currency = request.currency().trim().toUpperCase();

        walletRepository.findByUserIdAndCurrency(user.getId(), currency)
                .ifPresent(existingWallet -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Wallet already exists for this user and currency"
                    );
                });

        Wallet wallet = Wallet.builder()
                .user(user)
                .currency(currency)
                .balance(defaultIfNull(request.balance()))
                .lockedBalance(defaultIfNull(request.lockedBalance()))
                .build();

        WalletResponse response = toResponse(walletRepository.save(wallet));
        publishWalletUpdate("CREATED", response);
        return response;
    }

    @CacheEvict(cacheNames = {"walletsAll", "walletsById"}, allEntries = true)
    public WalletResponse updateWallet(Long id, WalletRequest request) {
        validateWalletRequest(request);

        Wallet wallet = findWalletEntityById(id);
        User user = userService.findUserEntityById(request.userId());
        String currency = request.currency().trim().toUpperCase();

        walletRepository.findByUserIdAndCurrency(user.getId(), currency)
                .filter(existingWallet -> !existingWallet.getId().equals(id))
                .ifPresent(existingWallet -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Wallet already exists for this user and currency"
                    );
                });

        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setBalance(defaultIfNull(request.balance()));
        wallet.setLockedBalance(defaultIfNull(request.lockedBalance()));

        WalletResponse response = toResponse(walletRepository.save(wallet));
        publishWalletUpdate("UPDATED", response);
        return response;
    }

    @CacheEvict(cacheNames = {"walletsAll", "walletsById"}, allEntries = true)
    public void deleteWallet(Long id) {
        Wallet wallet = findWalletEntityById(id);
        WalletResponse response = toResponse(wallet);
        walletRepository.delete(wallet);
        publishWalletUpdate("DELETED", response);
    }

    @Transactional(readOnly = true)
    public Wallet findWalletEntityById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));
    }

    private void validateWalletRequest(WalletRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wallet payload is required");
        }
        if (request.userId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        }
        if (isBlank(request.currency())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Currency is required");
        }

        BigDecimal balance = defaultIfNull(request.balance());
        BigDecimal lockedBalance = defaultIfNull(request.lockedBalance());
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Balance cannot be negative");
        }
        if (lockedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Locked balance cannot be negative");
        }
        if (lockedBalance.compareTo(balance) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Locked balance cannot exceed total balance"
            );
        }
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUser().getId(),
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getLockedBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void publishWalletUpdate(String eventType, WalletResponse response) {
        messagingTemplate.convertAndSend("/topic/wallets", response);
        messagingTemplate.convertAndSend("/topic/wallets/" + response.userId(), response);
        messagingTemplate.convertAndSend(
                "/queue/wallet-events",
                eventType + ":" + response.id() + ":" + response.currency()
        );
    }
}
