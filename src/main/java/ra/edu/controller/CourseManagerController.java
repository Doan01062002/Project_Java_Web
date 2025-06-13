package ra.edu.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.dto.CourseDTO;
import ra.edu.dto.StudentDTO;
import ra.edu.entity.Course;
import ra.edu.service.CourseService;

import java.io.IOException;
import java.util.Map;

@Controller
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 30
)
@RequestMapping("/course_manager")
public class CourseManagerController {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private CourseService courseService;

    @GetMapping("/show")
    public String showCourseManager(@RequestParam(name = "page", defaultValue = "1") int page,
                                    HttpSession session,
                                    Model model) {
        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !Boolean.TRUE.equals(loggedInUser.getRole())) {
            return "redirect:/login_form";
        }

        int pageSize = 5;
        long totalCourses = courseService.countTotalCourses();
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("listCourse", courseService.findAllByPage(page, pageSize));


        return "course_manager";
    }

    @GetMapping("/add")
    public String showFormAddCourse(Model model) {
        CourseDTO courseDTO = new CourseDTO();
        model.addAttribute("courseDTO", courseDTO);
        return "form_add_course";
    }

    @GetMapping("/cancel_form")
    public String cancelFormAddCourse(Model model) {
        return "redirect:/course_manager/show";
    }

    @PostMapping("/save")
    public String saveAddCourse(@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
                                BindingResult bindingResult,
                                Model model) {
        // ❗ Kiểm tra lỗi nhập liệu
        if (bindingResult.hasErrors()) {
            return "form_add_course";
        }

        // ✅ Kiểm tra trùng tên
        if (courseService.isCourseNameDuplicate(courseDTO.getName())) {
            bindingResult.rejectValue("name", "error.courseDTO", "Tên khoá học đã tồn tại");
            return "form_add_course";
        }

        MultipartFile fileImage = courseDTO.getImageFile();
        try {
            if (fileImage != null && !fileImage.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(fileImage.getBytes(), ObjectUtils.emptyMap());
                String url = uploadResult.get("url").toString();
                courseDTO.setImage(url);
            }
            courseService.addOrUpdateCourse(courseDTO);
        } catch (IOException exception) {
            model.addAttribute("error", "Lỗi upload ảnh: " + exception.getMessage());
            return "form_add_course";
        }

        return "redirect:/course_manager/show";
    }


    @GetMapping("/edit/{id}")
    public String showFormEditCourse(Model model, @PathVariable("id") int id) {
        Course course = courseService.getCourseById(id);
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setName(course.getName());
        courseDTO.setDuration(course.getDuration());
        courseDTO.setInstructor(course.getInstructor());
        courseDTO.setCreateAt(courseDTO.getCreateAt());
        courseDTO.setImage(course.getImage());

        model.addAttribute("courseDTO", courseDTO);
        return "form_edit_course";
    }

    @PostMapping("/save_edit_course")
    public String saveEditCourse(@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "form_edit_course";
        }

        // ✅ Kiểm tra trùng tên với khoá học khác
        if (courseService.isCourseNameDuplicate(courseDTO.getName(), courseDTO.getId())) {
            bindingResult.rejectValue("name", "error.courseDTO", "Tên khoá học đã tồn tại");
            return "form_edit_course";
        }

        MultipartFile fileImage = courseDTO.getImageFile();
        try {
            if (fileImage != null && !fileImage.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(fileImage.getBytes(), ObjectUtils.emptyMap());
                String url = uploadResult.get("url").toString();
                courseDTO.setImage(url);
            }
            courseService.addOrUpdateCourse(courseDTO);
        } catch (IOException exception) {
            model.addAttribute("error", "Lỗi upload ảnh: " + exception.getMessage());
            return "form_edit_course";
        }

        return "redirect:/course_manager/show";
    }



    @GetMapping("/delete_course/{id}")
    public String deleteCourse(@PathVariable("id") int id) {
        courseService.deleteCourse(courseService.getCourseById(id));
        return "redirect:/course_manager/show";
    }
}
