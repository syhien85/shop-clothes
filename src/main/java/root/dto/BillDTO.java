package root.dto;

import lombok.*;

import java.util.List;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillDTO extends TimeAuditableDTO {
    private Long id;
    private String status;
    private String couponCode;
    private Double discountAmount;
    private double totalBill;

    private UserDTO user;

    // JsonManagedReference: ngắt chỉ cho gọi 1 chiều: từ BillDTO lấy BillItemDTO
    // tránh trường hợp BillDTO và BillItemDTO tham chiếu lẫn nhau => loop vô hạn
    // phải có 2 annotation: @JsonManagedReference và @JsonBackReference, bên nào chính
    // dùng JsonBackReference
//    @JsonManagedReference
    private List<BillItemDTO> billItems;

    private PaymentDTO payment;
}
