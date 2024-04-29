package com.mheekotech.Paypal;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaypalController {

    private final PaypalService paypalService;

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @PostMapping
    public RedirectView createPayments(){
        try {
            String cancelUrl ="https://localhost:8080/payment/cancel";
            String successUrl ="https://localhost:8080/payment/success";

            Payment payment = paypalService.createPayment(10.00, "USD","paypal","Sale","new product",cancelUrl,successUrl);

            for(Links links: payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    return new RedirectView(links.getHref());
                }
            }
        }catch (PayPalRESTException e){
            log.error("An error occured::",e);
        }
        return  new RedirectView("/payment/error");
    }

    @PostMapping("/payment/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,  @RequestParam("payerId") String payerId){
            try {
                Payment payment = paypalService.executePayment(paymentId,payerId);
                if(payment.getState().equals("approved")){
                    return "paymentSuccess";
                }
            }catch (PayPalRESTException e){
                log.error("An error occured::",e);
            }
            return " paymentSuccess";
    }

    @PostMapping("/payment/cancel")
            public String paymentCancel(){
                return "paymentCancel";
    }

    @PostMapping("/payment/error")
    public String paymentError(){
        return "paymentError";
    }
}
