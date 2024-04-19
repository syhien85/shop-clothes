package root.service;

import root.dto.PaymentDTO;
import root.entity.Payment;
import root.exception.DuplicateResourceException;
import root.exception.ResourceNotFoundException;
import root.repository.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;

    @Transactional
    public void create(PaymentDTO paymentDTO) {
        if (paymentRepo.existsByPaymentMethod(paymentDTO.getPaymentMethod())) {
            throw new DuplicateResourceException(
                "payment with payment method [%s] already taken".formatted(paymentDTO.getPaymentMethod())
            );
        }
        Payment payment = new ModelMapper().map(paymentDTO, Payment.class);
        paymentRepo.save(payment);
    }

    @Transactional
    public void update(PaymentDTO paymentDTO) {
        Payment currentPayment = paymentRepo.findById(paymentDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "payment with id [" + paymentDTO.getId() + "] not found"
            )
        );
        if(paymentDTO.getPaymentMethod() != null) {
            if (paymentRepo.existsByPaymentMethod(paymentDTO.getPaymentMethod())) {
                throw new DuplicateResourceException(
                    "payment with payment method [%s] already taken".formatted(paymentDTO.getPaymentMethod())
                );
            }
            currentPayment.setPaymentMethod(paymentDTO.getPaymentMethod());
        }
        currentPayment.setAccountNumber(paymentDTO.getAccountNumber());
        currentPayment.setAccountName(paymentDTO.getAccountName());
        currentPayment.setBankName(paymentDTO.getBankName());

        paymentRepo.save(currentPayment);
    }

    @Transactional
    public void delete(Integer id) {
        if (!paymentRepo.existsById(id)) {
            throw new ResourceNotFoundException("payment with id [" + id + "] not found");
        }
        paymentRepo.deleteById(id);
    }

    public PaymentDTO getById(Integer id) {
        return paymentRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("payment with id [" + id + "] not found")
        );
    }

    public List<PaymentDTO> listAll() {
        return paymentRepo.findAll().stream().map(this::convert).toList();
    }

    private PaymentDTO convert(Payment payment) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(payment, PaymentDTO.class);
    }
}
