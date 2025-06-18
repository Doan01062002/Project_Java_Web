package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ra.edu.entity.Student;
import ra.edu.service.StudentService;

import java.util.List;

@Controller
@RequestMapping("/student_manager")
public class StudentManagerController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/show")
    public String showStudents(
            @RequestParam(name = "page",defaultValue = "1") int page,
            @RequestParam(name = "sortField",defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir",defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword",required = false) String keyword,
            Model model
    ) {
        int pageSize = 5;
        List<Student> students = studentService.findStudents(page, pageSize, sortField, sortDir, keyword);
        int total = studentService.countStudents(keyword);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        model.addAttribute("students", students);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "student_manager";
    }

    @PostMapping("/lock")
    public String lockStudent(@RequestParam("id") int id, @RequestParam("page") int page, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, @RequestParam(name = "keyword", required = false) String keyword) {
        studentService.lockOrUnlockStudent(id, false);
        return "redirect:/student_manager/show?page=" + page + "&sortField=" + sortField + "&sortDir=" + sortDir + (keyword != null ? "&keyword=" + keyword : "");
    }

    @PostMapping("/unlock")
    public String unlockStudent(@RequestParam("id") int id, @RequestParam("page") int page, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, @RequestParam(name = "keyword",required = false) String keyword) {
        studentService.lockOrUnlockStudent(id, true);
        return "redirect:/student_manager/show?page=" + page + "&sortField=" + sortField + "&sortDir=" + sortDir + (keyword != null ? "&keyword=" + keyword : "");
    }
}