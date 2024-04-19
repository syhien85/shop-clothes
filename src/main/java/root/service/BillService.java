package root.service;

import root.dto.*;
import root.entity.*;
import root.exception.RequestValidationException;
import root.exception.ResourceNotFoundException;
import root.pdf.BillIdPdfService;
import root.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class BillService {

    private final BillRepo billRepo;
    private final ProductColorRepo productColorRepo;
    private final UserRepo userRepo;
    private final CouponRepo couponRepo;
    private final PaymentRepo paymentRepo;

    private final EmailService emailService;
    private final BillIdPdfService billIdPdfService;

    @Transactional
    public void create(BillDTO billDTO) {
        billDTO.setStatus("NEW");

        if (!userRepo.existsById(billDTO.getUser().getId())) {
            throw new ResourceNotFoundException(
                "user with id [" + billDTO.getUser().getId() + "] not found"
            );
        }
        for (BillItemDTO billItemDTO : billDTO.getBillItems()) {
            ProductColor productColor = productColorRepo.findById(billItemDTO.getProductColor().getId())
                .orElseThrow(
                    () -> new ResourceNotFoundException(
                        "product with id [" + billItemDTO.getProductColor().getId() + "] not found"
                    )
                );
            if (productColor.getQuantity() - billItemDTO.getQuantity() < 0) {
                throw new RequestValidationException("quantity of product color is not enough");
            } else {
                productColor.setQuantity(productColor.getQuantity() - billItemDTO.getQuantity());
                productColorRepo.save(productColor);
            }

            billItemDTO.setPrice(productColor.getProduct().getPrice());
        }

        if (couponRepo.existsByCouponCode(billDTO.getCouponCode())) {
            Coupon coupon = couponRepo.findByCouponCode(billDTO.getCouponCode()).get();
            Date today = Date.from(Instant.now());
            if (coupon.getExpiredDate().before(today)) {
                billDTO.setDiscountAmount(0.0);
            } else {
                billDTO.setDiscountAmount(coupon.getDiscountAmount());
            }
        } else {
            billDTO.setCouponCode(null);
            billDTO.setDiscountAmount(0.0);
        }

        if (!paymentRepo.existsById(billDTO.getPayment().getId())) {
            throw new ResourceNotFoundException(
                "payment with id [" + billDTO.getPayment().getId() + "] not found"
            );
        }

        Bill bill = new ModelMapper().map(billDTO, Bill.class);
        // set bill id for billItem after save bill
        for (BillItem billItem : bill.getBillItems()) {
            billItem.setBill(bill);
        }
        billRepo.save(bill);
        billDTO.setId(bill.getId());

        new Thread(() -> {
            // generate billIdPdf before send mail
            billIdPdfService.billIdPdf(bill.getId());

            // send email bill info for customer (pdf file)
            emailService.sendBillCreatePdfEmail(
                userRepo.findById(billDTO.getUser().getId()).get().getEmail(),
//                "syhien85@hotmail.com",
                getById(bill.getId())
            );
        }).start();
    }

    @Transactional
    public void update(BillDTO billDTO) {
        Bill currentBill = billRepo.findById(billDTO.getId())
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "bill with id [" + billDTO.getId() + "] not found"
                )
            );

        currentBill.setStatus(billDTO.getStatus());

        if (couponRepo.existsByCouponCode(billDTO.getCouponCode())) {
            Coupon coupon = couponRepo.findByCouponCode(billDTO.getCouponCode()).get();
            Date today = Date.from(Instant.now());
            if (coupon.getExpiredDate().before(today)) {
                currentBill.setDiscountAmount(0.0);
            } else {
                currentBill.setDiscountAmount(coupon.getDiscountAmount());
            }
        } else {
            currentBill.setCouponCode(null);
            currentBill.setDiscountAmount(0.0);
        }

        Payment currentPayment = paymentRepo.findById(billDTO.getPayment().getId())
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "product with id [" + billDTO.getPayment().getId() + "] not found"
                )
            );
        currentBill.setPayment(currentPayment);

        billRepo.save(currentBill);
    }

    @Transactional
    public void delete(Long id) {
        if (!billRepo.existsById(id)) {
            throw new ResourceNotFoundException("bill with id [" + id + "] not found");
        }
        billRepo.deleteById(id);
    }

    public BillDTO getById(Long id) {
        return billRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("bill with id [" + id + "] not found")
        );
    }

    public PageDTO<BillDTO> searchService(SearchBillDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy
        );

        // If ko nhap gi ca
        Page<Bill> page = billRepo.findAll(pageRequest);

        String key = "%" + searchDTO.getKeyword() + "%";

        Long c1 = searchDTO.getUserId();
        Date c2 = searchDTO.getStart();
        Date c3 = searchDTO.getEnd();

        if (c1 == null && c2 == null && c3 == null) {
            page = billRepo.searchByDefault(key, pageRequest);
        }

        if (c1 != null && c2 == null && c3 == null) {
            page = billRepo.searchByUserId(key, c1, pageRequest);
        }
        if (c1 == null && c2 != null && c3 == null) {
            page = billRepo.searchByStart(key, c2, pageRequest);
        }
        if (c1 == null && c2 == null && c3 != null) {
            page = billRepo.searchByEnd(key, c3, pageRequest);
        }
        if (c1 != null && c2 != null && c3 != null) {
            page = billRepo.searchByUserIdAndStart(key, c1, c2, pageRequest);
        }
        if (c1 != null && c2 == null && c3 != null) {
            page = billRepo.searchByUserIdAndEnd(key, c1, c3, pageRequest);
        }
        if (c1 == null && c2 != null && c3 != null) {
            page = billRepo.searchByStartAndEnd(key, c2, c3, pageRequest);
        }
        if (c1 != null && c2 != null && c3 != null) {
            page = billRepo.searchByUserIdAndStartAndEnd(key, c1, c2, c3, pageRequest);
        }

        return PageDTO.<BillDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private BillDTO convert(Bill bill) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BillDTO billDTO = modelMapper.map(bill, BillDTO.class);

        // return billTotal in billDTO
        double billTotal = 0;
        // cộng đơn hàng
        for (BillItemDTO billItemDTO : billDTO.getBillItems()) {
            billTotal += billItemDTO.getQuantity() * billItemDTO.getPrice();
        }
        // trừ khuyến mãi
        billTotal -= billDTO.getDiscountAmount();
        billDTO.setTotalBill(billTotal);

        return billDTO;
    }

    public PageDTO<BillCountByMonthDTO[]> countBillByMonthService() {
        int currentPage = 0, size = 10;
        PageRequest pageRequest = PageRequest.of(currentPage, size);
        Page<BillCountByMonthDTO[]> page = billRepo.countBillByMonth(pageRequest);

        return PageDTO.<BillCountByMonthDTO[]>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.getContent())
            .build();
    }

    public PageDTO<BillCountByCouponCodeDTO> countBillByCouponCodeService() {
        int currentPage = 0, size = 10;
        PageRequest pageRequest = PageRequest.of(currentPage, size);
        Page<BillCountByCouponCodeDTO> page = billRepo.countBillByCouponCode(pageRequest);

        return PageDTO.<BillCountByCouponCodeDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.getContent())
            .build();
    }

    public PageDTO<BillByUserTotalInvoiceDTO> billByUserTotalInvoiceService() {
        int currentPage = 0, size = 10;
        PageRequest pageRequest = PageRequest.of(currentPage, size);
        Page<BillByUserTotalInvoiceDTO> page = billRepo.billByUserTotalInvoice(pageRequest);

        return PageDTO.<BillByUserTotalInvoiceDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.getContent())
            .build();
    }

    public PageDTO<BillByUserWithProductDTO> billByUserWithProductService() {
        int currentPage = 0, size = 10;
        PageRequest pageRequest = PageRequest.of(currentPage, size);
        Page<BillByUserWithProductDTO> page =
            billRepo.billByUserWithProduct(pageRequest);

        return PageDTO.<BillByUserWithProductDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.getContent())
            .build();
    }

    public PageDTO<BillCountByProductBestSellerDTO> billCountByProductBestSellerService() {
        int currentPage = 0, size = 10;
        PageRequest pageRequest = PageRequest.of(currentPage, size/*, Sort.by("").descending()*/);
        Page<BillCountByProductBestSellerDTO> page =
            billRepo.billCountByProductBestSeller(pageRequest);

        return PageDTO.<BillCountByProductBestSellerDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.getContent())
            .build();
    }
}
