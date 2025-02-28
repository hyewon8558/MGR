package com.MGR.controller;

import com.MGR.dto.MainTicketDto;
import com.MGR.dto.TicketFormDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.Ticket;
import com.MGR.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping(value = "/admin/ticket/new")
    public String ticketForm(Model model) {
        model.addAttribute("ticketFormDto", new TicketFormDto());
        return "ticket/ticketForm";
    }

    @PostMapping("/admin/ticket/new")
    public String ticketNew(@Valid TicketFormDto ticketFormDto, BindingResult bindingResult,
                            Model model, @RequestParam("ticketImgFile") List<MultipartFile> ticketImgFileList) {
        if (bindingResult.hasErrors()) {
            return "ticket/ticketForm";
        }
        if (ticketImgFileList.get(0).isEmpty() && ticketFormDto.getId() == null) {
            model.addAttribute("errorMessage", "티켓 이미지는 필수 입력 값입니다.");
            return "ticket/ticketForm";
        }
        try {
            ticketService.saveTicket(ticketFormDto, ticketImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "티켓 등록 중 에러가 발생하였습니다.");
            return "redirect:/admin/ticket/new"; // 폼을 다시 보여주기 위해 리다이렉트
        }
        return "redirect:/ticket"; // 성공 시 메인 페이지로 리다이렉트
    }

    @GetMapping(value = "/admin/ticket/{ticketId}")
    public String ticketDtl(@PathVariable("ticketId") Long ticketId, Model model) {
        try {
            TicketFormDto ticketFormDto = ticketService.getTicketDtl(ticketId);
            model.addAttribute("ticketFormDto", ticketFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 티켓입니다.");
            model.addAttribute("ticketFormDto", new TicketFormDto());
            return "ticket/ticketForm";
        }
        return "ticket/ticketForm";
    }

    @PostMapping(value = "/admin/ticket/{ticketId}")
    public String ticketUpdate(@Valid TicketFormDto ticketFormDto, BindingResult bindingResult,
                               Model model, @RequestParam("ticketImgFile") List<MultipartFile> ticketImgFileList
                             ) {
        if (bindingResult.hasErrors()) {
            return "ticket/ticketForm";
        }
        if (ticketImgFileList.get(0).isEmpty() && ticketFormDto.getId() == null) {
            model.addAttribute("errorMessage", "티켓 이미지는 필수 입력 값입니다.");
            return "ticket/ticketForm";
        }
        try {
            ticketService.updateTicket(ticketFormDto, ticketImgFileList);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "티켓 수정 중 오류가 발생했습니다.");
            return "ticket/ticketForm";
        }
        return "redirect:/ticket"; // 성공 시 메인 페이지로 리다이렉트
    }

    @GetMapping(value = {"/admin/tickets", "/admin/tickets/{page}"})
    public String ticketManage(TicketSearchDto ticketSearchDto,
                               @PathVariable("page") Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get():0, 3);
        Page<Ticket> tickets = ticketService.getAdminTicketPage(ticketSearchDto, pageable);
        model.addAttribute("tickets",tickets);
        model.addAttribute("ticketSearchDto", ticketSearchDto);
        model.addAttribute("maxPage",5);
        return "ticket/ticketMng";
    }

    @GetMapping(value="/ticket/{ticketId}")
    public String ticketDtl(Model model, @PathVariable("ticketId") Long ticketId){
        TicketFormDto ticketFormDto = ticketService.getTicketDtl(ticketId);
        model.addAttribute("ticket", ticketFormDto);

        return "ticket/ticketDtl";
    }
    @GetMapping(value="/ticket")
    public String ticketMain(TicketSearchDto ticketSearchDto,
                       Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainTicketDto> tickets = ticketService.getMainTicketPage(ticketSearchDto, pageable);

        model.addAttribute("tickets", tickets);
        model.addAttribute("ticketSearchDto", ticketSearchDto);
        model.addAttribute("maxPage", 5);

        return "ticket/ticketMain";
    }

}