package root.controller;

import root.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class ThymeleafControllerTest {

    private final BillService billService;

    @GetMapping("/test-bill-id")
    public String testBillId(Model model) {
        billService.getById(1L);
        model.addAttribute("billCreated", billService.getById(1L));
        return "email/bill/new-bill.html";
    }
}
